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
 * @author tcsbosr
 *
 * FM Service Info sent to FM Communicator 
 * 
 */

public class FMSInfo implements Serializable{


	private static final long serialVersionUID = 8902184529980921062L;
	private  String ip;
	private  String lookUp;

	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}


	/**
	 * @param ip the ip to set
	 */
	public void setIp(final String ip) {
		this.ip = ip;
	}


	/**
	 * @return the lookUp
	 */
	public String getLookUp() {
		return lookUp;
	}


	/**
	 * @param lookUp the lookUp to set
	 */
	public void setLookUp(final String lookUp) {
		this.lookUp = lookUp;
	}


	public String toString() {
		return "FMSInfo [ip=" + getIp() + ", Lookup="
				+ getLookUp() + "]";
	}


}