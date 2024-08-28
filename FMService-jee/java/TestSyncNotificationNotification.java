import java.util.Date;

import org.junit.*;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.nms.fm.fm_communicator.Notification.AckStatus;
import com.ericsson.nms.fm.fm_communicator.Notification.EventSeverity;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.*;
public class TestSyncNotificationNotification {


	@Test
	public void testForAlarmNotification() {
		SyncNotification syncNotification;
		Date theTime;
		Notification notif;
		
		theTime=new Date();
		syncNotification = new SyncNotification();
		syncNotification.setEventAgentId("eventAgentId");
		syncNotification.setEventType("eventType");
		syncNotification.setNodeAddress("TESTNODE");
		syncNotification.setNumId(0);
		syncNotification.setPerceivedSeverity("CRITICAL");
		syncNotification.setProbableCause("PC");		
		syncNotification.setSourceType("sourceType");
		syncNotification.setSpecificProblem("SP");
		syncNotification.setStringId("stringId");
		syncNotification.setTheTime(theTime);
		syncNotification.setTimeZone("UTC");
		syncNotification.setAckStatus(0);
		syncNotification.setOperator("operator");
		
		notif = syncNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),syncNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),syncNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");
		Assert.assertEquals(notif.getNumId(),syncNotification.getNumId());
		Assert.assertEquals(notif.getNumId(),0);
		Assert.assertEquals(notif.getOperatorName(),syncNotification.getOperator());
		Assert.assertEquals(notif.getOperatorName(),"operator");
		Assert.assertEquals(notif.getAckState(),syncNotification.getFMAckStatus());
		Assert.assertEquals(notif.getAckState(),AckStatus.ACKNOWLEDGED);
		
		Assert.assertEquals(notif.getProbableCause(),syncNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getSourceType(),syncNotification.getSourceType());
		Assert.assertEquals(notif.getSourceType(),"sourceType");
		Assert.assertEquals(notif.getSpecificProblem(),syncNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertEquals(notif.getStringId(),syncNotification.getStringId());
		Assert.assertEquals(notif.getStringId(),"stringId");
		Assert.assertEquals(notif.getTimeZone(),syncNotification.getTimeZone());
		Assert.assertEquals(notif.getTimeZone(),"UTC");
		Assert.assertNull(notif.getTopObjectName());

		Assert.assertEquals(notif.getNotificationType(),NotificationType.SYNC_EVENT_NOTIFICATION);
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CRITICAL);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		Assert.assertEquals(notif.getTheTime(),syncNotification.getTheTime());
		Assert.assertEquals(notif.getTheTime(),theTime);
		
		syncNotification.setPerceivedSeverity("MAJOR");
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MAJOR);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		
		syncNotification.setPerceivedSeverity("MINOR");
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MINOR);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		
		syncNotification.setPerceivedSeverity("WARNING");
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.WARNING);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		
		syncNotification.setPerceivedSeverity("INDETERMINATE");
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.INDERMINATE);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		
		syncNotification.setPerceivedSeverity("CLEARED");
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CLEARED);
		Assert.assertEquals(notif.getSeverity(),syncNotification.getEventSeverity());
		
		syncNotification.setAckStatus(1);
		notif=syncNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getAckState(),syncNotification.getFMAckStatus());
		Assert.assertEquals(notif.getAckState(),AckStatus.UNACKNOWLEDGED);
		
	}

}