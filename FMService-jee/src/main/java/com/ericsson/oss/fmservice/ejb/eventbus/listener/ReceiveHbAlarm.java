/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.fmservice.ejb.eventbus.listener;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.fmservice.ejb.constants.Constants;
import com.ericsson.oss.fmservice.ejb.dpsinterface.FmDPSHandler;
import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Consumes;
import com.ericsson.oss.services.fm.service.util.FDNConverter;

public class ReceiveHbAlarm {

    @Inject
    private Logger logger;

    @Inject
    private FMClusterListener fmclusterListener;

    @Inject
    private FmDPSHandler dpsInterface;

    public void receiveFailoverEvent(@Observes @Consumes(endpoint = Constants.HB_ALARM_TOPIC) final String notification) {
        if (fmclusterListener.getMasterState()) {
            logger.info("Received Hb alarm from Mediation  {}", notification);
            final String[] split = notification.split(Constants.DELIMITER);
            String torFdn = FDNConverter.convertOssFdnToTorFdn(split[0]);
            torFdn += Constants.CORBA_FDNSTR;
            final Map<String, Object> nodeAttrs = new HashMap<String, Object>();
            if (split[1].equals(com.ericsson.oss.mediation.translator.model.Constants.SEV_CRITICAL)) {
                nodeAttrs.put(Constants.HEARTBEAT_STATUS, Constants.STATE_FAILED);
                updateHbsupervisionStatus(torFdn, nodeAttrs);
            } else if (split[1].equals(com.ericsson.oss.mediation.translator.model.Constants.SEV_CLEARED)) {
                nodeAttrs.put(Constants.HEARTBEAT_STATUS, Constants.STATE_ESTABLISHED);
                updateHbsupervisionStatus(torFdn, nodeAttrs);
            }
        } else {
            logger.debug("This fmservice instance is not master at present");
        }

    }

    /**
     * @param torFdn
     *            tor fdn
     */
    private void updateHbsupervisionStatus(final String torFdn, final Map<String, Object> nodeAttributes) {
        if (dpsInterface.findMOExistsinDPS(torFdn) == 0) {
            dpsInterface.updateProtocolInfo(torFdn, nodeAttributes);
        } else {
            logger.info("The MO is not present in DPS for FDN: {}", torFdn);
        }
    }

}
