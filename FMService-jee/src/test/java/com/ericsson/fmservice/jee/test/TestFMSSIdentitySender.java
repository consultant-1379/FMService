package com.ericsson.fmservice.jee.test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.InetAddress;

import javax.annotation.Resource;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.fmservice.ejb.eventbus.listener.FMInstrumentedBean;
import com.ericsson.oss.fmservice.ejb.notification.OSSEventSender;
import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.fmservice.ejb.registration.FMServiceIdentitySender;
import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;

public class TestFMSSIdentitySender {


	private Timer mockTimer;
	private FMInstrumentedBean fmBean;


	@Resource
	private TimerService mockTimerService;
	
	private static BrokerService brokerSvc;
	private static String testIP = "localhost";
	private static String result;
	private static FMServiceIdentitySender idSender;

	private static final String appName = "testApp";
	private static final String modName = "testModule";
	private static MembershipChangeEvent mockMCE;
	private static final String queueName = "FMSIdQueue";
	FMServiceIdentitySender spySender;

	@BeforeClass
	public static void testSetup() throws Exception {

		brokerSvc = new BrokerService();
		brokerSvc.setBrokerName("TestBroker");
		brokerSvc.addConnector("tcp://" + testIP + ":" + testPort);
		brokerSvc.start();
		final QueueListener listner = new QueueListener();
		QueueListener.mockReceiver(listner, queueName);
		mockMCE = mock(MembershipChangeEvent.class);
	}

	@Test
	public void testOnLocalHost() throws Exception {
		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		idSender.timeout(mockTimer);
		Assert.assertEquals(
				InetAddress.getLocalHost().getHostAddress().toString()
						+ "ejb"
						+ ":"
						+ appName
						+ "/"
						+ modName
						+ "/FMServiceImpl!com.ericsson.nms.fm.fm_communicator.FMServiceRemote",
				result);
	}

	@Test
	public void testIsMasterFalse() throws Exception {
		when(mockMCE.isMaster()).thenReturn(false);
		setUp();
		final FMServiceIdentitySender mockSender= Mockito.spy(idSender);
		mockSender.timeout(mockTimer);
		verify(mockSender, never()).getBindingAddress();
	}

	@Test
	public void testJMSException() throws Exception {
		when(mockMCE.isMaster()).thenReturn(true);
		setTestIP("duplicateIP");
		setUp();
		idSender.timeout(mockTimer);
	}

	@Test
	public void testLocalIp1() throws Exception {
		System.setProperty("jboss.bind.address", "127.0.0.1");
		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		idSender.timeout(mockTimer);
		Assert.assertEquals(
				InetAddress.getLocalHost().getHostAddress().toString()
						+ "ejb"
						+ ":"
						+ appName
						+ "/"
						+ modName
						+ "/FMServiceImpl!com.ericsson.nms.fm.fm_communicator.FMServiceRemote",
				result);
	}

	@Test
	public void testLocalIp2() throws Exception {
		System.setProperty("jboss.bind.address", "0.0.0.0");
		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		idSender.timeout(mockTimer);
		Assert.assertEquals(
				InetAddress.getLocalHost().getHostAddress().toString()
						+ "ejb"
						+ ":"
						+ appName
						+ "/"
						+ modName
						+ "/FMServiceImpl!com.ericsson.nms.fm.fm_communicator.FMServiceRemote",
				result);
	}

	@Test
	public void testInit() throws Exception {

		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		spySender.init();
		verify(spySender, times(1)).createStartUpTimer();
		
	}

	@Test
	public void testgetBindingAddress() throws Exception {

		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		spySender.timeout(mockTimer);
		verify(spySender, times(1)).getBindingAddress();
	}

	@Test
	public void testCreateStartUpTimer() throws Exception{
		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		spySender.createStartUpTimer();
		verify(spySender, times(1)).cancelTimer();
	}
	@Test 
	public void testCleanUp() throws Exception{
		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		spySender.cleanUp();
		verify(spySender, times(1)).cancelTimer();
		verify(spySender, times(1)).closeQueueConn();
	}
	@Test
	public void testLocalIpNull() throws Exception {

		setTestIP("localhost");
		when(mockMCE.isMaster()).thenReturn(true);
		setUp();
		idSender.timeout(mockTimer);
		Assert.assertEquals(
				InetAddress.getLocalHost().getHostAddress().toString()
						+ "ejb"
						+ ":"
						+ appName
						+ "/"
						+ modName
						+ "/FMServiceImpl!com.ericsson.nms.fm.fm_communicator.FMServiceRemote",
				result);
	}

