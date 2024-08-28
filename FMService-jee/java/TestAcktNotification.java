import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.nms.fm.fm_communicator.Notification.AckStatus;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.AckNotification;


public class TestAcktNotification {

	@Test
	public void testForAlarmNotification() {
		AckNotification ackNotification;
		Notification notif;
		
		ackNotification = new AckNotification();
		ackNotification.setEventType("eventType");
		ackNotification.setNodeAddress("TESTNODE");
		ackNotification.setProbableCause("PC");		
		ackNotification.setSpecificProblem("SP");
		ackNotification.setOperator("operator");
		ackNotification.setAckStatus(0);
		
		notif = ackNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),ackNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),ackNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertEquals(notif.getOperatorName(),ackNotification.getOperator());
		Assert.assertEquals(notif.getOperatorName(),"operator");
		Assert.assertEquals(notif.getProbableCause(),ackNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.ACK_NOTIFICATION);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),ackNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertNull(notif.getStringId());
		Assert.assertNull(notif.getTimeZone());
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertNull(notif.getSeverity());
		Assert.assertNull(notif.getTheTime());
		Assert.assertEquals(notif.getAckState(),ackNotification.getFMAckStatus());
		Assert.assertEquals(notif.getAckState(),AckStatus.ACKNOWLEDGED);
		
		ackNotification.setAckStatus(1);
		notif=ackNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getAckState(),ackNotification.getFMAckStatus());
		Assert.assertEquals(notif.getAckState(),AckStatus.UNACKNOWLEDGED);
		

	}

}