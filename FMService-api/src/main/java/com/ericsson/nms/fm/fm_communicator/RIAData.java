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
import java.util.Arrays;

/**
 * @author xnavrat
 * 
 *         RIA Data received from FM Communicator
 * 
 */
public class RIAData implements Serializable {

	private static final long serialVersionUID = 8902184529980921057L;
	private boolean deltaSynchSupported;
	private boolean sourceSynchSupported;
	private boolean synchable;
	private boolean synchOnCommFailureClear;
	private boolean acknowledgeSupported;
	private boolean closeSupported;
	private ClearAllBehaviour clearAllBehaviour;
	private long communicationTimeOut;
	private boolean subordinateObjectSynchSupported;
	private String[] filterInfo;

	public boolean isDeltaSynchSupported() {
		return deltaSynchSupported;
	}

	public void setDeltaSynchSupported(final boolean deltaSynchSupported) {
		this.deltaSynchSupported = deltaSynchSupported;
	}

	public boolean isSourceSynchSupported() {
		return sourceSynchSupported;
	}

	public void setSourceSynchSupported(final boolean sourceSynchSupported) {
		this.sourceSynchSupported = sourceSynchSupported;
	}

	public boolean isSynchable() {
		return synchable;
	}

	public void setSynchable(final boolean synchable) {
		this.synchable = synchable;
	}

	public boolean isSynchOnCommFailureClear() {
		return synchOnCommFailureClear;
	}

	public void setSynchOnCommFailureClear(final boolean synchOnCommFailureClear) {
		this.synchOnCommFailureClear = synchOnCommFailureClear;
	}

	public boolean isAcknowledgeSupported() {
		return acknowledgeSupported;
	}

	public void setAcknowledgeSupported(final boolean acknowledgeSupported) {
		this.acknowledgeSupported = acknowledgeSupported;
	}

	public boolean isCloseSupported() {
		return closeSupported;
	}

	public void setCloseSupported(final boolean closeSupported) {
		this.closeSupported = closeSupported;
	}

	public ClearAllBehaviour getClearAllBehaviour() {
		return clearAllBehaviour;
	}

	public void setClearAllBehaviour(final ClearAllBehaviour clearAllBehaviour) {
		this.clearAllBehaviour = clearAllBehaviour;
	}

	public long getCommunicationTimeOut() {
		return communicationTimeOut;
	}

	public void setCommunicationTimeOut(final long communicationTimeOut) {
		this.communicationTimeOut = communicationTimeOut;
	}

	public boolean isSubordinateObjectSynchSupported() {
		return subordinateObjectSynchSupported;
	}

	public void setSubordinateObjectSynchSupported(
			final boolean subordinateObjectSynchSupported) {
		this.subordinateObjectSynchSupported = subordinateObjectSynchSupported;
	}

	public String[] getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(final String[] filterInfo) {
		this.filterInfo = filterInfo;
	}

	public String toString() {

		final StringBuilder sBuilder = new StringBuilder(
				"RIAData [deltaSynchSupported="
						+ deltaSynchSupported
						+ ", sourceSynchSupported="
						+ sourceSynchSupported
						+ ", synchable="
						+ synchable
						+ ", synchOnCommFailureClear="
						+ synchOnCommFailureClear
						+ ", acknowledgeSupported="
						+ acknowledgeSupported
						+ ", closeSupported="
						+ closeSupported
						+ ", communicationTimeOut="
						+ communicationTimeOut
						+ ", subordinateObjectSynchSupported="
						+ subordinateObjectSynchSupported
						+ ", filterInfo="
						+ (filterInfo != null ? Arrays.asList(filterInfo)
								: null) + "]");

		return sBuilder.toString();
	}

	public enum ClearAllBehaviour {
		CLOSE_ALL, SYNCHRONIZE, CLOSE_ALL_AND_SYNCHRONIZE

	}

}
