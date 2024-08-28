package com.ericsson.oss.fmservice.ejb.eventbus.listener;

import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.fmservice.ejb.constants.Constants;
import com.ericsson.oss.fmservice.ejb.dpsinterface.FmDPSHandler;
import com.ericsson.oss.fmservice.ejb.notification.OSSEventSender;
import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.itpf.datalayer.datapersistence.api.ManagedObject;
import com.ericsson.oss.itpf.datalayer.datapersistence.api.PersistentObject;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Consumes;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.mediation.core.events.MediationTaskRequest;
import com.ericsson.oss.mediation.translator.model.EventNotification;
import com.ericsson.oss.services.fm.service.alarm.FmMediationNotification;
import com.ericsson.oss.services.fm.service.model.FmMediationAlarmSyncRequest;
import com.ericsson.oss.services.fm.service.model.FmMediationSupervisionRequest;
import com.ericsson.oss.services.fm.service.util.ConvertEventNotificationToAlarm;
import com.ericsson.oss.services.fm.service.util.DPSConstants;
import com.ericsson.oss.services.fm.service.util.FDNConverter;

/**
 * @author tcsbosr
 * 
 */
@InstrumentedBean
@ApplicationScoped
public class ReceiveModeledEvent {

	@Inject
	private FmDPSHandler dpsInterface;

	@Inject
	private FMClusterListener fmclusterListener;

	@Inject
	private Logger logger;

	@Inject
	private OSSEventSender modeledEventSender;
	
	@Inject
	private SystemRecorder systemRecorder;

	@Inject
	@Modeled
	private EventSender<MediationTaskRequest> fmMediationRequestEventSender;
	

	/**
	 * @param alarm
	 */
	@Profiled
	public void receiveSpecificEvent(
			@Observes @Modeled final FmMediationNotification mediationNotification) {
		if(fmclusterListener.getMasterState()) {
		logger.debug("MediationNotification received in master from the channel. {}",
				mediationNotification);

		modeledEventSender.fmQueueSend(mediationNotification);
		} else {
			logger.debug("The current FMService is not master, So not processing the Notification");
		}

	}

	public void receiveFailoverEvent(
			@Observes @Consumes(endpoint = Constants.HANDOVERTOPIC) final String failedMS) {
		if(fmclusterListener.getMasterState()){
			logger.info("Got failover event for Mediation Service with key {}",failedMS);
			if (failedMS.contains("MediationServiceConsumer_1")
					||failedMS.contains("MediationServiceConsumer_0")||failedMS.contains(Constants.FMMSREGFAILED)) {				
				sendNotifications(failedMS);
				logger.debug("Sending Failover Notifications is done");
			}else{
				logger.debug("This fmservice instance is not master at present so relaxing");
			}
		}

	}

	/**
	 * SendNotifications for all FM NEs in DPS
	 */
	public void sendNotifications(final String failedMS) {
		try{
			final List<String>failovernodelist=new ArrayList<String>();
			final List<String> hbClearList = new ArrayList<String>();
			boolean issupervision = false;
			final Collection<PersistentObject> p = dpsInterface.getDpManager().findByType("FmAccess");
			for (PersistentObject nInfo : p) { 
				final ManagedObject m =(ManagedObject) nInfo;
				final String nodeFdn=m.getFDN();
				final Map<String, Object> nodeAttrs = dpsInterface.getSupervisionStatus(nodeFdn);
				
				if (nodeAttrs.containsKey(DPSConstants.ALARM_SUPERVISION)) {
                    issupervision = (boolean) nodeAttrs.get(DPSConstants.ALARM_SUPERVISION);
                }
				
				final String heartbeatStatus = (String) nodeAttrs.get(Constants.HEARTBEAT_STATUS);

                logger.debug("The nodeFdn is: {} and the supervision status is: {}", nodeFdn, issupervision);
                if (issupervision) {
                    failovernodelist.add(nodeFdn);
                    if (heartbeatStatus != null && heartbeatStatus.equals(Constants.STATE_FAILED)) {
                        hbClearList.add(nodeFdn);
                    }
                }
			}
			logger.info("The size of failover node list is:"+failovernodelist.size());
			logger.info("The node list for which supervision is on at time of failover is"+failovernodelist.toString());
			
			logger.info("The size of HB clear node list is:" + hbClearList.size());
            logger.info("The node list for which HB clear needs to be sent during failover is:" + hbClearList.toString());
			
			for (int fdnCount = 0; fdnCount < hbClearList.size(); fdnCount++) {
                sendHbClearAlarm(hbClearList.get(fdnCount), Constants.NODESOURCETYPE);
            }
			
			for(int fdnCount=0;fdnCount<failovernodelist.size();fdnCount++){
				sendStartSupervision(failovernodelist.get(fdnCount),failedMS);
			}
			
			for (int fdnCount = 0; fdnCount < failovernodelist.size(); fdnCount++) {
				sendStartSync(failovernodelist.get(fdnCount),failedMS);
				sendSyncStartNotif(failovernodelist.get(fdnCount), Constants.NODESOURCETYPE);
			}
		}catch (Exception e) {
			logger.error("Exception in sendNotifications:"+e.toString());
		}

	}

