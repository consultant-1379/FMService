package com.ericsson.oss.fmservice.ejb.dpsinterface;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.datalayer.datapersistence.api.*;
import com.ericsson.oss.itpf.datalayer.model.po.*;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.fm.service.util.DPSConstants;

/**
 * @author tcsbosr The EJB is responsible to interact with
 *         DataPersitanceManager
 * TODO: This needs to modified to update only CirpProtocolInfo
 * as all objects are merged into one PO.
 */

@Stateless
public class FmDPSHandler {
	
	/** To be replaced with @EServiceRef, once support is provided */
	//@EJB(lookup = "ejb:DataPersistence/DataPersistence-ejb/DataPersistenceManagerImpl!com.ericsson.oss.itpf.datalayer.datapersistence.api.DataPersistenceManager")
	@EServiceRef
	private DataPersistenceManager dpManager;
	
	@Inject
	private Logger logger;


	public DataPersistenceManager getDpManager() {
		return dpManager;
	}
	
	
	

	/**
	 * Update CORBA and SNMP protocol info
	 * 
	 * @param torFDN
	 * @param nodeAttributes
	 * @return
	 */
	public int updateProtocolInfo(final String torFDN,
			final Map<String, Object> nodeAttributes) throws DataPersistenceException {
		int status = 0;
		final ManagedObject managedObject = dpManager.findMO(torFDN);
		logger.debug("Managed Object for protocol updation : "+managedObject);
		if (managedObject != null) {
			final EntityAddressInfo eaInfo = (EntityAddressInfo) managedObject
					.getEntityAddressInfo();
			logger.debug("Entity Address Information:"+eaInfo);
			if (eaInfo != null) {
				logger.debug("Entity Address Info:"+eaInfo);
				CirpProtocolInfo cirpProtocolInfo = null;
				final Collection<PersistentObject> niAssociations = eaInfo
						.getAssociation(DPSConstants.PROTOCOL_INFO_ASSOCIATION_NAME);
				for (PersistentObject nInfo : niAssociations) {
					if (nInfo.getType().equals(DPSConstants.CIRP_PROTOCOL_INFO_TYPE)) {
						cirpProtocolInfo = (CirpProtocolInfo) nInfo;
						break;
					}
				}
				if (cirpProtocolInfo != null) {
					logger.debug("Cirp Protocol Info:"+cirpProtocolInfo);
					cirpProtocolInfo.setAttributes(nodeAttributes);
					dpManager.persist(cirpProtocolInfo);
					eaInfo.addAssociation(DPSConstants.PROTOCOL_INFO_ASSOCIATION_NAME, cirpProtocolInfo);
					dpManager.persist(eaInfo);
					managedObject.setEntityAddressInfo(eaInfo);
					dpManager.persist(managedObject);
				} else {
					logger.error("No cirp protocol info available: "+cirpProtocolInfo);
					status = -1;
				}
			} else {
				logger.error("No Entity Address Info  available: "+eaInfo);
				status = -1;
			}
		} else {
			status = -1;
			logger.error("Managed Object for updation of protocol info is not present: "+managedObject);
		}
		return status;
	}
	
	/**
	 * find node exists in DPS or not
	 * 
	 * @param torFDN
	 * @return node existence in DPS
	 */
	public int findMOExistsinDPS(final String torFDN)
			throws DataPersistenceException {
		final ManagedObject managedObject = dpManager.findMO(torFDN);
		if (managedObject != null) {
			logger.debug("findMOExistsinDPS:Managed Object exists {}",managedObject);
			return 0;
		} else {
			logger.error("findMOExistsinDPS:Managed Object does not exists {}",managedObject);
			return -1;
		}
	}

	/**
	 * get the supervision status for the node
	 * 
	 * @param torFDN
	 * @return supervisionStatus
	 */
	public Map<String, Object> getSupervisionStatus(final String torFDN)
			throws DataPersistenceException {
		boolean supervisionStatus = false;
		CirpProtocolInfo cirpProtocolInfo = null;
		Map<String, Object> sourceTypeMap = new HashMap<String, Object>();
		final ManagedObject managedObject = dpManager.findMO(torFDN);

		if (managedObject != null) {
			logger.info("getSupervisionStatus:Managed Object for getProtocolInfo "+managedObject);
			final EntityAddressInfo eaInfo = (EntityAddressInfo) managedObject
					.getEntityAddressInfo();
			if (eaInfo != null) {
				logger.debug("getSupervisionStatus:Entity Address Info:"+eaInfo);
				final Collection<PersistentObject> neAssociations = eaInfo
						.getAssociation(DPSConstants.PROTOCOL_INFO_ASSOCIATION_NAME);
				for (PersistentObject nInfo : neAssociations) {
					if (nInfo.getType().equals(
							DPSConstants.CIRP_PROTOCOL_INFO_TYPE)) {
						cirpProtocolInfo = (CirpProtocolInfo) nInfo;
						break;
					}
				}

				if (cirpProtocolInfo != null) {
						logger.debug("getSourceType:cirpProtocolInfo is: " + cirpProtocolInfo);
						sourceTypeMap = cirpProtocolInfo.getAttributes();
				}
				else {
					logger.error("getSupervisionStatus:No protocol info available:");

				}

			} else {
				logger.error("getSupervisionStatus:No Entity Address Info  available");


			}
		} else {
			logger.error("getSupervisionStatus:Managed Object for updation of protocol info is not present: "+managedObject);

		}
		return sourceTypeMap;
	}
	
