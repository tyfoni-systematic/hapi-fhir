package ca.uhn.fhir.jpa.model.entity;

import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ResourceIndexedSearchParamDateTest {

	private Date date1A, date1B, date2A, date2B;
	private Timestamp timestamp1A, timestamp1B, timestamp2A, timestamp2B;

	@BeforeEach
	public void setUp() throws Exception {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(1970, 01, 01, 10, 23, 33);

		Calendar cal2 = Calendar.getInstance();
		cal2.set(1990, 01, 01, 5, 11, 0);

		date1A = cal1.getTime();
		date1B = cal1.getTime();
		date2A = cal2.getTime();
		date2B = cal2.getTime();

		timestamp1A = new Timestamp(date1A.getTime());
		timestamp1B = new Timestamp(date1B.getTime());
		timestamp2A = new Timestamp(date2A.getTime());
		timestamp2B = new Timestamp(date2B.getTime());
	}

	@Test
	public void equalsIsTrueForMatchingNullDates() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", null, null, null, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", null, null, null, null, "SomeValue");

		validateEquals(param, param2);
	}

	@Test
	public void equalsIsTrueForMatchingDates() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1B, null, date2B, null, "SomeValue");

		validateEquals(param, param2);
	}

	@Test
	public void equalsIsTrueForMatchingTimeStampsThatMatch() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp1A, null, timestamp2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp1B, null, timestamp2B, null, "SomeValue");

		validateEquals(param, param2);
	}

	// Scenario that occurs when updating a resource with a date search parameter. One date will be a java.util.Date, the
	// other will be equivalent but will be a java.sql.Timestamp. Equals should work in both directions.
	@Test
	public void equalsIsTrueForMixedTimestampsAndDates() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp1A, null, timestamp2A, null, "SomeValue");

		validateEquals(param, param2);
	}

	@Test
	public void equalsIsTrueForOptimizedSearchParam() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");

		param2.optimizeIndexStorage();

		validateEquals(param, param2);
	}

	private void validateEquals(ResourceIndexedSearchParamDate theParam, ResourceIndexedSearchParamDate theParam2) {
		assertEquals(theParam, theParam2);
		assertEquals(theParam2, theParam);
		assertEquals(theParam.hashCode(), theParam2.hashCode());
	}

	@Test
	public void equalsIsFalseForNonMatchingDates() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date2A, null, date1A, null, "SomeValue");

		validateNotEquals(param, param2);
	}

	@Test
	public void equalsIsFalseForNonMatchingDatesNullCase() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", null, null, null, null, "SomeValue");

		validateNotEquals(param, param2);
	}

	@Test
	public void equalsIsFalseForNonMatchingTimeStamps() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp1A, null, timestamp2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp2A, null, timestamp1A, null, "SomeValue");

		validateNotEquals(param, param2);
	}

	@Test
	public void equalsIsFalseForMixedTimestampsAndDatesThatDoNotMatch() {
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", date1A, null, date2A, null, "SomeValue");
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(), "Patient", "SomeResource", timestamp2A, null, timestamp1A, null, "SomeValue");

		validateNotEquals(param, param2);
	}

	private void validateNotEquals(ResourceIndexedSearchParamDate theParam, ResourceIndexedSearchParamDate theParam2) {
		assertNotEquals(theParam, theParam2);
		assertNotEquals(theParam2, theParam);
		assertThat(theParam2.hashCode()).isNotEqualTo(theParam.hashCode());
	}


	@Test
	public void testEqualsAndHashCode_withSameParams_equalsIsTrueAndHashCodeIsSame() {
		ResourceIndexedSearchParamDate val1 = new ResourceIndexedSearchParamDate()
			.setValueHigh(new Date(100000000L))
			.setValueLow(new Date(111111111L));
		val1.setPartitionSettings(new PartitionSettings());
		val1.calculateHashes();
		ResourceIndexedSearchParamDate val2 = new ResourceIndexedSearchParamDate()
			.setValueHigh(new Date(100000000L))
			.setValueLow(new Date(111111111L));
		val2.setPartitionSettings(new PartitionSettings());
		val2.calculateHashes();
		validateEquals(val1, val2);
	}

	@ParameterizedTest
	@CsvSource({
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, " +
		"Observation, param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, ResourceType is different",
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false,     " +
		"Patient,     name,  2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, ParamName is different",
		"Patient,     param, 2017-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, " +
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, LowDate is different",
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, " +
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2020-04-25T14:05:15.953Z, false, HighDate is different",
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, true, " +
		"Patient,     param, 2018-04-25T14:05:15.953Z, 2019-04-25T14:05:15.953Z, false, Missing is different",
	})
	public void testEqualsAndHashCode_withDifferentParams_equalsIsFalseAndHashCodeIsDifferent(String theFirstResourceType,
																							  String theFirstParamName,
																							  String theFirstLowDate,
																							  String theFirstHighDate,
																							  boolean theFirstMissing,
																							  String theSecondResourceType,
																							  String theSecondParamName,
																							  String theSecondLowDate,
																							  String theSecondHighDate,
																							  boolean theSecondMissing,
																							  String theMessage) {
		Date firstLowDate = Date.from(Instant.parse(theFirstLowDate));
		Date firstHighDate = Date.from(Instant.parse(theFirstHighDate));
		ResourceIndexedSearchParamDate param = new ResourceIndexedSearchParamDate(new PartitionSettings(),
			theFirstResourceType, theFirstParamName, firstLowDate, theFirstLowDate, firstHighDate, theFirstHighDate, null);
		param.setMissing(theFirstMissing);

		Date secondLowDate = Date.from(Instant.parse(theSecondLowDate));
		Date secondHighDate = Date.from(Instant.parse(theSecondHighDate));
		ResourceIndexedSearchParamDate param2 = new ResourceIndexedSearchParamDate(new PartitionSettings(),
			theSecondResourceType, theSecondParamName, secondLowDate, theSecondLowDate, secondHighDate, theSecondHighDate, null);
		param2.setMissing(theSecondMissing);

		assertNotEquals(param, param2, theMessage);
		assertNotEquals(param2, param, theMessage);
		assertNotEquals(param.hashCode(), param2.hashCode(), theMessage);
	}
}
