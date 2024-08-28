/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.fmservice.ejb.constants;

public class Constants {

    public static final String HB_ALARM_TOPIC = "jms:/topic/hbalarmtopic";
    public static final String DELIMITER = "@@@";
    public static final String MEDFMONE_CHANNEL = "FmMediationtaskQueue_0";
    public static final String MEDFMTWO_CHANNEL = "FmMediationtaskQueue_1";
    public static final String MED_NODEONE = "MedCore_su_0_jee_instance";
    public static final String MED_NODETWO = "MedCore_su_1_jee_instance";

    public static final String HANDOVERTOPIC = "jms:/topic/handovertopic";
    public static final String FMMSREGFAILED = "FMMSRegistrationFailed";
    public static final String NODESOURCETYPE = "LRAN";
    public static final String CORBA_FDNSTR = ",ManagedElement=1,FmAccess=1";

    public static final String HEARTBEAT_STATUS = "heartbeatStatus";
    public static final String STATE_FAILED = "FAILED";
    public static final String STATE_ESTABLISHED = "ESTABLISHED";
	
	/**
     * @return the string containing time in msec
     */
    public static String getTimeString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        return sb.toString();
    }

}