	@AfterClass
	public static void cleanUpClass() throws Exception   {

		if (brokerSvc != null) {
			brokerSvc.stop();
		}
		QueueListener.consumer.close();
		QueueListener.session.close();
		QueueListener.connection_torip.close();
	}

	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		MockitoAnnotations.initMocks(this);

		idSender = new FMServiceIdentitySender();
		fmBean = new FMInstrumentedBean();
		
		final Field fmIsBean = FMServiceIdentitySender.class.getDeclaredField("fmIsBean");
		fmIsBean.setAccessible(true);
		fmIsBean.set(idSender, fmBean);
		
		
		final Field ossTimerService = FMServiceIdentitySender.class.getDeclaredField("timerService");
		ossTimerService.setAccessible(true);
		ossTimerService.set(idSender, mockTimerService);
		
		
		final Field ossTimer = FMServiceIdentitySender.class.getDeclaredField("ossNotifTimer");
		ossTimer.setAccessible(true);
		ossTimer.set(idSender, mockTimer);
		
		
		final FMClusterListener clusterListener = new FMClusterListener();
		final OSSEventSender eventSender = new OSSEventSender();

		final Field ossIpField = OSSEventSender.class.getDeclaredField("ossIP");
		ossIpField.setAccessible(true);
		ossIpField.set(eventSender, getTestip());

		final Field ossPort = OSSEventSender.class.getDeclaredField("ossPort");
		ossPort.setAccessible(true);
		ossPort.set(eventSender, getTestport());

		final Logger mockingLoggerCluster = LoggerFactory
				.getLogger(FMClusterListener.class);

		final Field mockLoggerListener = FMClusterListener.class
				.getDeclaredField("logger");
		mockLoggerListener.setAccessible(true);
		mockLoggerListener.set(clusterListener, mockingLoggerCluster);

		clusterListener.listenForMembershipChange(mockMCE);

		final Field mockLocalIp = FMServiceIdentitySender.class
				.getDeclaredField("localIP");
		mockLocalIp.setAccessible(true);
		mockLocalIp.set(idSender, null);

		final Logger mockingLogger = LoggerFactory
				.getLogger(TestFMSSIdentitySender.class);


		final Field ossQueue = FMServiceIdentitySender.class
				.getDeclaredField("idQueueName");
		ossQueue.setAccessible(true);
		ossQueue.set(idSender, queueName);

		final Field mockLogger = FMServiceIdentitySender.class
				.getDeclaredField("logger");
		mockLogger.setAccessible(true);
		mockLogger.set(idSender, mockingLogger);

		final Field cluster = FMServiceIdentitySender.class
				.getDeclaredField("clusterListener");
		cluster.setAccessible(true);
		cluster.set(idSender, clusterListener);

		final Field sender = FMServiceIdentitySender.class
				.getDeclaredField("ossEventSender");
		sender.setAccessible(true);
		sender.set(idSender, eventSender);

		final Field mockModuleName = FMServiceIdentitySender.class
				.getDeclaredField("moduleName");
		mockModuleName.setAccessible(true);
		mockModuleName.set(idSender, modName);

		final Field mockAppName = FMServiceIdentitySender.class
				.getDeclaredField("appName");
		mockAppName.setAccessible(true);
		mockAppName.set(idSender, appName);
		spySender = Mockito.spy(idSender);

	}

	public static void setTestIP(final String testIP) {
		TestFMSSIdentitySender.testIP = testIP;
	}

	private static String testPort = "61616";

	public static BrokerService getBrokerSvc() {
		return brokerSvc;
	}

	public static void setBrokerSvc(final BrokerService brokerSvc) {
		TestFMSSIdentitySender.brokerSvc = brokerSvc;
	}

	public Timer getMockTimer() {
		return mockTimer;
	}

	public void setMockTimer(final Timer mockTimer) {
		this.mockTimer = mockTimer;
	}

	public static FMServiceIdentitySender getIdSender() {
		return idSender;
	}

	public static void setIdSender(final FMServiceIdentitySender idSender) {
		TestFMSSIdentitySender.idSender = idSender;
	}

	public static String getTestip() {
		return testIP;
	}

	public static String getTestport() {
		return testPort;
	}

	public static String getResult() {
		return result;
	}

	public static String getAppname() {
		return appName;
	}

	public static String getModname() {
		return modName;
	}

	public static void setResult(final String res) {
		result = res;
	}
}