import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.nms.fm.fm_communicator.Notification.EventState;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.EventCloseNotification;


public class TestEventCloseNotification {

	@Test
	public void testForAlarmNotification() {
		EventCloseNotification eventCloseNotification;
		Notification notif;
		
		eventCloseNotification = new EventCloseNotification();
		eventCloseNotification.setEventType("eventType");
		eventCloseNotification.setNodeAddress("TESTNODE");
		eventCloseNotification.setProbableCause("PC");		
		eventCloseNotification.setSpecificProblem("SP");
		eventCloseNotification.setOperator("operator");
		eventCloseNotification.setOrginatorName("orginatorName");
		eventCloseNotification.setPreviousEventState(0);
		
		
		notif = eventCloseNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),eventCloseNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),eventCloseNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");

		Assert.assertEquals(notif.getOperatorName(),eventCloseNotification.getOperator());
		Assert.assertEquals(notif.getOperatorName(),"operator");
		Assert.assertEquals(notif.getOriginatorName(),eventCloseNotification.getOrginatorName());
		Assert.assertEquals(notif.getOriginatorName(),"orginatorName");
		
		Assert.assertEquals(notif.getProbableCause(),eventCloseNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getNotificationType(),NotificationType.CLOSE_NOTIFICATION);

		Assert.assertNull(notif.getSourceType());
		Assert.assertEquals(notif.getSpecificProblem(),eventCloseNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertNull(notif.getStringId());
		Assert.assertNull(notif.getTimeZone());
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getAckState());
		Assert.assertNull(notif.getSeverity());
		Assert.assertNull(notif.getTheTime());
		Assert.assertEquals(notif.getPreviousEventState(),eventCloseNotification.getEventState());
		Assert.assertEquals(notif.getPreviousEventState(),EventState.ACTIVE_ACKNOWLEDGED);
		
		eventCloseNotification.setPreviousEventState(1);
		notif = eventCloseNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getPreviousEventState(),eventCloseNotification.getEventState());
		Assert.assertEquals(notif.getPreviousEventState(),EventState.ACTIVE_UNACKNOWLEDGED);
		
		eventCloseNotification.setPreviousEventState(2);
		notif = eventCloseNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getPreviousEventState(),eventCloseNotification.getEventState());
		Assert.assertEquals(notif.getPreviousEventState(),EventState.CLEARED_ACKNOWLEDGED);
		
		eventCloseNotification.setPreviousEventState(3);
		notif = eventCloseNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getPreviousEventState(),eventCloseNotification.getEventState());
		Assert.assertEquals(notif.getPreviousEventState(),EventState.CLEARED_UNACKNOWLEDGED);
		
		eventCloseNotification.setPreviousEventState(4);
		notif = eventCloseNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getPreviousEventState(),eventCloseNotification.getEventState());
		Assert.assertEquals(notif.getPreviousEventState(),EventState.CLOSED);
		

	}

}