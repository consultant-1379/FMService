import org.junit.*;

import com.ericsson.oss.mediation.translator.model.*;
import com.ericsson.oss.services.fm.service.alarm.*;
import com.ericsson.oss.services.fm.service.util.ConvertEventNotificationToAlarm;


public class TestConvertEventNotificationToAlarm {

	private AlarmNotification alarmNotification;
	private EventNotification eventNotification;
	private ErrorNotification errorNotification;
	private NodeSuspendNotification nodeSuspendNotification;
	
	
	
	@Test
	public void testForAlarmNotification() {
		Assert.assertNotNull(this.alarmNotification);
		Assert.assertEquals("sourceType",this.alarmNotification.getSourceType());
		Assert.assertEquals("eventAgentID",
				this.alarmNotification.getEventAgentId());
		Assert.assertEquals("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST", this.alarmNotification
				.getNodeAddress());
		Assert.assertEquals("specificProblem", this.alarmNotification.getSpecificProblem());
		Assert.assertEquals("ProbableCause", this.alarmNotification.getProbableCause());
		Assert.assertEquals("UTC", this.alarmNotification.getTimeZone());

	}


	@Test
	public void testForErrorNotification() {
	
		Assert.assertNotNull(this.errorNotification);
		Assert.assertEquals("sourceType",this.errorNotification.getSourceType());
		Assert.assertEquals("eventAgentID",
				this.errorNotification.getEventAgentId());
		Assert.assertEquals("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST", this.errorNotification
				.getNodeAddress());
		Assert.assertEquals("specificProblem", this.errorNotification.getSpecificProblem());
		Assert.assertEquals("ProbableCause", this.errorNotification.getProbableCause());
		Assert.assertEquals("UTC", this.errorNotification.getTimeZone());

	}

	

	@Test
	public void testForNodeSuspendNotification() {
		Assert.assertNotNull(this.nodeSuspendNotification);
		Assert.assertEquals("sourceType",this.nodeSuspendNotification.getSourceType());
		Assert.assertEquals("eventAgentID",
				this.nodeSuspendNotification.getEventAgentId());
		Assert.assertEquals("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST", this.nodeSuspendNotification
				.getNodeAddress());
		Assert.assertEquals("specificProblem", this.nodeSuspendNotification.getSpecificProblem());
		Assert.assertEquals("ProbableCause", this.nodeSuspendNotification.getProbableCause());
		Assert.assertEquals("UTC", this.nodeSuspendNotification.getTimeZone());

	}

	@Before
	public void setUp() {
		
		this.eventNotification = new EventNotification();
		this.eventNotification.setSeverity("CRITICAL");
		this.eventNotification.setSourceType("sourceType");
		this.eventNotification.setEventAgentId("eventAgentId");
		this.eventNotification.setFmEventType("ALARM");
		this.eventNotification.setEventAgentId("eventAgentID");
		this.eventNotification.setManagedObjectInstance("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		this.eventNotification.setSpecificProblem("specificProblem");
		this.eventNotification.setProbableCause("ProbableCause");
		this.eventNotification.setTimeZone("UTC");
		
		alarmNotification = (AlarmNotification)ConvertEventNotificationToAlarm.converEventNotificationToAlarmNotification(eventNotification);
		this.eventNotification = new EventNotification();
		this.eventNotification.setSeverity("CRITICAL");
		this.eventNotification.setSourceType("sourceType");
		this.eventNotification.setEventAgentId("eventAgentId");
		this.eventNotification.setFmEventType("ERROR");
		this.eventNotification.setEventAgentId("eventAgentID");
		this.eventNotification.setManagedObjectInstance("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		this.eventNotification.setSpecificProblem("specificProblem");
		this.eventNotification.setProbableCause("ProbableCause");
		this.eventNotification.setTimeZone("UTC");
		errorNotification = (ErrorNotification) ConvertEventNotificationToAlarm.converEventNotificationToAlarmNotification(eventNotification);
		
		this.eventNotification = new EventNotification();
		this.eventNotification.setSeverity("CRITICAL");
		this.eventNotification.setSourceType("sourceType");
		this.eventNotification.setEventAgentId("eventAgentId");
		this.eventNotification.setFmEventType("NODESUSPENDED");
		this.eventNotification.setEventAgentId("eventAgentID");
		this.eventNotification.setManagedObjectInstance("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		this.eventNotification.setSpecificProblem("specificProblem");
		this.eventNotification.setProbableCause("ProbableCause");
		this.eventNotification.setTimeZone("UTC");
		nodeSuspendNotification = (NodeSuspendNotification) ConvertEventNotificationToAlarm.converEventNotificationToAlarmNotification(eventNotification);
	}


}