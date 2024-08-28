import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.AlarmSyncStartNotification;


public class TestAlarmSyncStartNotification {

	@Test
	public void testForAlarmNotification() {
		AlarmSyncStartNotification syncStartNotification;
		Notification notif;
		
		syncStartNotification = new AlarmSyncStartNotification();
		syncStartNotification.setEventType("eventType");
		syncStartNotification.setNodeAddress("TESTNODE");
		syncStartNotification.setProbableCause("PC");		
		syncStartNotification.setSpecificProblem("SP");
		notif = syncStartNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),syncStartNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),syncStartNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),syncStartNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.SYNC_START);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),syncStartNotification.getSpecificProblem());
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