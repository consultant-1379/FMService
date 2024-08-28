import org.junit.Assert;
import org.junit.Test;

import com.ericsson.oss.services.fm.service.util.FDNConverter;

public class TestFDNConverter {

	@Test
	public void testOssFdnToTorFdn1() {
		final String fdn = "SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST";
		Assert.assertEquals("NW=ONRM_ROOT_MO,SN=RNC_TEST,MeC=RNC_TEST",
				FDNConverter.convertOssFdnToTorFdn(fdn));
	}

	@Test
	public void testOssFdnToTorFdn2() {
		final String fdn = "SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST,ManagedElement=1";
		Assert.assertEquals(
				"NW=ONRM_ROOT_MO,SN=RNC_TEST,MeC=RNC_TEST,ManagedElement=1",
				FDNConverter.convertOssFdnToTorFdn(fdn));
	}

	@Test
	public void testTorFdnToOssFdn1() {
		final String fdn = "NW=ONRM_ROOT_MO,SN=RNC_TEST,MeC=RNC_TEST";
		Assert.assertEquals(
				"SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST",
				FDNConverter.convertTorFdnToOssFdn(fdn));
	}

	@Test
	public void testTorFdnToOssFdn2() {
		final String fdn = "NW=ONRM_ROOT_MO,SN=RNC_TEST,MeC=RNC_TEST,ManagedElement=1";
		Assert.assertEquals(
				"SubNetwork=ONRM_ROOT_MO,SubNetwork=RNC_TEST,MeContext=RNC_TEST,ManagedElement=1",
				FDNConverter.convertTorFdnToOssFdn(fdn));
	}


}