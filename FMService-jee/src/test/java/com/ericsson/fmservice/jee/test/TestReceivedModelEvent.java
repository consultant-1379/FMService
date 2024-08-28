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

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URISyntaxException;

import javax.jms.JMSException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.ericsson.oss.fmservice.ejb.eventbus.listener.ReceiveModeledEvent;
import com.ericsson.oss.fmservice.ejb.notification.OSSEventSender;
import com.ericsson.oss.fmservice.ejb.registration.FMClusterListener;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.services.fm.service.alarm.FmMediationNotification;

public class TestReceivedModelEvent {
	
	@Mock
	private static FMClusterListener fmClusterListener;

	@Mock
	public EventSender<FmMediationNotification> mockedSender;

	@Mock
	private static OSSEventSender mockReceiver;
	
	

	@Mock
	ReceiveModeledEvent receiveEvent;

	@Mock
	private  Logger logger;

	@Test
	public void testReceiver() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, JMSException,
			URISyntaxException {
		
		
		MockitoAnnotations.initMocks(this);
		final ReceiveModeledEvent receiveEvent = new ReceiveModeledEvent();
		final Field mockLocalIp = ReceiveModeledEvent.class
				.getDeclaredField("modeledEventSender");
		mockLocalIp.setAccessible(true);
		mockLocalIp.set(receiveEvent, mockReceiver);
		
		final Field fmListener = ReceiveModeledEvent.class.getDeclaredField("fmclusterListener");
		fmListener.setAccessible(true);
		fmListener.set(receiveEvent, fmClusterListener);

		final Field mockLogger = ReceiveModeledEvent.class.getDeclaredField("logger");
		mockLogger.setAccessible(true);
		mockLogger.set(receiveEvent, logger);
		final FmMediationNotification notif = new FmMediationNotification();
		when(fmClusterListener.getMasterState()).thenReturn(true);
		receiveEvent.receiveSpecificEvent(notif);
		verify(mockReceiver, times(1)).fmQueueSend(notif);
	}

}