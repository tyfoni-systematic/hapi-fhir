package ca.uhn.fhir.i18n;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HapiLocalizerTest {

	@Test
	public void testEscapePatterns() {
		HapiLocalizer loc = new HapiLocalizer();

		assertEquals("some message", loc.newMessageFormat("some message").format(new Object[]{}));
		assertEquals("var1 {var2} var3 {var4}", loc.newMessageFormat("var1 {var2} var3 {var4}").format(new Object[]{}));
		assertEquals("var1 A var3 B", loc.newMessageFormat("var1 {0} var3 {1}").format(new Object[]{"A", "B"}));
	}

	
	@Test
	public void testAllKeys() {
		HapiLocalizer svc = new HapiLocalizer();
		Set<String> allKeys = svc.getAllKeys();
		assertThat(allKeys).isNotEmpty();
		
		for (String next : allKeys) {
			svc.getMessage(next);
		}
	}

	@Test
	public void testGetVersion() {
		HapiLocalizer svc = new HapiLocalizer();
		String version = svc.getMessage("hapi.version");
		assertThat(version).matches("[0-9]+.*");
	}
	
}
