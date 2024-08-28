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

/**
 * @author xnavrat
 */
package com.ericsson.nms.fm.fm_communicator;

import java.io.Serializable;

/**
 * 
 * Properties for Managed Elements received from FM Communicator
 * 
 */
public class FmNeProperties implements Serializable {

	private static final long serialVersionUID = 8902184529980921059L;
	private boolean autoSync;
	private boolean nodeSuspended;
	private String sourceType;
	private boolean heartBeatSupervision;
	private int heartBeatTimeout;
	private boolean alarmSupervision;

	public boolean isAutoSync() {
		return autoSync;
	}

	public void setAutoSync(final boolean autoSync) {
		this.autoSync = autoSync;
	}

	public boolean isNodeSuspended() {
		return nodeSuspended;
	}

	public void setNodeSuspended(final boolean nodeSuspended) {
		this.nodeSuspended = nodeSuspended;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(final String sourceType) {
		this.sourceType = sourceType;
	}

	public boolean isHeartBeatSupervision() {
		return heartBeatSupervision;
	}

	public void setHeartBeatSupervision(final boolean heartBeatSupervision) {
		this.heartBeatSupervision = heartBeatSupervision;
	}

	public int getHeartBeatTimeout() {
		return heartBeatTimeout;
	}

	public void setHeartBeatTimeout(final int heartBeatTimeout) {
		this.heartBeatTimeout = heartBeatTimeout;
	}

	public boolean isAlarmSupervision() {
		return alarmSupervision;
	}

	public void setAlarmSupervision(final boolean alarmSupervision) {
		this.alarmSupervision = alarmSupervision;
	}

	public String toString() {
		final StringBuilder sBuilder = new StringBuilder(
				"FmNeProperties [autoSync=" + autoSync + ", nodeSuspended="
						+ nodeSuspended + ", sourceType=" + sourceType
						+ ", heartBeatSupervision=" + heartBeatSupervision
						+ ", heartBeatTimeout=" + heartBeatTimeout
						+ ", alarmSupervision=" + alarmSupervision + "]");
		return sBuilder.toString();
	}

}