	private void sendStartSupervision(final String failOverEvent,final String failedMS){
		logger.debug("Failover sendStartSupervision called for fdn:"+failOverEvent);
		try {

			final FmMediationSupervisionRequest supervisionRequest = new FmMediationSupervisionRequest(
					failOverEvent, Constants.getTimeString(), true);
			
			if(failedMS.contains("MediationServiceConsumer_0")){
				logger.debug("Sending the mediation request to mediation core:{}",Constants.MEDFMTWO_CHANNEL);
				fmMediationRequestEventSender.send(supervisionRequest,Constants.MEDFMTWO_CHANNEL);
			}else{
				logger.debug("Sending the mediation request to mediation core:{}",Constants.MEDFMONE_CHANNEL);
				fmMediationRequestEventSender.send(supervisionRequest,Constants.MEDFMONE_CHANNEL);
			}
		}
		catch (Exception exception) {
			logger.error("Error in FMService sendStartSupervision {}",exception.getLocalizedMessage());
		}

		systemRecorder.recordEvent("ReceiveModeledEvent:",
				EventLevel.DETAILED, "sendStartSupervision sent for FDN", failOverEvent,
				"in ReceiveModeledEvent");

	}

	private void sendStartSync(final String fdn,final String failedMS){
		try{
			logger.debug("sendStartSync called for sourceType: {} and TOR fdn {}",fdn);

			final FmMediationAlarmSyncRequest fmCorbaMediationAlarmSyncRequest = new FmMediationAlarmSyncRequest();
			fmCorbaMediationAlarmSyncRequest.setProtocolInfo("FM");
			fmCorbaMediationAlarmSyncRequest.setNodeAddress(fdn);
			fmCorbaMediationAlarmSyncRequest.setJobId(Constants.getTimeString());
			
			if(failedMS.contains("MediationServiceConsumer_0")){
				logger.debug("Sending the mediation request to mediation core: {}",Constants.MEDFMTWO_CHANNEL);
				fmMediationRequestEventSender.send(fmCorbaMediationAlarmSyncRequest,Constants.MEDFMTWO_CHANNEL);
			}else{
				logger.debug("Sending the mediation request to mediation core:{}",Constants.MEDFMONE_CHANNEL);
				fmMediationRequestEventSender.send(fmCorbaMediationAlarmSyncRequest,Constants.MEDFMONE_CHANNEL);
			}
			
		}
		catch (final Exception exception) {
			logger.error("Error in FMService sendStartSync: {}"+exception.getLocalizedMessage());
		}

		systemRecorder.recordEvent("ReceiveModeledEvent:",
				EventLevel.DETAILED, "sendStartSync sent for FDN", fdn,
				"in ReceiveModeledEvent");

	}

	private void sendSyncStartNotif(final String fdn,final String sourceType)
	{
		logger.debug("sendSyncStartNotif called for sourceType: {} and TOR fdn {}",fdn,sourceType);
		EventNotification syncStart = new EventNotification();
		String ossFdn="";
		try{
			if(fdn.contains(Constants.CORBA_FDNSTR)){
				ossFdn=fdn.replace(Constants.CORBA_FDNSTR, "");
			}
			ossFdn = FDNConverter.convertTorFdnToOssFdn(ossFdn);
			syncStart= EventNotification.createSynchronizationStartNotification(ossFdn, sourceType);
			final FmMediationNotification syncStartNotif = ConvertEventNotificationToAlarm.converEventNotificationToAlarmNotification(syncStart);

			if(syncStart!=null){
				logger.debug("Sending the Synch start Notification to FM Queue Sender: {}",syncStartNotif.toString());
				modeledEventSender.fmQueueSend(syncStartNotif);
			}
		}
		catch(Exception e){
			logger.error("Exception in sending the Synch start Notification : {}",e.toString());
		}
	}
	
	private void sendHbClearAlarm(final String fdn, final String sourceType) {
        logger.debug("sendHbClearAlarm called for fdn {}", fdn);
        String ossFdn = "";
        if (fdn.contains(Constants.CORBA_FDNSTR)) {
            ossFdn = fdn.replace(Constants.CORBA_FDNSTR, "");
        }
        ossFdn = FDNConverter.convertTorFdnToOssFdn(ossFdn);
        final EventNotification hbClear = EventNotification.createHeartbeatAlarmClearing(fdn, sourceType, "Heartbeat clear Alarm");
        final FmMediationNotification hbClearNotif = ConvertEventNotificationToAlarm.converEventNotificationToAlarmNotification(hbClear);
        if (hbClearNotif != null) {
            logger.debug("Sending the HB clear Notification to FM Queue Sender: {}", hbClearNotif.toString());
            modeledEventSender.fmQueueSend(hbClearNotif);
        }
    }
	
	
}
