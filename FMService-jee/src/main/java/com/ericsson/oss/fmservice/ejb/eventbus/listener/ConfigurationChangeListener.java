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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;


@ApplicationScoped
public class ConfigurationChangeListener {

	@Inject @Configured(propertyName="FMService.AlarmRateCheckInterval")
	private Integer alarmRateCheckInterval;

	@Inject @Configured(propertyName="FMService.AlarmRateThreshold")
	private Integer alarmRateThreshold;

	@Inject @Configured(propertyName="FMService.AlarmRateNormalThreshold")
	private Integer alarmRateNormalThreshold;

	@Inject @Configured(propertyName="FMService.AlarmRateFlowControl")
	private String alarmRateFlowControl;

	@Inject
	private Logger logger;


	void listenForThresholdChanges(
			@Observes @ConfigurationChangeNotification(propertyName="FMService.AlarmRateThreshold") final Integer threshold) {

		logger.info("listenForThresholdChanges called in ConfigurationChangeListener" + threshold);
		this.alarmRateThreshold = threshold;
	}

	void listenForFlowControlChanges(
			@Observes @ConfigurationChangeNotification(propertyName="FMService.AlarmRateFlowControl") final String flowControl) {

		logger.info("listenForFlowControlChanges called in ConfigurationChangeListener" + flowControl);
		this.alarmRateFlowControl = flowControl;
	}

	void listenForIntervalChanges(
			@Observes @ConfigurationChangeNotification(propertyName="FMService.AlarmRateCheckInterval") final Integer checkInterval) {

		logger.info("listenForIntervalChanges called in ConfigurationChangeListener" + checkInterval);
		this.alarmRateCheckInterval = checkInterval;
	}

	void listenForNormalThresholdChanges(
			@Observes @ConfigurationChangeNotification(propertyName="FMService.AlarmRateNormalThreshold") final Integer normalThreshold) {

		logger.info("listenForNormalThresholdChanges called in ConfigurationChangeListener" + normalThreshold);
		this.alarmRateNormalThreshold = normalThreshold;
	}


	/**
	 * @return the alarmRateCheckInterval
	 */
	public Integer getAlarmRateCheckInterval() {
		return alarmRateCheckInterval;
	}

	/**
	 * @return the alarmRateThreshold
	 */
	public Integer getAlarmRateThreshold() {
		return alarmRateThreshold;
	}

	/**
	 * @return the alarmRateNormalThreshold
	 */
	public Integer getAlarmRateNormalThreshold() {
		return alarmRateNormalThreshold;
	}

	/**
	 * @return the alarmRateFlowControl
	 */
	public String getAlarmRateFlowControl() {
		return alarmRateFlowControl;
	}

}
