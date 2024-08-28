package test.integration.ejb;

import java.util.*;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import test.integration.Artifact;

import com.ericsson.nms.fm.fm_communicator.*;
import com.ericsson.oss.fmservice.ejb.FMSStartupBean;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.service.alarm.*;

/** Arquillian test - Injecting EJB, Creating Archives and Libraries */

/**
 * @author tcsbosr
 * 
 */
@RunWith(Arquillian.class)
public class FMBeanTest {

	@Inject
	private Logger logger;

	@Inject
	private FMServiceRemote fmServRemote;

	private AlarmNotification alarmNotification;
	private ErrorNotification errorNotification;
	private ClearAllEventNotification clearAllEventNotification;
	private AlarmSyncAbortNotification alarmSyncAbortNotification;
	private AlarmSyncStartNotification alarmSyncStartNotification;
	private AlarmSyncEndNotification alarmSyncEndNotification;
	private NodeSuspendNotification nodeSuspendNotification;
	private SyncNotification syncNotification;
	private Map<String, String> otherAttributes;
	private AckRequest ackRequest;
	private AckInfo ai;

	@Inject
	@Modeled
	private EventSender<AlarmNotification> alarmNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<ClearAllEventNotification> clearAllEventNotificationeventSender;

	@Inject
	@Modeled
	private EventSender<AlarmSyncAbortNotification> alarmSyncAbortNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<AlarmSyncEndNotification> alarmSyncEndNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<AlarmSyncStartNotification> alarmSyncStartNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<SyncNotification> syncNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<ErrorNotification> errorNotificationEventSender;

	@Inject
	@Modeled
	private EventSender<NodeSuspendNotification> nodeSuspendNotificationEventSender;

	List<MediationNotification> list = new ArrayList<MediationNotification>();

