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
package com.ericsson.oss.fmservice.ejb.service.upgrade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradeEvent;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradePhase;

@ApplicationScoped
public class UpgradeEventObserver {

	@Inject
	private Logger logger;

	@Inject
	private SystemRecorder systemRecorder;

	public void upgradeNotificationObserver(@Observes final UpgradeEvent event) {

		final UpgradePhase phase = event.getPhase();
		switch (phase) {
		case SERVICE_INSTANCE_UPGRADE_PREPARE:
			logger.debug("FMService Instance is ready for Upgrade.",
					phase.toString());
			event.accept("FMService Instance is ready for upgrade.");
			break;
		case SERVICE_CLUSTER_UPGRADE_PREPARE:
			logger.debug("FMService Cluster is ready for Upgrade.",
					phase.toString());
			event.accept("FMService Cluster is ready for upgrade.");
			break;
		case SERVICE_CLUSTER_UPGRADE_FAILED:
			logger.debug("FMService Cluster upgrade Failed.", phase.toString());
			event.accept("FMService Cluster upgrade Failed.");
			break;
		case SERVICE_CLUSTER_UPGRADE_FINISHED_SUCCESSFULLY:
			logger.debug("FMService Cluster upgrade Success.", phase.toString());
			event.accept("FMService Cluster upgrade Success.");
			break;
		case SERVICE_INSTANCE_UPGRADE_FAILED:
			logger.debug("FMService Instance upgrade Failed.", phase.toString());
			event.accept("FMService Instance upgrade Failed.");
			break;
		case SERVICE_INSTANCE_UPGRADE_FINISHED_SUCCESSFULLY:
			logger.debug("FMService Instance upgrade Success.",
					phase.toString());
			event.accept("FMService Instance upgrade Success.");
			break;
		default:
			logger.error("Unexpected UpgradePhase", phase.toString());
			event.reject("Unexpected UpgradePhase");
			break;
		}

		systemRecorder.recordEvent(
				"Upgrade Notification Observer in FMService",
				EventLevel.DETAILED, "FMService Upgrade", "FMService Upgrade",
				phase.toString() + " Upgrade event is recieved For FMService");
	}
}