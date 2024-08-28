import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.Notification;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.ClearAllEventNotification;


public class TestClearAllEventNotificationtNotification {

	@Test
	public void testForAlarmNotification() {
		ClearAllEventNotification clearAllEventNotification;
		Notification notif;
		
		clearAllEventNotification = new ClearAllEventNotification();
		clearAllEventNotification.setEventType("eventType");
		clearAllEventNotification.setNodeAddress("TESTNODE");
		clearAllEventNotification.setProbableCause("PC");		
		clearAllEventNotification.setSpecificProblem("SP");
		clearAllEventNotification.setTopObjectName("topObjectName");
		
		notif = clearAllEventNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),clearAllEventNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),clearAllEventNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),clearAllEventNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.CLEAR_ALL_EVENT);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),clearAllEventNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertNull(notif.getStringId());
		Assert.assertNull(notif.getTimeZone());
		Assert.assertEquals(notif.getTopObjectName() , clearAllEventNotification.getTopObjectName());
		Assert.assertEquals(notif.getTopObjectName() , "topObjectName");
		Assert.assertNull(notif.getAckState());
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertNull(notif.getSeverity());
		Assert.assertNull(notif.getTheTime());
		

	}

}