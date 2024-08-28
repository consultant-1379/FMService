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
import java.util.*;

import org.junit.Test;
import org.mockito.*;
import org.slf4j.Logger;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.oss.fmservice.ejb.dpsinterface.FmDPSHandler;
import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.fmservice.ejb.service.impl.FMServiceImpl;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.mediation.core.events.MediationTaskRequest;
import com.ericsson.oss.services.fm.service.model.FmMediationAckRequest;
import com.ericsson.oss.services.fm.service.model.FmMediationAlarmSyncRequest;
import com.ericsson.oss.services.fm.service.util.DPSConstants;
import com.ericsson.oss.services.fm.service.util.FDNConverter;

public class TestFMServiceImpl {

	@Mock
	private EventSender<MediationTaskRequest> mockEventSender;
	@Mock
	private FmDPSHandler mockDPSI;

	@Mock
	private  Logger logger;
	
	@Mock
	private SystemRecorder sysRecorder;
	
	@Mock
	private FMServiceImpl fms;
	
	@Mock
	private static FMClusterListener fmClusterListener;


	private static final String fdn = "SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,ManagedElement=RNC_TEST";
	private static FMServiceImpl fmsImpl;

	@Test
	public void testStartSync() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		setUp();
		//recv.setMCOneStatus(status);
		fmsImpl.startSync(fdn);
		
		final FmMediationAlarmSyncRequest fmMediationAlarmSyncRequest = new FmMediationAlarmSyncRequest();
		fmMediationAlarmSyncRequest.setNodeAddress(FDNConverter.convertOssFdnToTorFdn(fdn).concat(",ManagedElement=1,FmAccess=1"));
		fmMediationAlarmSyncRequest.setJobId("jobId");
		
	}
	
	

	//@Test
	public void testStartSup() throws Exception {
		setUp();
		final FMServiceImpl mockService = Mockito.spy(fmsImpl);
		final FmNeProperties fmNeProperties = new FmNeProperties();
		final RIAData riaData = new RIAData();
		final Map<String, Object> properties = new HashMap<String, Object>();

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

		properties.put(DPSConstants.SOURCE_TYPE, fmNeProperties.getSourceType());


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

		final String convertedFDN = FDNConverter.convertOssFdnToTorFdn(fdn);

		mockService.startSupervision(fdn, fmNeProperties, riaData);
		verify(mockService, times(1)).getFmNeProperties(fmNeProperties,
				riaData, properties);
		verify(mockDPSI, times(1)).updateProtocolInfo(convertedFDN, properties);

	}

	@Test
	public void testAckAlarm() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		setUp();
		final AckRequest ackRequest = new AckRequest();
		ackRequest.setOor(fdn);
		ackRequest.setAlarmId("alarmId");
		ackRequest.setOperator("operator");
		ackRequest.setAckStatus(AckRequest.AckStatus.ACKNOWLEDGE);
		fmsImpl.ackAlarm(ackRequest);
		final FmMediationAckRequest fmMediationAckRequest = new FmMediationAckRequest();
		fmMediationAckRequest.setNodeAddress(FDNConverter.convertOssFdnToTorFdn(ackRequest.getOor()).concat(",ManagedElement=1,FmAccess=1"));
		final List<String> alarmList = new ArrayList<String>();
		alarmList.add(ackRequest.getAlarmId());
		fmMediationAckRequest.setAlarmId(alarmList);
		fmMediationAckRequest.setOperatorName(ackRequest.getOperator());
		fmMediationAckRequest.setProtocolInfo("FM");
		fmMediationAckRequest
				.setAckStatus(FmMediationAckRequest.AckStatus.ACKNOWLEDGE);


				fmMediationAckRequest.setJobId("jobId");
				verify(mockEventSender, times(1)).send(fmMediationAckRequest);
				//verify(mockEventSender, times(1)).send(fmMediationAckRequest,ReceiveModeledEvent.MEDFMONE_CHANNEL);
	}
	
	
	//@Test
	public void testStopSup() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		setUp();
		fmsImpl.stopSupervision(fdn);
		final String convertedFDN = FDNConverter.convertOssFdnToTorFdn(fdn);
		verify(mockDPSI, times(1)).setSupervisionOff(convertedFDN);

	}

	public void setUp() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		MockitoAnnotations.initMocks(this);
		fmsImpl = new FMServiceImpl();

		final Field mockSender = FMServiceImpl.class
				.getDeclaredField("fmMediationRequestEventSender");
		mockSender.setAccessible(true);
		mockSender.set(fmsImpl, mockEventSender);
		
		final Field mockRecorder = FMServiceImpl.class.getDeclaredField("systemRecorder");
		mockRecorder.setAccessible(true);
		mockRecorder.set(fmsImpl, sysRecorder);

		final Field mockDpsInterface = FMServiceImpl.class
				.getDeclaredField("dpsInterface");
		mockDpsInterface.setAccessible(true);
		mockDpsInterface.set(fmsImpl, mockDPSI);

		final Field mockLogger = FMServiceImpl.class.getDeclaredField("logger");
		mockLogger.setAccessible(true);
		mockLogger.set(fmsImpl, logger);

	}
}
