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
package com.ericsson.fmservice.jee.test;

import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;

public class TestFMClusterListener {
	
	@Mock
	private  Logger mockingLogger;
	
	@Mock
	MembershipChangeEvent changeEvent;
	
	private FMClusterListener clusterListener;
	
	
	@Test
	public void testClusterListenerTrue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		setUp();
		when(changeEvent.isMaster()).thenReturn(true);
		clusterListener.listenForMembershipChange(changeEvent);
		Assert.assertEquals(true,clusterListener.getMasterState());
		
	}
	
	@Test
	public void testClusterListenerFalse() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		setUp();
		when(changeEvent.isMaster()).thenReturn(false);
		clusterListener.listenForMembershipChange(changeEvent);
		Assert.assertEquals(false,clusterListener.getMasterState());
		
	}
	
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		MockitoAnnotations.initMocks(this);
		clusterListener = new FMClusterListener();
		
		final Field mockLoggerListener = FMClusterListener.class
				.getDeclaredField("logger");
		mockLoggerListener.setAccessible(true);
		mockLoggerListener.set(clusterListener, mockingLogger);
		
		
	}
	

}
