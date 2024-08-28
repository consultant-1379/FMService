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
package com.ericsson.oss.fmservice.ejb.registration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;
import com.ericsson.oss.itpf.sdk.cluster.annotation.ServiceCluster;

@ApplicationScoped
public class FMClusterListener {
	private boolean isMaster = false;

	@Inject
	private Logger logger;

	public void listenForMembershipChange(
			@Observes @ServiceCluster("FMServiceCluster")  final MembershipChangeEvent changeEvent) {
		if (changeEvent.isMaster()) {
			logger.debug(
					"Recieved membsership change event [{}], setting current FM instance to master",
					changeEvent.isMaster());

			isMaster = true;

		} else {
			logger.debug(
					"Recieved membsership change event [{}], setting current FM instance to redundant",
					changeEvent.isMaster());
			isMaster = false;
		}
	}

	/**
	 * @return boolean state of current FM Service instance
	 */
	public boolean getMasterState() {
		return isMaster;
	}

}
