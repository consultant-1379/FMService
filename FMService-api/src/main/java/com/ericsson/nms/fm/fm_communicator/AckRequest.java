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
package com.ericsson.nms.fm.fm_communicator;

import java.io.Serializable;

/**
 * @author tcsjapa
 *
 * Acknowledge Request received from FM Communicator over RMI.
 */

public class AckRequest implements Serializable{
	private static final long serialVersionUID = 8902184529980921064L;
	private String oor;
	private String alarmId;
	private String operator;
	private AckStatus ackStatus;


	/**
	 * @return the oor
	 */
	public String getOor() {
		return oor;
	}
	/**
	 * @param oor the oor to set
	 */
	public void setOor(final String oor) {
		this.oor = oor;
	}
	
	/**
	 * @return the alarmId
	 */
	public String getAlarmId() {
		return alarmId;
	}
	/**
	 * @param alarmId the alarmId to set
	 */
	public void setAlarmId(final String alarmId) {
		this.alarmId = alarmId;
	}
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(final String operator) {
		this.operator = operator;
	}
	/**
	 * @return the ackStatus
	 */
	public AckStatus getAckStatus() {
		return ackStatus;
	}
	/**
	 * @param ackStatus the ackStatus to set
	 */
	public void setAckStatus(final AckStatus ackStatus) {
		this.ackStatus = ackStatus;
	}
	
	public enum AckStatus {
		ACKNOWLEDGE,
		UNACKNOWLEDGE;
	}	
	

}
