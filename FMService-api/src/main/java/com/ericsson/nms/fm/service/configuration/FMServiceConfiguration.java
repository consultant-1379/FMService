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
package com.ericsson.nms.fm.service.configuration;

import javax.validation.constraints.NotNull;

import com.ericsson.oss.itpf.sdk.modeling.annotation.DefaultValue;
import com.ericsson.oss.itpf.sdk.modeling.config.annotation.*;

/**
 * This class holds configuration parameters needed in FMService
 * 
 */
@ModeledConfigurationDefinition
public class FMServiceConfiguration {
	
	

	@NotNull
    @ModeledConfigurationParameter(name = "FMService.AlarmRateFlowControl", description = "Alarm rate flow control", scope = ConfigurationParameterScope.SERVICE)
    @DefaultValue("true")
    public String AlarmRateFlowControl;
		
	
	@NotNull
	@ModeledConfigurationParameter(name = "FMService.AlarmRateThreshold", description = "Allowed number of alarms within alarm rate check interval", scope = ConfigurationParameterScope.SERVICE)
	@DefaultValue("500")
	public Integer AlarmRateThreshold;
	
	@NotNull
    @ModeledConfigurationParameter(name = "FMService.AlarmRateNormalThreshold" ,description = "Alarm rate normal threshold. Integer. Percentage of alarm rate threshold", scope = ConfigurationParameterScope.SERVICE)
    @DefaultValue("50")
    public Integer AlarmRateNormalThreshold;
	
	@NotNull
    @ModeledConfigurationParameter(name = "FMService.AlarmRateCheckInterval" ,description = "Alarm rate check interval. Integer. Check interval in minutes.", scope = ConfigurationParameterScope.SERVICE)
    @DefaultValue("5")
    public Integer AlarmRateCheckInterval;
	
	
	
}