	/**
	 * @param torFDN
	 * @param hbSupervisionAttributes
	 * @return
	 */
	public int updateHBSupervision(final String torFDN,
			final Map<String, Object> hbSupervisionAttributes) throws DataPersistenceException {
		int status = 0;
		final ManagedObject managedObject = dpManager.findMO(torFDN);
		logger.info("Managed Object for update HBSupervision : "+managedObject);
		if (managedObject != null) {
			HeartbeatSupervision heartbeatSupervision = null;
		
			final Collection<PersistentObject> hbAssociations = managedObject
					.getAssociation(DPSConstants.HEARTBEAT_SUPERVISION_TYPE);
			for (PersistentObject hbSupervision : hbAssociations) {
				if (hbSupervision.getType().equals(
						DPSConstants.HEARTBEAT_SUPERVISION_TYPE)) {
					logger.debug("Supervision type:"+hbSupervision.getType());
					heartbeatSupervision = (HeartbeatSupervision) hbSupervision;
					
					break;
				}
			}
			if (heartbeatSupervision != null) {
				heartbeatSupervision.setAttributes(hbSupervisionAttributes);
				logger.info("Setting the attributes for updating HBSupervision");
			} else {
				logger.info("creating PO since hearbeat supervision type is not available");
				heartbeatSupervision = (HeartbeatSupervision) dpManager
						.createPO(DPSConstants.HEARTBEAT_SUPERVISION_TYPE,
								hbSupervisionAttributes);
				managedObject.addAssociation(
						DPSConstants.HEARTBEAT_SUPERVISION_TYPE,
						(PersistentObject) heartbeatSupervision);
			}
			dpManager.persist((PersistentObject) heartbeatSupervision);
			managedObject.addAssociation(
					DPSConstants.HEARTBEAT_SUPERVISION_TYPE,
					heartbeatSupervision);

		} else {
			logger.info("Managed Object for update HBSupervision not present: "+managedObject);
			status = -1;

		}
		return status;
	}

	/**
	 * @param torFdn
	 * @param alarmSupervisionAttributes
	 * @return
	 */
	public int updateAlarmSupervision(final String torFdn,
			final Map<String, Object> alarmSupervisionAttributes) throws DataPersistenceException {
		
		int status = 0;
		final ManagedObject managedObject = dpManager.findMO(torFdn);
		logger.info("Managed Object for update Alarm Supervision : "+managedObject);
		if (managedObject != null) {

			final Collection<PersistentObject> alarmAssociations = managedObject
					.getAssociation(DPSConstants.ALARM_SUPERVISION_TYPE);

			AlarmSupervision alarmSupervision = null;
			for (PersistentObject alSupervision : alarmAssociations) {
				if (alSupervision.getType().equals(
						DPSConstants.ALARM_SUPERVISION_TYPE)) {
					logger.debug("Supervision type:"+alSupervision.getType());
					alarmSupervision = (AlarmSupervision) alSupervision;
					break;
				}
			}
			if (alarmSupervision != null) {
				alarmSupervision.setAttributes(alarmSupervisionAttributes);
				logger.info("Setting  attributes for updating alarm");
			} else {
				logger.info("creating PO since alarm supervision type is not available");
				alarmSupervision = (AlarmSupervision) dpManager.createPO(
						DPSConstants.ALARM_SUPERVISION_TYPE,
						alarmSupervisionAttributes);
				managedObject.addAssociation(
						DPSConstants.ALARM_SUPERVISION_TYPE,
						(PersistentObject) alarmSupervision);

			}
			dpManager.persist((PersistentObject) alarmSupervision);
			managedObject.addAssociation(DPSConstants.ALARM_SUPERVISION,
					alarmSupervision);

		} else {
			logger.info("Managed Object for update  Alarm Supervision not present: "+managedObject);
			status = -1;
		}
		return status;
	}

	/**
	 * @param convertedFDN
	 */
	public int setSupervisionOff(final String convertedFDN) throws DataPersistenceException {

		final ManagedObject managedObject = dpManager.findMO(convertedFDN);
		int status = 0;
		logger.info("Managed Object for Supervision Off : "+managedObject);
		if (managedObject != null) {
			final Collection<PersistentObject> alarmAssociations = managedObject
					.getAssociation(DPSConstants.ALARM_SUPERVISION_TYPE);
			AlarmSupervision alarmSupervision = null;
			for (PersistentObject alSupervision : alarmAssociations) {
				if (alSupervision.getType().equals(
						DPSConstants.ALARM_SUPERVISION_TYPE)) {
					logger.debug("Supervision type:"+alSupervision.getType());
					alarmSupervision = (AlarmSupervision) alSupervision;
					break;
				}
			}
			try {
				if (alarmSupervision != null) {
					logger.info("Setting the attributes for supervision off");
					alarmSupervision.setAttribute(
							DPSConstants.ALARM_SUPERVISION, false);
				} else {
					logger.error("Could not find Alarm Supervision OFF for "
							+ convertedFDN);
				}
			} catch (Exception e) {
				
			}
			dpManager.persist((PersistentObject) alarmSupervision);
			managedObject.addAssociation(DPSConstants.ALARM_SUPERVISION_TYPE,
					alarmAssociations);
			dpManager.persist(managedObject);

		} else {
			logger.info("Managed Object for supervision Off not present: "+managedObject);
			status = -1;
		}
		return status;
	}

}
