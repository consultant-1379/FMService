package com.ericsson.fmservice.jee.test;

import java.net.URISyntaxException;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import com.ericsson.nms.fm.fm_communicator.*;

public class QueueListener implements MessageListener {

	public static String torIP = null;
	public static String torLookUp = null;
	public static QueueListener instance;
	public static ActiveMQConnection connection_torip;
	public static Session session;
	public static Destination destination;
	public static MessageConsumer consumer;

	public QueueListener() {
		instance = this;
	}

	/**
	 * Listen to queue FMSIdQueue to receive IP address of presently activated
	 * FM Service instance
	 * 
	 * @param Instance
	 *            of QueueListner
	 */
	public static void mockReceiver(final QueueListener listener, final String queueName)
			throws JMSException, URISyntaxException {

		connection_torip = ActiveMQConnection
				.makeConnection("tcp://localhost:61616");
		connection_torip.start();
		session = connection_torip.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queueName);
		consumer = session.createConsumer(destination);
		consumer.setMessageListener(listener);
	}

	@Override
	public void onMessage(final Message message) {

		try {
			if (message instanceof ObjectMessage) {
				final ObjectMessage objectMessage = (ObjectMessage) message;
				if (objectMessage.getObject().getClass() == FMSInfo.class) {
					final FMSInfo fmsInfo = (FMSInfo) objectMessage.getObject();
					torIP = fmsInfo.getIp();
					torLookUp = fmsInfo.getLookUp();
					TestFMSSIdentitySender.setResult(torIP + torLookUp);
				} else if (objectMessage.getObject().getClass() == Notification[].class) {
					final 	Notification[] notification = (Notification[]) objectMessage
							.getObject();
					TestOSSEventSender.resultNotification = notification;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
