/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.fmservice.jee.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.activemq.broker.BrokerService;
import org.junit.*;
import org.mockito.Mockito;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.oss.fmservice.ejb.eventbus.listener.FMInstrumentedBean;
import com.ericsson.oss.fmservice.ejb.notification.OSSEventSender;
import com.ericsson.oss.mediation.translator.model.Constants;
import com.ericsson.oss.services.fm.service.alarm.AlarmNotification;

public class TestOSSEventSender {

	/*@Mock
	private SystemRecorder mockRecorder;*/
	
	private FMInstrumentedBean fmBean;

	private static BrokerService brokerSvc;
	private static String testIP = "localhost";
	private static String testPort = "61616";
	private static final String queueName = "FMService_queue";
	public static Notification[] resultNotification;
	private static OSSEventSender eventSender;
	private static AlarmNotification notification;
	private final static SimpleDateFormat timeformat = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	@BeforeClass
	public static void testSetup() throws Exception {
		brokerSvc = new BrokerService();
		brokerSvc.setBrokerName("TestBroker");
		brokerSvc.addConnector("tcp://" + testIP + ":" + testPort);
		brokerSvc.start();
		final QueueListener listner = new QueueListener();
		QueueListener.mockReceiver(listner, queueName);
	}
   

	// @Test(expected = JMSException.class)
	public void testJMSException() throws Exception {
		setTestIP("duplicateIP");
		setUp();
		eventSender.fmQueueSend(notification);

	}
	
	@Test
	public void testConnections() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setTestIP("localhost");
		setUp();
		final OSSEventSender sender = Mockito.spy(eventSender);
		sender.fmQueueSend(notification);
		verify(sender, times(1)).connectToOSS();
		verify(sender, times(1)).reconnect();
		verify(sender, times(1)).reconnectToOSS();
		verify(sender, times(1)).fmQueueCloseConn();
	}

	@AfterClass
	public static void cleanUpClass() throws Exception  {

		if (brokerSvc != null) {
			brokerSvc.stop();
		}
		QueueListener.consumer.close();
		QueueListener.session.close();
		QueueListener.connection_torip.close();

	}

	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {

		/*final Logger mockingLogger = LoggerFactory
				.getLogger(TestOSSEventSender.class);*/

		eventSender = new OSSEventSender();
		fmBean = new FMInstrumentedBean();
		
		final Field fmIsBean = OSSEventSender.class.getDeclaredField("instBean");
		fmIsBean.setAccessible(true);
		fmIsBean.set(eventSender, fmBean);


		final Field mockQueue = OSSEventSender.class.getDeclaredField("subject");
		mockQueue.setAccessible(true);
		mockQueue.set(eventSender, queueName);

		final Field ossIpField = OSSEventSender.class.getDeclaredField("ossIP");
		ossIpField.setAccessible(true);
		ossIpField.set(eventSender, testIP);

/*		final Field mockLogger = OSSEventSender.class.getDeclaredField("logger");
		mockLogger.setAccessible(true);
		mockLogger.set(eventSender, mockingLogger);*/

		final Field ossPort = OSSEventSender.class.getDeclaredField("ossPort");
		ossPort.setAccessible(true);
		ossPort.set(eventSender, testPort);
		notification = makeNotification();

	}

	public AlarmNotification makeNotification() {

		final AlarmNotification notification = new AlarmNotification();
		notification.setEventAgentId("eventAgentId");
		notification.setEventType("eventType");
		notification.setNodeAddress("nodeAddress");
		notification.setNumId(0);
		notification.setOtherAttributes("otherAttributes");
		notification.setPerceivedSeverity(Constants.SEV_CRITICAL);
		notification.setProbableCause("probableCause");
		notification.setSourceType("sourceType");
		notification.setSpecificProblem("specificProblem");
		notification.setStringId("stringId");
		notification.setTheTime(timeformat.format(new Date()));
		notification.setTimeZone("timeZone");
		return notification;
	}

	public static String getTestIP() {
		return testIP;
	}

	public static void setTestIP(final String testIP) {
		TestOSSEventSender.testIP = testIP;
	}

	public static String getTestPort() {
		return testPort;
	}

	public static void setTestPort(final String testPort) {
		TestOSSEventSender.testPort = testPort;
	}

}
