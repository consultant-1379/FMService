import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.AlarmSyncEndNotification;


public class TestAlarmSyncEndtNotification {

	@Test
	public void testForAlarmNotification() {
		AlarmSyncEndNotification syncEndNotification;
		Notification notif;
		
		syncEndNotification = new AlarmSyncEndNotification();
		syncEndNotification.setEventType("eventType");
		syncEndNotification.setNodeAddress("TESTNODE");
		syncEndNotification.setProbableCause("PC");		
		syncEndNotification.setSpecificProblem("SP");
		notif = syncEndNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),syncEndNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),syncEndNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),syncEndNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.SYNC_END);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),syncEndNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertNull(notif.getStringId());
		Assert.assertNull(notif.getTimeZone());
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getAckState());
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertNull(notif.getSeverity());
		Assert.assertNull(notif.getTheTime());
		

	}

}