	@Deployment(name = "FMServiceBeanEar")
	public static Archive<?> createTestArchive() {
		final MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		final EnterpriseArchive archive = ShrinkWrap
				.create(EnterpriseArchive.class,
						FMBeanTest.class.getSimpleName() + ".ear")
				.addAsModule(createModuleArchive())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___SERVICES_CORE_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___CONFIG_API_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___CONFIG_CORE_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___CONFIG_IMPL_DEV_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___MODELLEDEVENTBUS_API_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___EVENTBUS_API_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___MODELLEDEVENTBUS_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_FMSERVICE_API)
								.resolveAsFiles())

				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_FMSERVICE_JEE)
								.resolveAsFiles())

				.addAsLibraries(
						resolver.artifact(Artifact.JAVAX___JMS_JAR)
								.resolveAsFiles())

				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_ITPF_SDK___EVENTBUS_JMS_JAR)
								.resolveAsFiles())

				.addAsLibraries(
						resolver.artifact(Artifact.ORG_SLF4J___JAVAX_API_JAR)
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact(
								Artifact.COM_ERICSSON_OSS_MEDIATION_CORE___API_JAR)
								.resolveAsFiles())
				.addAsLibrary(createLibraryArchive());

		return archive;
	}

	/**
	 * @return
	 */
	private static Archive<?> createModuleArchive() {

		return ShrinkWrap
				.create(JavaArchive.class, "fm-service-bean-test-jee.jar")
				.addAsResource("META-INF/beans.xml", "META-INF/beans.xml")
				.addPackage(FMServiceRemote.class.getPackage().getName())
				.addPackage(EventSender.class.getPackage().getName())
				.addClass(FMSStartupBean.class);
	}

	/**
	 * @return
	 */
	private static Archive<?> createLibraryArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "fm-service-bean-test-lib.jar")
				.addAsResource("META-INF/beans.xml", "META-INF/beans.xml")
				.addPackage(FMServiceRemote.class.getPackage().getName())
				.addClass(FMBeanTest.class).addClass(FMSStartupBean.class);
	}

	@Test
	@InSequence(1)
	public void TORFM_22_Func_001() {
		logger.info("=================================Arquillian Testing TORFM_22_Func_001 For FMService ==========================");
		Assert.assertNotNull("ServiceBean should not be null", fmServRemote);

	}

	@Test
	@InSequence(2)
	public void TORFM_22_Func_002() {
		logger.info("=================================Arquillian Testing TORFM_22_Func_002 For FMService==========================");
		Assert.assertNotNull("ServiceBean should not be null", fmServRemote);
		Assert.assertEquals(
				1,
				fmServRemote
						.startSync("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST"));
	}

	@Test
	@InSequence(3)
	public void TORFM_22_Func_003() {
		logger.info("=================================Arquillian Testing TORFM_22_Func_003 For FMService==========================");
		Assert.assertNotNull("ServiceBean should not be null", fmServRemote);
		Assert.assertEquals(
				1,
				fmServRemote
						.stopSupervision("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST"));
	}

	@Test
	@InSequence(4)
	public void TORFM_22_Func_004() {
		logger.info("=================================Arquillian Testing TORFM_22_Func_004 For FMService ==========================");
		Assert.assertNotNull("ServiceBean should not be null", fmServRemote);
		final FmNeProperties fmNeProperties = null;
		final RIAData riaData = null;
		Assert.assertEquals(
				1,
				fmServRemote
						.startSupervision(
								"SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST",
								fmNeProperties, riaData));
	}

	@Test
	@InSequence(5)
	public void TORFM_22_Func_005() {
		logger.info("================================= Arquillian Testing TORFM_22_Func_005 For FMService ==========================");
		Assert.assertNotNull("ServiceBean should not be null", logger);

		ackRequest = new AckRequest();
		ackRequest.setAckStatus(AckRequest.AckStatus.ACKNOWLEDGE);
		ackRequest
				.setOor("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		ackRequest.setOperator("operator");
		ackRequest.setAlarmId("alarmId");
		Assert.assertEquals(1, fmServRemote.ackAlarm(ackRequest));
	}

	// Start of AlarmNotification Test cases with different cases

	@Test
	@InSequence(6)
	public void TORFM_21_Func_006() {
		logger.info("----- Arquillian Testing TORFM_21_Func_006 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause -CRITICAL");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem  -CRITICAL");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.CRITICAL);
		alarmNotification
				.setSourceType("AlarmNotification sourceType  -CRITICAL");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId  -CRITICAL");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2000);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string  -CRITICAL");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);
	}

	@Test
	@InSequence(7)
	public void TORFM_21_Func_007() {
		logger.info("----- Arquillian Testing TORFM_21_Func_007 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause - MAJOR");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem - MAJOR");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.MAJOR);
		alarmNotification.setSourceType("AlarmNotification sourceType - MAJOR");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId  - MAJOR");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2001);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string  - MAJOR");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);

	}

	@Test
	@InSequence(8)
	public void TORFM_21_Func_008() {
		logger.info("----- Arquillian Testing TORFM_21_Func_008 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause -MINOR");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem  -MINOR");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.MINOR);
		alarmNotification.setSourceType("AlarmNotification sourceType  -MINOR");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId  -MINOR");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2002);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string  -MINOR");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);

	}

	@Test
	@InSequence(9)
	public void TORFM_21_Func_009() {
		logger.info("----- Arquillian Testing TORFM_21_Func_009 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause --WARNING");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem --WARNING");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.WARNING);
		alarmNotification
				.setSourceType("AlarmNotification sourceType --WARNING");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId --WARNING");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2003);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string --WARNING");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);

	}

	@Test
	@InSequence(10)
	public void TORFM_21_Func_010() {
		logger.info("----- Arquillian Testing TORFM_21_Func_010 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause --CLEARED");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem --CLEARED");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.CLEARED);
		alarmNotification
				.setSourceType("AlarmNotification sourceType --CLEARED");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId --CLEARED");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2004);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string --CLEARED");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);

	}

	@Test
	@InSequence(11)
	public void TORFM_21_Func_011() {
		logger.info("----- Arquillian Testing TORFM_21_Func_011 For FMService -----");
		alarmNotification = new AlarmNotification();
		alarmNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmNotification
				.setProbableCause("AlarmNotification Samaple Probable Cause -INDETERMINATE");
		alarmNotification
				.setSpecificProblem("AlarmNotification specificProblem -INDETERMINATE");
		alarmNotification.setPerceivedSeverity(FmEventSeverity.INDETERMINATE);
		alarmNotification
				.setSourceType("AlarmNotification sourceType -INDETERMINATE");
		otherAttributes = new HashMap<String, String>();
		alarmNotification.setOtherAttributes(otherAttributes);
		alarmNotification
				.setEventAgentId("AlarmNotification eventAgentId -INDETERMINATE");
		alarmNotification.setFmMediationEventId(new FmMediationEventId());
		alarmNotification.getFmMediationEventId().setNumId(2005);
		alarmNotification.getFmMediationEventId().setStringId(
				"sample string -INDETERMINATE");
		alarmNotification.setTheTime(new Date());
		alarmNotification.setEventType("abc");
		alarmNotification.setSourceType("URAN");
		alarmNotificationEventSender.send(alarmNotification);

	}

	// End of AlarmNotification Test cases with different cases

	// Start of SyncNotification Test cases with different cases

	@Test
	@InSequence(12)
	public void TORFM_21_Func_012() {
		logger.info("----- Arquillian Testing TORFM_21_Func_012 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification.setProbableCause("SyncNotification Probable Cause");
		syncNotification.setProbableCause("SyncNotification Probable Cause");
		syncNotification.setSpecificProblem("SyncNotification SpecificProblem");
		syncNotification.setPerceivedSeverity(FmEventSeverity.CRITICAL);
		syncNotification.setSourceType("SyncNotification SourceType");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification.setEventAgentId("SyncNotification EventAgentId");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(13)
	public void TORFM_21_Func_013() {
		logger.info("----- Arquillian Testing TORFM_21_Func_013 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -MAJOR");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -MAJOR");
		syncNotification.setPerceivedSeverity(FmEventSeverity.MAJOR);
		syncNotification.setSourceType("SyncNotification SourceType -MAJOR");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -MAJOR");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -MAJOR");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(14)
	public void TORFM_21_Func_014() {
		logger.info("----- Arquillian Testing TORFM_21_Func_014 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -MINOR");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -MINOR");
		syncNotification.setPerceivedSeverity(FmEventSeverity.MINOR);
		syncNotification.setSourceType("SyncNotification SourceType -MINOR");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -MINOR");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -MINO");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -MINOR");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(15)
	public void TORFM_21_Func_015() {
		logger.info("----- Arquillian Testing TORFM_21_Func_015 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -WARNING");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -WARNING");
		syncNotification.setPerceivedSeverity(FmEventSeverity.WARNING);
		syncNotification.setSourceType("SyncNotification SourceType -WARNING");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -WARNING");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -WARNING");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -WARNING");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(16)
	public void TORFM_21_Func_016() {
		logger.info("----- Arquillian Testing TORFM_21_Func_016 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -INDETERMINATE");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -INDETERMINATE");
		syncNotification.setPerceivedSeverity(FmEventSeverity.INDETERMINATE);
		syncNotification
				.setSourceType("SyncNotification SourceType -INDETERMINATE");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -INDETERMINATE");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -INDETERMINATE");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -INDETERMINATE");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(17)
	public void TORFM_21_Func_017() {
		logger.info("----- Arquillian Testing TORFM_21_Func_017 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -CLEARED");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -CLEARED");
		syncNotification.setPerceivedSeverity(FmEventSeverity.CLEARED);
		syncNotification.setSourceType("SyncNotification SourceType -CLEARED");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -CLEARED");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -CLEARED");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -CLEARED");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(18)
	public void TORFM_21_Func_018() {
		logger.info("----- Arquillian Testing TORFM_21_Func_018 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification.setProbableCause("SyncNotification Probable Cause");
		syncNotification.setProbableCause("SyncNotification Probable Cause");
		syncNotification.setSpecificProblem("SyncNotification SpecificProblem");
		syncNotification.setPerceivedSeverity(FmEventSeverity.CRITICAL);
		syncNotification.setSourceType("SyncNotification SourceType");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification.setEventAgentId("SyncNotification EventAgentId");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.NOT_ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(19)
	public void TORFM_21_Func_019() {
		logger.info("----- Arquillian Testing TORFM_21_Func_019 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -MAJOR");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -MAJOR");
		syncNotification.setPerceivedSeverity(FmEventSeverity.MAJOR);
		syncNotification.setSourceType("SyncNotification SourceType -MAJOR");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -MAJOR");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -MAJOR");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(20)
	public void TORFM_21_Func_020() {
		logger.info("----- Arquillian Testing TORFM_21_Func_020 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -MINOR");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -MINOR");
		syncNotification.setPerceivedSeverity(FmEventSeverity.MINOR);
		syncNotification.setSourceType("SyncNotification SourceType -MINOR");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -MINOR");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.NOT_ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -MINO");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -MINOR");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(21)
	public void TORFM_21_Func_021() {
		logger.info("----- Arquillian Testing TORFM_21_Func_021 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -WARNING");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -WARNING");
		syncNotification.setPerceivedSeverity(FmEventSeverity.WARNING);
		syncNotification.setSourceType("SyncNotification SourceType -WARNING");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -WARNING");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.NOT_ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -WARNING");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -WARNING");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(22)
	public void TORFM_21_Func_022() {
		logger.info("----- Arquillian Testing TORFM_21_Func_022 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -INDETERMINATE");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -INDETERMINATE");
		syncNotification.setPerceivedSeverity(FmEventSeverity.INDETERMINATE);
		syncNotification
				.setSourceType("SyncNotification SourceType -INDETERMINATE");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -INDETERMINATE");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.NOT_ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -INDETERMINATE");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -INDETERMINATE");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(23)
	public void TORFM_21_Func_023() {
		logger.info("----- Arquillian Testing TORFM_21_Func_023 For FMService -----");
		syncNotification = new SyncNotification();
		syncNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		syncNotification
				.setProbableCause("SyncNotification Probable Cause -CLEARED");
		syncNotification
				.setSpecificProblem("SyncNotification SpecificProblem -CLEARED");
		syncNotification.setPerceivedSeverity(FmEventSeverity.CLEARED);
		syncNotification.setSourceType("SyncNotification SourceType -CLEARED");
		otherAttributes = new HashMap<String, String>();
		syncNotification.setOtherAttributes(otherAttributes);
		syncNotification
				.setEventAgentId("SyncNotification EventAgentId -CLEARED");
		ai = new AckInfo();
		ai.setAckStatus(FmMediationEventAckStatus.NOT_ACKNOWLEDGED);
		ai.setOperator("SyncNotification Operator -CLEARED");
		syncNotification.setAckInfo(ai);
		syncNotification.setFmMediationEventId(new FmMediationEventId());
		syncNotification.getFmMediationEventId().setNumId(2001);
		syncNotification.getFmMediationEventId().setStringId(
				"SyncNotification sample string -CLEARED");
		syncNotification.setTheTime(new Date());
		syncNotification.setEventType("abc");
		syncNotification.setSourceType("URAN");
		syncNotificationEventSender.send(syncNotification);

	}

	@Test
	@InSequence(24)
	public void TORFM_21_Func_024() {
		logger.info("----- Arquillian Testing TORFM_21_Func_024 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause -CRITICAL");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem -CRITICAL");
		errorNotification.setPerceivedSeverity(FmEventSeverity.CRITICAL);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId -CRITICAL");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2002);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string -CRITICAL");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc -CRITICAL");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(25)
	public void TORFM_21_Func_025() {
		logger.info("----- Arquillian Testing TORFM_21_Func_025 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause - INDETERMINATE");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem - INDETERMINATE");
		errorNotification.setPerceivedSeverity(FmEventSeverity.INDETERMINATE);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId - INDETERMINATE");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2003);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string - INDETERMINATE");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc - INDETERMINATE");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(26)
	public void TORFM_21_Func_026() {
		logger.info("----- Arquillian Testing TORFM_21_Func_026 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause - MAJOR");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem - MAJOR");
		errorNotification.setPerceivedSeverity(FmEventSeverity.MAJOR);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId - MAJOR");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2004);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string - MAJOR");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc - MAJOR");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(27)
	public void TORFM_21_Func_027() {
		logger.info("----- Arquillian Testing TORFM_21_Func_027 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause - MINOR");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem - MINOR");
		errorNotification.setPerceivedSeverity(FmEventSeverity.MINOR);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId - MINOR");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2004);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string - MINOR");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc - MINOR");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(28)
	public void TORFM_21_Func_028() {
		logger.info("----- Arquillian Testing TORFM_21_Func_028 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause - WARNING");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem - WARNING");
		errorNotification.setPerceivedSeverity(FmEventSeverity.WARNING);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId - WARNING");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2004);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string - WARNING");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc - WARNING");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(29)
	public void TORFM_21_Func_029() {
		logger.info("----- Arquillian Testing TORFM_21_Func_029 For FMService -----");
		errorNotification = new ErrorNotification();
		errorNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		errorNotification
				.setProbableCause("ErrorNotification Samaple Probable Cause - CLEARED");
		errorNotification
				.setSpecificProblem("ErrorNotification specificProblem - CLEARED");
		errorNotification.setPerceivedSeverity(FmEventSeverity.CLEARED);
		otherAttributes = new HashMap<String, String>();
		errorNotification.setOtherAttributes(otherAttributes);
		errorNotification
				.setEventAgentId("ErrorNotification eventAgentId - CLEARED");
		errorNotification.setFmMediationEventId(new FmMediationEventId());
		errorNotification.getFmMediationEventId().setNumId(2004);
		errorNotification.getFmMediationEventId().setStringId(
				"ErrorNotification sample string - CLEARED");
		errorNotification.setTheTime(new Date());
		errorNotification.setEventType("abc - CLEARED");
		errorNotification.setSourceType("URAN");
		errorNotificationEventSender.send(errorNotification);

	}

	@Test
	@InSequence(30)
	public void TORFM_21_Func_030() {
		logger.info("----- Arquillian Testing TORFM_21_Func_030 For FMService -----");
		clearAllEventNotification = new ClearAllEventNotification();
		clearAllEventNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		clearAllEventNotificationeventSender.send(clearAllEventNotification);
	}

	@Test
	@InSequence(31)
	public void TORFM_21_Func_031() {
		logger.info("----- Arquillian Testing TORFM_21_Func_031 For FMService -----");
		alarmSyncAbortNotification = new AlarmSyncAbortNotification();
		alarmSyncAbortNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmSyncAbortNotificationEventSender.send(alarmSyncAbortNotification);

	}

	@Test
	@InSequence(32)
	public void TORFM_21_Func_032() {
		logger.info("----- Arquillian Testing TORFM_21_Func_032 For FMService -----");
		alarmSyncStartNotification = new AlarmSyncStartNotification();
		alarmSyncStartNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmSyncStartNotificationEventSender.send(alarmSyncStartNotification);

	}

	@Test
	@InSequence(33)
	public void TORFM_21_Func_033() {
		logger.info("----- Arquillian Testing TORFM_21_Func_033 For FMService -----");
		alarmSyncEndNotification = new AlarmSyncEndNotification();
		alarmSyncEndNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		alarmSyncEndNotificationEventSender.send(alarmSyncEndNotification);

	}

	@Test
	@InSequence(34)
	public void TORFM_21_Func_034() {
		logger.info("----- Arquillian Testing TORFM_21_Func_034 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -CRITICAL");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -CRITICAL");
		nodeSuspendNotification.setPerceivedSeverity(FmEventSeverity.CRITICAL);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -CRITICAL");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -CRITICAL");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -CRITICAL");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}

	@Test
	@InSequence(35)
	public void TORFM_21_Func_035() {
		logger.info("----- Arquillian Testing TORFM_21_Func_035 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -INDETERMINATE");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -INDETERMINATE");
		nodeSuspendNotification
				.setPerceivedSeverity(FmEventSeverity.INDETERMINATE);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -INDETERMINATE");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -INDETERMINATE");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -INDETERMINATE");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}

	@Test
	@InSequence(36)
	public void TORFM_21_Func_036() {
		logger.info("----- Arquillian Testing TORFM_21_Func_036 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -MAJOR");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -MAJOR");
		nodeSuspendNotification.setPerceivedSeverity(FmEventSeverity.MAJOR);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -MAJOR");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -MAJOR");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -MAJOR");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}

	@Test
	@InSequence(37)
	public void TORFM_21_Func_037() {
		logger.info("----- Arquillian Testing TORFM_21_Func_037 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -MINOR");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -MINOR");
		nodeSuspendNotification.setPerceivedSeverity(FmEventSeverity.MINOR);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -MINOR");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -MINOR");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -MINOR");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}

	@Test
	@InSequence(38)
	public void TORFM_21_Func_038() {
		logger.info("----- Arquillian Testing TORFM_21_Func_038 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -WARNING");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -WARNING");
		nodeSuspendNotification.setPerceivedSeverity(FmEventSeverity.WARNING);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -WARNING");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -WARNING");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -WARNING");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}

	@Test
	@InSequence(39)
	public void TORFM_21_Func_039() {
		logger.info("----- Arquillian Testing TORFM_21_Func_039 For FMService -----");
		nodeSuspendNotification = new NodeSuspendNotification();
		nodeSuspendNotification
				.setNodeAddress("SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST");
		nodeSuspendNotification
				.setProbableCause("NodeSuspendNotification Samaple Probable Cause -CLEARED");
		nodeSuspendNotification
				.setSpecificProblem("NodeSuspendNotification specificProblem -CLEARED");
		nodeSuspendNotification.setPerceivedSeverity(FmEventSeverity.CLEARED);
		otherAttributes = new HashMap<String, String>();
		nodeSuspendNotification.setOtherAttributes(otherAttributes);
		nodeSuspendNotification
				.setEventAgentId("NodeSuspendNotification eventAgentId -CLEARED");
		nodeSuspendNotification.setFmMediationEventId(new FmMediationEventId());
		nodeSuspendNotification.getFmMediationEventId().setNumId(20010);
		nodeSuspendNotification.getFmMediationEventId().setStringId(
				"NodeSuspendNotification sample string -CLEARED");
		nodeSuspendNotification.setTheTime(new Date());
		nodeSuspendNotification.setEventType("abc - -CLEARED");
		nodeSuspendNotification.setSourceType("URAN");
		nodeSuspendNotificationEventSender.send(nodeSuspendNotification);

	}
}
