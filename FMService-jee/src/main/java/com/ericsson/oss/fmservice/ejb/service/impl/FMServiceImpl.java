package com.ericsson.oss.fmservice.ejb.service.impl;

import java.util.*;

import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.oss.fmservice.ejb.constants.Constants;
import com.ericsson.oss.fmservice.ejb.dpsinterface.FmDPSHandler;
import com.ericsson.oss.fmservice.ejb.eventbus.listener.ConfigurationChangeListener;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.mediation.core.events.MediationTaskRequest;
import com.ericsson.oss.services.fm.service.model.*;
import com.ericsson.oss.services.fm.service.util.DPSConstants;
import com.ericsson.oss.services.fm.service.util.FDNConverter;

/**
 * @author tcsjapa
 * 
 *         This FMServiceBean is used to interact with FM Communicator for
 *         supervising the nodes
 * 
 */
@Stateless
@Remote(FMServiceRemote.class)
public class FMServiceImpl implements FMServiceRemote {

	@Inject
	@Modeled
	private EventSender<MediationTaskRequest> fmMediationRequestEventSender;

	@EJB
	private FmDPSHandler dpsInterface;

	@Inject
	private SystemRecorder systemRecorder;

	@Inject
	private ConfigurationChangeListener configChange;

	@Inject
	private Logger logger;




