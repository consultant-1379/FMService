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
package com.ericsson.oss.fmservice.ejb.notification;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.oss.fmservice.ejb.eventbus.listener.FMInstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.fm.service.alarm.AlarmSyncEndNotification;
import com.ericsson.oss.services.fm.service.alarm.FmMediationNotification;

/**
 * @author tcsbosr
 * 
 */

@Stateless
public class OSSEventSender {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OSSEventSender.class);
	/**
	 * 
	 * Active MQ Connection objects with OSS
	 * 
	 */
	private ActiveMQConnection connection = null;
	private Session session = null;
	private MessageProducer producer = null;

	static private String subject = "FMService_queue";
	static private String ossIP = "masterservice";
	static private String ossPort = "50057";
	private static int alarmSyncNotificationCount;
	private static int notificationCount;
	static private boolean timerCreated = false;
	static private Timer alarmTimer;

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the oSS_IP
	 */
	public String getOSS_IP() {
		return ossIP;
	}

	/**
	 * @return the oSS_PORT
	 */
	public String getOSS_PORT() {
		return ossPort;
	}

	@Inject
	private SystemRecorder systemRecorder;

	@Inject
	private FMInstrumentedBean instBean;

	@Resource
	private TimerService timerService;

	/**
	 * @param Object
	 *            Notification received from event bus
	 * 
	 */
	@Profiled
	public void fmQueueSend(
			final FmMediationNotification fmMediationNotification) {
		Notification[] notificationArray = null;
		ObjectMessage message = null;
		try {

			connectToOSS();
			message = session.createObjectMessage();

			if (fmMediationNotification instanceof AlarmSyncEndNotification) {
				if (!timerCreated) {
					createAlarmTimer();
					timerCreated = true;
				}
				notificationArray = ((AlarmSyncEndNotification) fmMediationNotification)
						.getNotificationsForOSS();
				for (int index = 0; index < notificationArray.length; index++) {
					LOGGER.debug("SyncnotificationArray[" + index + "]="
							+ notificationArray[index]);
				}

				alarmSyncNotificationCount += notificationArray.length;

			} else {
				if (!timerCreated) {
					createAlarmTimer();
					timerCreated = true;
				}


				final Notification notif = fmMediationNotification
						.getNotificationForOSS();
				notificationArray = new Notification[1];

				notificationArray[0] = notif;
				LOGGER.debug("notificationArray[0]" + notificationArray[0]);
				notificationCount += notificationArray.length;





				instBean.increaseminalarmCount();

			}
			message.setObject(notificationArray);
			producer.send(message);
			LOGGER.debug("Current alarmSyncNotificationCount is"
					+ alarmSyncNotificationCount + "and notificationCount is"
					+ notificationCount);
		} catch (Exception e) {

			if (!(fmMediationNotification instanceof AlarmSyncEndNotification)) {
				LOGGER.error("Exception in sending the alarm to communicator, So calling the increaseFailedAlarms");
				instBean.increaseminFailedAlarms();
			}
			LOGGER.error("Exception occured while sending notification to FMCommunicator : SO, Old connection will be closed -"
					+ e.getMessage());
			fmQueueCloseConn();
			reconnect();
			sendFailedAlarm(message);

			systemRecorder
			.recordError(
					"Exception occured while sending notification to FMCommunicator",
					ErrorSeverity.MAJOR, "FMService OSSEventSender",
					notificationArray[0].getManagedObjectInstance(),
					"Exception occured while sending notification to FMCommunicator: "
							+ e.getMessage());
		}

	}

	private void sendFailedAlarm(final ObjectMessage message){
		try{
			LOGGER.info("Tryying to send the failed alarms to FMCommunicator Again");
			producer.send(message);
		}catch(Exception e){
			LOGGER.error("Exception in sending the failed alarm::",e.toString());
		}

	}

	public void fmQueueCloseConn() {

		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (session != null) {
				session.close();
				session = null;
			}
			if (producer != null) {
				producer.close();
				producer = null;
			}

			LOGGER.debug("Connection closed to  ", subject);
		} catch (JMSException e) {
			LOGGER.error("Exception occured while closing connection to "
					+ subject + e.getMessage());

			systemRecorder.recordError(
					"Exception occured while closing connection",
					ErrorSeverity.MAJOR, "FMService OSSEventSender",
					"FMService OSSEventSender",
					"Exception occured while closing connection to " + subject+" "
							+ e.getMessage());
		}

	}

	@PreDestroy
	public void cleanUp(){
		try{
			fmQueueCloseConn();
			cancelTimer();
		}catch(Exception ex){
			LOGGER.error("Exception in cleanUp method:: {}",ex.toString());
		}
	}

	public void reconnect() {

		try {
			LOGGER.debug("Trying to reconnect ...");

			connection = ActiveMQConnection.makeConnection("tcp://" + ossIP
					+ ":" + ossPort);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			final Destination destination = session.createQueue(subject);
			producer = session.createProducer(destination);
		} catch (Exception e) {
			LOGGER.error("Failed to reconnect - " + e.getMessage());
		}

	}

	@Lock(javax.ejb.LockType.WRITE)
	public void reconnectToOSS() {
		if (producer == null || connection == null || session == null) {
			fmQueueCloseConn();
			reconnect();
		}
	}

	public void connectToOSS() {
		if (producer == null || connection == null || session == null) {
			reconnectToOSS();
		}
	}

	private void cancelTimer() {
		if (alarmTimer != null) {
			alarmTimer.cancel();
		}
		LOGGER.debug("Alarm Timer Canceled");
	}

	private void createAlarmTimer() {
		cancelTimer();
		if (timerService != null) {

			final TimerConfig timerConfig = new TimerConfig();
			timerConfig.setPersistent(false);
			alarmTimer = timerService.createIntervalTimer(10000, 60000,
					timerConfig);

		}
		LOGGER.debug("Created Startup timer - {} ");

	}

	@Timeout
	public void timeout(final Timer startUpTimer) {
		LOGGER.debug("Timeout Method called, Re-setting the number of alarms");
		instBean.resetminalarmCount();

		LOGGER.debug("Timeout Method called, Re-setting the number failed of alarms");
		instBean.resetminFailedCount();
	}

}
