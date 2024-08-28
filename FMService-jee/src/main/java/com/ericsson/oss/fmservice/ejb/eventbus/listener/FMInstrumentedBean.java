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
package com.ericsson.oss.fmservice.ejb.eventbus.listener;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;

@InstrumentedBean
@ApplicationScoped
@Profiled
public class FMInstrumentedBean {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FMInstrumentedBean.class);


	/**
	 * Represents the number of alarms received in the fmalarmqueue.
	 * This will be reset to zero for each minute.
	 */
	private int alarmCount = 0;
	
	private int minalarmCount = 0;

	/**
	 * Represents the failed number of alarms while sending the alarms to FMCommunicator.
	 * This will be reset to zero for each minute.
	 */
	private int failedAlarmCount=0;
	
	private int minfailedAlarmCount=0;


	/**
	 * Represents the connection status between OSS (FMCommunicator) and TOR (FMService)
	 * This will be incremented by one when connection fails and will be made zero when the 
	 * connection is re-established.
	 */
	private int connectionStatus = 0;

	/**
	 * @return the alarmCount
	 */
	public int getalarmCount() {
		return this.alarmCount;
	}


	/**
	 * @return the failedAlarmCount
	 */
	public int getFailedAlarmCount() {
		return this.failedAlarmCount;
	}
	

	/**
	 * @return the connectionStatus
	 */
	public int getConnectionStatus() {
		return this.connectionStatus;
	}

	/**
	 * Increment the connectionStatus.
	 * This method is called when there is a connection problem with FMCommunicator. 
	 */
	public  void increaseConnectionStatus(){
		this.connectionStatus++;
	}


	/**
	 * Decrement the connectionStatus.
	 * This method is called when there is a connection is reestablished with FMCommunicator. 
	 */
	public void decreaseConnectionStatus(){
		this.connectionStatus--;
	}
	
	/**
	 * @return the minalarmCount
	 */
	public int getminalarmCount() {
		return this.minalarmCount;
	}

	public void increaseminalarmCount() {
		this.minalarmCount++;
		LOGGER.debug("Increased the minalarmCount: {}",this.minalarmCount);
	}

	public void resetminalarmCount(){
		this.alarmCount = this.minalarmCount;
		this.minalarmCount=0;
		LOGGER.debug("Resetting the minalarm count to Zero after one minute: {}",this.minalarmCount);
	}
	
	public void resetminFailedCount(){
		this.failedAlarmCount = this.minfailedAlarmCount;
		this.minfailedAlarmCount=0;
		LOGGER.debug("Resetting the alarm count to Zero after one minute: {}",this.failedAlarmCount);
	}


	/**
	 * @return the minfailedAlarmCount
	 */
	public int getminFailedAlarmCount() {
		return this.minfailedAlarmCount;
	}

	/**
	 * Increment the minfailedAlarmCount
	 */ 
	public void increaseminFailedAlarms() {
		this.minfailedAlarmCount++;
		LOGGER.debug("Failed to Send the alarm to FM communicator, Increased the failedAlarmCount: {}",this.failedAlarmCount);
	}


}
