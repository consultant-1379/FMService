package com.ericsson.oss.fmservice.ejb.registration;

import java.net.*;

import javax.annotation.*;
import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;

import com.ericsson.oss.fmservice.ejb.eventbus.listener.FMInstrumentedBean;
import com.ericsson.oss.fmservice.ejb.notification.OSSEventSender;
import com.ericsson.nms.fm.fm_communicator.*;

//import com.ericsson.oss.fmservice.configuration.*;

/**
 * @author tcsnahi This bean sends IP of FM Service to FM Communicator when this
 *         instance of FM Service becomes master
 * 
 */

@Singleton
@Startup
public class FMServiceIdentitySender {

	@Inject
	private FMClusterListener clusterListener;

	@Inject
	private Logger logger;

	@Resource(lookup = "java:module/ModuleName")
	private String moduleName;

	@Resource(lookup = "java:app/AppName")
	private String appName;

	private static String idQueueName = "FMSIdQueue";

	@Resource
	private TimerService timerService;

	@Inject
	private OSSEventSender ossEventSender;
	
	@Inject
	private FMInstrumentedBean fmIsBean;

	/*
	 * The below attributes need not be saved for future sessions. For every
	 * session, the below attributes have to be re-initialized, and hence the
	 * EJB is not @Stateful, but kept as @Stateless
	 */
	private ActiveMQConnection connection = null;
	private Session queueSession = null;
	private MessageProducer queueProducer = null;
	private static String localIP;
	private Timer ossNotifTimer;
	private final static int MAX_RETRY = 3;
	private static int retryCount =0;
	private static boolean initial = true;

	@PostConstruct
	public void init() {

		/**
		 * TODO: EventId/ErrorId will be declared in seperate file.
		 * SystemRecorder will be wrapped by local EJB.
		 */

		createStartUpTimer();
	}

	@Timeout
	public void timeout(final Timer startUpTimer) {
		try{
			if (clusterListener.getMasterState()) {
				if ((localIP == null) || "".equals(localIP)) {
					localIP = getBindingAddress();
					logger.debug("Binding Address : "+localIP);
				}
				if(initial){
					createConnections();

					initial = false;
				}else if (!(retryCount < MAX_RETRY)){
					retryCount=0;
					logger.debug("Exceeded number of retries, New connections will be created");
					if(connection != null){	
						connection.close();
						connection = null;
					}


					if(queueSession != null){		
						queueSession.close();
						queueSession = null;
					}

					if(queueProducer != null){	
						queueProducer.close();
						queueProducer = null;
					}

					createConnections();
					if(fmIsBean.getConnectionStatus()==1){
						fmIsBean.decreaseConnectionStatus();
					}

				}
				final FMSInfo fmsInfo = new FMSInfo();
				fmsInfo.setIp(localIP);
				fmsInfo.setLookUp("ejb:"
						+ appName
						+ "/"
						+ moduleName
						+ "/FMServiceImpl!com.ericsson.nms.fm.fm_communicator.FMServiceRemote");
				final ObjectMessage message = queueSession.createObjectMessage();
				message.setObject(fmsInfo);

				// send(Message message,int deliveryMode, int priority, long
				// timeToLive)
				// Here Priority is 4 and time to live is 0 (In milliseconds)
				queueProducer.send(message, DeliveryMode.PERSISTENT, 4, 0);
				logger.debug(
						"FM Service Information sent to FM Communicator {} {} ",
						localIP, fmsInfo.getLookUp());
			} else {
				logger.debug("current FM instance is not master yet. Exiting from timeout");
			}

		}
		catch(Exception e){
			
			if(!(fmIsBean.getConnectionStatus()==1)){
			fmIsBean.increaseConnectionStatus();
			}
			
			logger.error("Will Try Again::Exception in sending the FMService Identity to Communicator"+e.toString());
			retryCount++;
		}
	}

	/**
	 * @return Binding IP Address of the JBOSS server
	 */

	public String getBindingAddress() throws UnknownHostException {
		String finalListeningIP = System.getProperty("jboss.bind.address");

		if (finalListeningIP != null) {
			if (finalListeningIP.equalsIgnoreCase("0.0.0.0")
					|| finalListeningIP.equalsIgnoreCase("127.0.0.1")) {
				finalListeningIP = InetAddress.getLocalHost().getHostAddress()
						.toString();
			}
		} else {
			finalListeningIP = InetAddress.getLocalHost().getHostAddress()
					.toString();
		}
		return finalListeningIP;
		//return "172.16.65.7";
	}
	
	private void createConnections() throws JMSException, URISyntaxException{
		final String ossIP = ossEventSender.getOSS_IP();
		final String ossPort = ossEventSender.getOSS_PORT();


		connection = ActiveMQConnection.makeConnection("tcp://" + ossIP
				+ ":" + ossPort);
		logger.debug("New connection is created");

		connection.start();
		logger.trace("Active MQconenction:"+connection);
		queueSession = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);

		final Destination destination = queueSession
				.createQueue(idQueueName);

		queueProducer = queueSession.createProducer(destination);
	}

	public void cancelTimer() {
		if (ossNotifTimer != null) {
			ossNotifTimer.cancel();
		}
		logger.debug("OSS Notification Timer Canceled");
	}

	public void createStartUpTimer() {
		cancelTimer();
		if (timerService != null) {

					final TimerConfig timerConfig = new TimerConfig();
								timerConfig.setPersistent(false);
											ossNotifTimer = timerService.createIntervalTimer(10000, 6000,
																timerConfig);
																		
		}
		logger.debug("Created Startup timer - {} ");

	}

	@PreDestroy
	public void cleanUp() {
		cancelTimer();
		closeQueueConn();
	}

	public void closeQueueConn() {

		try {
			if (connection != null) {
				connection.close();
				connection = null;
				logger.trace("Connection closed:"+connection);
			}
			if (queueSession != null) {
				queueSession.close();
				queueSession = null;
				logger.trace("Session closed :"+queueSession);
			}
			if (queueProducer != null) {
				queueProducer.close();
				queueProducer = null;
				logger.trace("Producer closed :"+queueProducer);
			}
			logger.debug("Connection closed to  ", idQueueName);
		} catch (JMSException e) {
			logger.error("Exception occured while closing connection to "
					+ idQueueName + e.getMessage());
		}
	}

}