	/**
	 * Create alarm supervision attributes with received parameters
	 * 
	 * @param fmNeProperties
	 * @param riaData
	 * @param properties
	 */
	public void getFmNeProperties(final FmNeProperties fmNeProperties,
			final RIAData riaData, final Map<String, Object> properties) {

		properties.put(DPSConstants.AUTOMATIC_SYNCHRONIZATION,
				fmNeProperties.isAutoSync());
		properties.put(DPSConstants.IS_NODESUSPENDED,
				fmNeProperties.isNodeSuspended());
		properties.put(DPSConstants.ALARM_SUPERVISION,
				fmNeProperties.isAlarmSupervision());
		properties.put(DPSConstants.HEARTBEAT_SUPERVISON,
				fmNeProperties.isHeartBeatSupervision());
		properties.put(DPSConstants.HEARTBEAT_TIMEOUT,
				fmNeProperties.getHeartBeatTimeout());
		properties
		.put(DPSConstants.SOURCE_TYPE, fmNeProperties.getSourceType());
		properties.put(DPSConstants.DELTA_SYNCH_SUPPORTED,
				riaData.isDeltaSynchSupported());
		properties.put(DPSConstants.SOURCE_SYNCH_SUPPORTED,
				riaData.isSourceSynchSupported());
		properties.put(DPSConstants.SYNCH_ON_COMMIT_FAILURE_CLEAR,
				riaData.isSynchOnCommFailureClear());
		properties.put(DPSConstants.ACKNOWLEDGE_SUPPORTED,
				riaData.isAcknowledgeSupported());
		properties.put(DPSConstants.CLOSED_SUPPORTED,
				riaData.isCloseSupported());
		properties.put(DPSConstants.COMMUNICATION_TIMEOUT,
				riaData.getCommunicationTimeOut());
		properties.put(DPSConstants.SUBORDINATE_OBJECT_SYNCSUPPORTED,
				riaData.isSubordinateObjectSynchSupported());
		properties.put(DPSConstants.FILTER_INFO, riaData.getFilterInfo());
		// This is to be confirmed with testing.
		// This needs to be same a protocol attribute in datapath.
		properties.put("name", "FM");
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.fm.fm_communicator.FMSIdentitySenderInterace#startSupervision(java.lang.String)
	 */

	@Override
	public int startSupervision(final String fdn,
			final FmNeProperties fmNeProperties, final RIAData riaData) {
		logger.info("Start Supervision......{}", fdn);
		String convertedFDN = FDNConverter.convertOssFdnToTorFdn(fdn);


		convertedFDN += Constants.CORBA_FDNSTR;
		logger.info("Converted FDN: {}", convertedFDN);
		int status = 0;



		final Map<String, Object> nodeAttrs = new HashMap<String, Object>();
		nodeAttrs.put(DPSConstants.ALARM_SUPERVISION, true);
		nodeAttrs.put(Constants.HEARTBEAT_STATUS, Constants.STATE_ESTABLISHED);
		nodeAttrs.put("AlarmRateFlowControl", configChange.getAlarmRateFlowControl());
		nodeAttrs.put("AlarmRateThreshold", configChange.getAlarmRateThreshold());
		nodeAttrs.put("AlarmRateNormalThreshold", configChange.getAlarmRateNormalThreshold());
		nodeAttrs.put("AlarmRateCheckInterval", configChange.getAlarmRateCheckInterval());


		getFmNeProperties(fmNeProperties, riaData, nodeAttrs);

		try {
			status = dpsInterface.updateProtocolInfo(convertedFDN, nodeAttrs);
			if (status != -1) {
				pause(50);
				final FmMediationSupervisionRequest supervisionRequest = new FmMediationSupervisionRequest(
						convertedFDN, Constants.getTimeString(), true);
				logger.debug("sending start supervision request...{}", supervisionRequest);
				fmMediationRequestEventSender.send(supervisionRequest);


			}
		} catch (final Exception exception) {
			logger.error("failed to send supervision request...{}", exception);
			status = -1;
		}


		systemRecorder.recordEvent("FMService StartSupervision", EventLevel.DETAILED,
				"FMService", fdn, "StartSupervision called for FDN: " + fdn);

		return status;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.fm.fm_communicator.FMSIdentitySenderInterace#stopSupervision(java.lang.String)
	 */
	@Override
	public int stopSupervision(final String fdn) {

		int status = 0;
		logger.info("Stop Supervision......{}",fdn);
		String convertedFDN = FDNConverter.convertOssFdnToTorFdn(fdn);
		convertedFDN += Constants.CORBA_FDNSTR;
		logger.info("Converted FDN: {}", convertedFDN);
		try {
			final Map<String, Object> nodeAttrs = new HashMap<String, Object>();
			nodeAttrs.put("alarmSupervision", false);
			status = dpsInterface.updateProtocolInfo(convertedFDN, nodeAttrs);
			if(status != -1){
				final FmMediationSupervisionRequest supervisionRequest = new FmMediationSupervisionRequest(
						convertedFDN, Constants.getTimeString(), false);
				logger.debug("sending stop supervision request...{}", supervisionRequest.toString());
				fmMediationRequestEventSender.send(supervisionRequest);
			}
			

		} catch (final Exception fmdpsexception) {
			// Ignore
			logger.error("failed to send supervision request...{}", fmdpsexception);
			status = -1;
		}

		systemRecorder.recordEvent("FMService StopSupervision", EventLevel.DETAILED,
				"FMService", fdn, "StopSupervision called for FDN: " + fdn);

		return status;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.fm.fm_communicator.FMSIdentitySenderInterace#startSync(java.lang.String)
	 */
	@Override
	public int startSync(final String fdn) {
		// code for sending ack/unack request to mediation event based client
		logger.info("Start Sync......{}", fdn);
		String convertedFDN = FDNConverter.convertOssFdnToTorFdn(fdn);
		convertedFDN += Constants.CORBA_FDNSTR;
		logger.info("Converted FDN: {}", convertedFDN);
		try {
			if(dpsInterface.findMOExistsinDPS(convertedFDN) == 0){
				final FmMediationAlarmSyncRequest fmMediationAlarmSyncRequest = new FmMediationAlarmSyncRequest();
				fmMediationAlarmSyncRequest.setProtocolInfo("FM");
				fmMediationAlarmSyncRequest.setNodeAddress(convertedFDN);
				fmMediationAlarmSyncRequest.setJobId(Constants.getTimeString());
				logger.debug("sending Fm Mediation alarm sync request...{}", fmMediationAlarmSyncRequest.toString());
				fmMediationRequestEventSender.send(fmMediationAlarmSyncRequest);

				systemRecorder.recordEvent("FMService startSync", EventLevel.DETAILED,
						"FMService", fdn, "startSync called for FDN: " + fdn);

				return 0;
			}else{
				logger.info("Discarding startSync request with fdn {} as ManagedObject is not found in DPS.",fdn);
				return -1;
			}
		} catch (final Exception fmdpsexception) {			
			logger.error("failed to send sync request for fdn {} with Exception {}",fdn,fmdpsexception);
			return -1;
		}
		
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.fm.fm_communicator.FMSIdentitySenderInterace#ackAlarm(com.ericsson.nms.fm.fm_communicator.AckRequest[])
	 */
	@Override
	public int ackAlarm(final AckRequest ackRequest) {
		logger.debug("Ack request ......{}", ackRequest);
		final FmMediationAckRequest fmMediationAckRequest = new FmMediationAckRequest();
		String convertedFDN = FDNConverter.convertOssFdnToTorFdn(ackRequest
				.getOor());
		convertedFDN += Constants.CORBA_FDNSTR;
		logger.info("Converted FDN: {}", convertedFDN);
		try{
			if(dpsInterface.findMOExistsinDPS(convertedFDN) == 0){
				fmMediationAckRequest.setNodeAddress(convertedFDN);
				final List<String> alarmList = new ArrayList<String>();
				alarmList.add(ackRequest.getAlarmId());
				fmMediationAckRequest.setAlarmId(alarmList);
				fmMediationAckRequest.setOperatorName(ackRequest.getOperator());
				fmMediationAckRequest.setProtocolInfo("FM");
				fmMediationAckRequest.setJobId("jobId");
				if (ackRequest.getAckStatus() == AckRequest.AckStatus.ACKNOWLEDGE) {
					logger.debug("Acknowledge request:{}", AckRequest.AckStatus.ACKNOWLEDGE);
					fmMediationAckRequest
					.setAckStatus(FmMediationAckRequest.AckStatus.ACKNOWLEDGE);

				} else {
					logger.debug("Unacknowledge request:{}", AckRequest.AckStatus.UNACKNOWLEDGE);
					fmMediationAckRequest
					.setAckStatus(FmMediationAckRequest.AckStatus.UNACKNOWLEDGE);
				}
				logger.debug("Sending the acknowledge request..{}", fmMediationAckRequest.toString());
				fmMediationRequestEventSender.send(fmMediationAckRequest);

				systemRecorder.recordEvent("FMService AckAlarm", EventLevel.DETAILED,
						"FMService", ackRequest.getOor(), "AckAlarm called for FDN: "
								+ ackRequest.getOor());

				return 0;
			}else{
				logger.info("Discarding ackAlarm request with fdn {} as ManagedObject is not found in DPS.",ackRequest.getOor());
				return -1;
			}
		}catch(final Exception exception){
			logger.error("failed to send ack request for fdn {} with Exception {}",ackRequest.getOor(),exception);
			return -1;
		}

	}
	
	/**
     * @param milliSeconds
     */
    private static void pause(final long milliSeconds) {
        final long start = new Date().getTime();
        long end = new Date().getTime();
        while (end - start < milliSeconds) {
            end = new Date().getTime();
        }
    }

}
