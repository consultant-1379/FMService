import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.nms.fm.fm_communicator.Notification.EventSeverity;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.NodeSuspendNotification;
public class TestNodeSuspendedNotification {


	@Test
	public void testForAlarmNotification() {
		NodeSuspendNotification nodeSuspendedNotification;
		Date theTime;
		Notification notif;
		
		theTime=new Date();
		nodeSuspendedNotification = new NodeSuspendNotification();
		nodeSuspendedNotification.setEventAgentId("eventAgentId");
		nodeSuspendedNotification.setEventType("eventType");
		nodeSuspendedNotification.setNodeAddress("TESTNODE");
		nodeSuspendedNotification.setNumId(0);
		nodeSuspendedNotification.setPerceivedSeverity("CRITICAL");
		nodeSuspendedNotification.setProbableCause("PC");		
		nodeSuspendedNotification.setSourceType("sourceType");
		nodeSuspendedNotification.setSpecificProblem("SP");
		nodeSuspendedNotification.setStringId("stringId");
		nodeSuspendedNotification.setTheTime(theTime);
		nodeSuspendedNotification.setTimeZone("UTC");
		notif = nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),nodeSuspendedNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),nodeSuspendedNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");
		Assert.assertEquals(notif.getNumId(),nodeSuspendedNotification.getNumId());
		Assert.assertEquals(notif.getNumId(),0);
		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),nodeSuspendedNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getSourceType(),nodeSuspendedNotification.getSourceType());
		Assert.assertEquals(notif.getSourceType(),"sourceType");
		Assert.assertEquals(notif.getSpecificProblem(),nodeSuspendedNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertEquals(notif.getStringId(),nodeSuspendedNotification.getStringId());
		Assert.assertEquals(notif.getStringId(),"stringId");
		Assert.assertEquals(notif.getTimeZone(),nodeSuspendedNotification.getTimeZone());
		Assert.assertEquals(notif.getTimeZone(),"UTC");
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getAckState());
		Assert.assertEquals(notif.getNotificationType(),NotificationType.NODE_SUSPENDED);
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CRITICAL);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
		Assert.assertEquals(notif.getTheTime(),nodeSuspendedNotification.getTheTime());
		Assert.assertEquals(notif.getTheTime(),theTime);
		
		nodeSuspendedNotification.setPerceivedSeverity("MAJOR");
		notif=nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MAJOR);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
		
		nodeSuspendedNotification.setPerceivedSeverity("MINOR");
		notif=nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MINOR);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
		
		nodeSuspendedNotification.setPerceivedSeverity("WARNING");
		notif=nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.WARNING);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
		
		nodeSuspendedNotification.setPerceivedSeverity("INDETERMINATE");
		notif=nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.INDERMINATE);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
		
		nodeSuspendedNotification.setPerceivedSeverity("CLEARED");
		notif=nodeSuspendedNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CLEARED);
		Assert.assertEquals(notif.getSeverity(),nodeSuspendedNotification.getEventSeverity());
	}



}