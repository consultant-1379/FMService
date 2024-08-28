import java.util.*;

import org.junit.*;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.nms.fm.fm_communicator.Notification.EventSeverity;
import com.ericsson.nms.fm.fm_communicator.Notification.NotificationType;
import com.ericsson.oss.services.fm.service.alarm.*;
public class TestAlarmNotification {

	private static final char NULL_CHAR = Character.forDigit(0, 10);
	private static final char[] NULL_CHAR_ARRAY ={NULL_CHAR};
	private static final String NULL_STRING = new String(NULL_CHAR_ARRAY);
	@Test
	public void testForAlarmNotification() {
		AlarmNotification alarmNotification;
		Date theTime;
		Notification notif;
		
		theTime=new Date();
		alarmNotification = new AlarmNotification();
		alarmNotification.setEventAgentId("eventAgentId");
		alarmNotification.setEventType("eventType");
		alarmNotification.setNodeAddress("TESTNODE");
		alarmNotification.setNumId(0);
		alarmNotification.setPerceivedSeverity("CRITICAL");
		alarmNotification.setProbableCause("PC");		
		alarmNotification.setSourceType("sourceType");
		alarmNotification.setSpecificProblem("SP");
		alarmNotification.setStringId("stringId");
		alarmNotification.setTheTime(theTime);
		alarmNotification.setTimeZone("UTC");
		notif = alarmNotification.getNotificationForOSS();
		Assert.assertNotNull(notif);
		Assert.assertEquals(notif.getEventType(),alarmNotification.getEventType());
		Assert.assertEquals(notif.getEventType(),"eventType");
		Assert.assertEquals(notif.getManagedObjectInstance(),alarmNotification.getNodeAddress());
		Assert.assertEquals(notif.getManagedObjectInstance(),"TESTNODE");
		Assert.assertEquals(notif.getNumId(),alarmNotification.getNumId());
		Assert.assertEquals(notif.getNumId(),0);
		Assert.assertNull(notif.getOperatorName());
		Assert.assertEquals(notif.getProbableCause(),alarmNotification.getProbableCause());
		Assert.assertEquals(notif.getProbableCause(),"PC");
		Assert.assertEquals(notif.getSourceType(),alarmNotification.getSourceType());
		Assert.assertEquals(notif.getSourceType(),"sourceType");
		Assert.assertEquals(notif.getSpecificProblem(),alarmNotification.getSpecificProblem());
		Assert.assertEquals(notif.getSpecificProblem(),"SP");
		Assert.assertEquals(notif.getStringId(),alarmNotification.getStringId());
		Assert.assertEquals(notif.getStringId(),"stringId");
		Assert.assertEquals(notif.getTimeZone(),alarmNotification.getTimeZone());
		Assert.assertEquals(notif.getTimeZone(),"UTC");
		Assert.assertNull(notif.getTopObjectName());
		Assert.assertNull(notif.getAckState());
		Assert.assertEquals(notif.getNotificationType(),NotificationType.ALARM_EVENT);
		Assert.assertNull(notif.getPreviousEventState());
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CRITICAL);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		Assert.assertEquals(notif.getTheTime(),alarmNotification.getTheTime());
		Assert.assertEquals(notif.getTheTime(),theTime);
		
		alarmNotification.setPerceivedSeverity("MAJOR");
		notif=alarmNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MAJOR);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		
		alarmNotification.setPerceivedSeverity("MINOR");
		notif=alarmNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.MINOR);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		
		alarmNotification.setPerceivedSeverity("WARNING");
		notif=alarmNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.WARNING);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		
		alarmNotification.setPerceivedSeverity("INDETERMINATE");
		notif=alarmNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.INDERMINATE);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		
		alarmNotification.setPerceivedSeverity("CLEARED");
		notif=alarmNotification.getNotificationForOSS();
		Assert.assertEquals(notif.getSeverity(),EventSeverity.CLEARED);
		Assert.assertEquals(notif.getSeverity(),alarmNotification.getEventSeverity());
		
	}

	@Test
	public void testOtherAttributes() {
		AlarmNotification alarmNotification;
		alarmNotification = new AlarmNotification();
		
		Map<String, String>otherAttributesMap=new HashMap<String, String>();
		otherAttributesMap.put("A", "A_Value");
		otherAttributesMap.put("B", "B_Value");
		otherAttributesMap.put("C", "C_Value");
		String stringWithNull=new String("A");
		stringWithNull+=Character.forDigit(0, 10);
		stringWithNull+="BC";
		otherAttributesMap.put(stringWithNull,"Null_Value");
		otherAttributesMap.put("D",null);
		alarmNotification.setOtherAttributesFromMap(otherAttributesMap);
		Map<String, String>otherAttributesReturnedMap=alarmNotification.getOtherAttributesMap();		
		Assert.assertEquals(otherAttributesReturnedMap.get("A"),"A_Value");
		Assert.assertEquals(otherAttributesReturnedMap.get("B"),"B_Value");
		Assert.assertEquals(otherAttributesReturnedMap.get("C"),"C_Value");
		Assert.assertEquals(otherAttributesReturnedMap.get("A*BC"),"Null_Value");
		Assert.assertEquals(otherAttributesReturnedMap.get("D"),"");		
	}
	@Test
	public void testOtherAttributesNull() {
		AlarmNotification alarmNotification;
		alarmNotification = new AlarmNotification();
		alarmNotification.setOtherAttributes(null);
		Map<String, String>otherAttributesReturnedMap=alarmNotification.getOtherAttributesMap();
		Assert.assertNotNull(otherAttributesReturnedMap);
	}
	@Test
	public void testOtherAttributesBlankMap() {
		AlarmNotification alarmNotification;
		alarmNotification = new AlarmNotification();
		alarmNotification.setOtherAttributesFromMap(new HashMap<String,String>());
		Map<String, String>otherAttributesReturnedMap=alarmNotification.getOtherAttributesMap();
		Assert.assertNotNull(otherAttributesReturnedMap);
	}

}