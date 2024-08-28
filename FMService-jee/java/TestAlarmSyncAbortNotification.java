import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.AlarmSyncAbortNotification;


public class TestAlarmSyncAbortNotification {

	@Test
	public void testForAlarmNotification() {
		AlarmSyncAbortNotification syncAbortNotification;
		Notification notif;
		
		syncAbortNotification = new AlarmSyncAbortNotification();
		syncAbortNotification.setEventType("eventType");
		syncAbortNotification.setNodeAddress("TESTNODE");
		syncAbortNotification.setProbableCause("PC");		
		syncAbortNotification.setSpecificProblem("SP");
		notif = syncAbortNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),syncAbortNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),syncAbortNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),syncAbortNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.SYNC_ABORT);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),syncAbortNotification.getSpecificProblem());
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