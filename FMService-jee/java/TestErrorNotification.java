import java.util.Date;

import org.junit.*;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.nms.fm.fm_communicator.Notification.EventSeverity;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.*;
public class TestErrorNotification {


	@Test
	public void testForAlarmNotification() {
		ErrorNotification errorNotification;
		Date theTime;
		Notification notif;
		
		theTime=new Date();
		errorNotification = new ErrorNotification();
		errorNotification.setEventAgentId("eventAgentId");
		errorNotification.setEventType("eventType");
		errorNotification.setNodeAddress("TESTNODE");
		errorNotification.setNumId(0);
		errorNotification.setPerceivedSeverity("CRITICAL");
		errorNotification.setProbableCause("PC");		
		errorNotification.setSourceType("sourceType");
		errorNotification.setSpecificProblem("SP");
		errorNotification.setStringId("stringId");
		errorNotification.setTheTime(theTime);
		errorNotification.setTimeZone("UTC");
		notif = errorNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),errorNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),errorNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");
		Assert.assertEquals(notif.getNumId(),errorNotification.getNumId());
		Assert.assertEquals(notif.getNumId(),0);
		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),errorNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getSourceType(),errorNotification.getSourceType());
		Assert.assertEquals(notif.getSourceType(),"sourceType");
		Assert.assertEquals(notif.getSpecificProblem(),errorNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertEquals(notif.getStringId(),errorNotification.getStringId());
		Assert.assertEquals(notif.getStringId(),"stringId");
		Assert.assertEquals(notif.getTimeZone(),errorNotification.getTimeZone());
		Assert.assertEquals(notif.getTimeZone(),"UTC");
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getAckState());
		Assert.assertEquals(notif.getNotificationType(),NotificationType.ERROR_EVENT);
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CRITICAL);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
		Assert.assertEquals(notif.getTheTime(),errorNotification.getTheTime());
		Assert.assertEquals(notif.getTheTime(),theTime);
		
		errorNotification.setPerceivedSeverity("MAJOR");
		notif=errorNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MAJOR);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
		
		errorNotification.setPerceivedSeverity("MINOR");
		notif=errorNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MINOR);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
		
		errorNotification.setPerceivedSeverity("WARNING");
		notif=errorNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.WARNING);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
		
		errorNotification.setPerceivedSeverity("INDETERMINATE");
		notif=errorNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.INDERMINATE);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
		
		errorNotification.setPerceivedSeverity("CLEARED");
		notif=errorNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CLEARED);
		Assert.assertEquals(notif.getSeverity(),errorNotification.getEventSeverity());
	}

}