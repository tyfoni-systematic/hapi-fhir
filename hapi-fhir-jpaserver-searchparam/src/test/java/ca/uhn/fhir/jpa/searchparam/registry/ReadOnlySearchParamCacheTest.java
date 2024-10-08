package ca.uhn.fhir.jpa.searchparam.registry;

import org.junit.jupiter.api.Test;

import static ca.uhn.fhir.jpa.searchparam.registry.ReadOnlySearchParamCache.searchParamMatchesAtLeastOnePattern;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadOnlySearchParamCacheTest {

	@Test
	void testSearchParamMatchesAtLeastOnePattern() {
		assertTrue(searchParamMatchesAtLeastOnePattern(newHashSet("*"), "Patient", "name"));
		assertTrue(searchParamMatchesAtLeastOnePattern(newHashSet("Patient:name"), "Patient", "name"));
		assertTrue(searchParamMatchesAtLeastOnePattern(newHashSet("Patient:*"), "Patient", "name"));
		assertTrue(searchParamMatchesAtLeastOnePattern(newHashSet("*:name"), "Patient", "name"));
		assertFalse(searchParamMatchesAtLeastOnePattern(newHashSet("Patient:foo"), "Patient", "name"));
		assertFalse(searchParamMatchesAtLeastOnePattern(newHashSet("Foo:name"), "Patient", "name"));
	}


	@Test
	void testSearchParamMatchesAtLeastOnePattern_InvalidPattern() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> searchParamMatchesAtLeastOnePattern(newHashSet("aaa"), "Patient", "name"));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> searchParamMatchesAtLeastOnePattern(newHashSet(":name"), "Patient", "name"));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> searchParamMatchesAtLeastOnePattern(newHashSet("Patient:"), "Patient", "name"));
	}

}

