package ca.uhn.fhir.parser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.parser.PatientWithNestedBlockExtensionR4.ItemExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FUT1-25450 accept Extension.id on block-typed declared extensions.
 *
 * <p>An {@link org.hl7.fhir.r4.model.Extension} inherits {@code Element.id} from {@code Element}, so an
 * element id is legal on any extension. For declared extensions modelled as a {@code @Block} the id
 * has exactly one home in Java, JSON and XML, and these tests pin that it survives a round trip in
 * both encodings and under the strict error handler.
 *
 * <p>Declared extensions holding a single {@code value[x]} have no such home and keep rejecting the
 * id, which is pinned here too.
 */
public class DeclaredExtensionIdR4Test {

	private static final FhirContext ourCtx = FhirContext.forR4();

	/**
	 * The id may appear before or after the child extensions - JSON property order must not matter.
	 */
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void testParseJson_blockExtensionWithId_idIsPreserved(boolean theIdFirst) {
		String idProperty = "\"id\": \"item-id-1\"";
		String childProperties = "\"url\": \"http://acme.org/item\",\n"
				+ "      \"extension\": [ {\n"
				+ "        \"url\": \"description\",\n"
				+ "        \"valueString\": \"Group #1\"\n"
				+ "      } ]";
		String input = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"extension\": [ {\n"
				+ "      " + (theIdFirst ? idProperty + ",\n      " + childProperties : childProperties + ",\n      " + idProperty) + "\n"
				+ "  } ]\n"
				+ "}";

		PatientWithNestedBlockExtensionR4 parsed = parseJsonStrict(input);

		ItemExtension item = parsed.getItem();
		assertNotNull(item);
		assertEquals("item-id-1", item.getId());
		assertEquals("Group #1", item.getDescription().getValue());

		String encoded = ourCtx.newJsonParser().encodeResourceToString(parsed);
		assertThat(encoded).contains("\"id\":\"item-id-1\"");
		assertEquals("item-id-1", parseJsonStrict(encoded).getItem().getId());
	}

	@Test
	public void testParseJson_nestedBlockExtensionWithId_idIsPreserved() {
		String input = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"extension\": [ {\n"
				+ "    \"id\": \"item-id-1\",\n"
				+ "    \"url\": \"http://acme.org/item\",\n"
				+ "    \"extension\": [ {\n"
				+ "      \"id\": \"linkage-id-1\",\n"
				+ "      \"url\": \"linkage\",\n"
				+ "      \"extension\": [ {\n"
				+ "        \"url\": \"reference\",\n"
				+ "        \"valueString\": \"Questionnaire/1\"\n"
				+ "      } ]\n"
				+ "    } ]\n"
				+ "  } ]\n"
				+ "}";

		PatientWithNestedBlockExtensionR4 parsed = parseJsonStrict(input);

		assertEquals("item-id-1", parsed.getItem().getId());
		assertEquals("linkage-id-1", parsed.getItem().getLinkage().getId());
		assertEquals("Questionnaire/1", parsed.getItem().getLinkage().getReference().getValue());

		String encoded = ourCtx.newJsonParser().encodeResourceToString(parsed);
		assertThat(encoded).contains("\"id\":\"item-id-1\"").contains("\"id\":\"linkage-id-1\"");

		PatientWithNestedBlockExtensionR4 reparsed = parseJsonStrict(encoded);
		assertEquals("item-id-1", reparsed.getItem().getId());
		assertEquals("linkage-id-1", reparsed.getItem().getLinkage().getId());
	}

	/**
	 * An id on an extension the model does not declare has always worked - make sure the change to the
	 * declared path leaves it alone.
	 */
	@Test
	public void testParseJson_undeclaredExtensionWithId_idIsPreserved() {
		String input = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"extension\": [ {\n"
				+ "    \"id\": \"other-id-1\",\n"
				+ "    \"url\": \"http://acme.org/something-else\",\n"
				+ "    \"valueString\": \"hello\"\n"
				+ "  } ]\n"
				+ "}";

		PatientWithNestedBlockExtensionR4 parsed = parseJsonStrict(input);

		assertEquals(
				"other-id-1", parsed.getExtensionByUrl("http://acme.org/something-else").getId());
		assertThat(ourCtx.newJsonParser().encodeResourceToString(parsed)).contains("\"id\":\"other-id-1\"");
	}

	@Test
	public void testParseXml_blockExtensionWithId_idIsPreserved() {
		String input = "<Patient xmlns=\"http://hl7.org/fhir\">\n"
				+ "  <extension id=\"item-id-1\" url=\"http://acme.org/item\">\n"
				+ "    <extension url=\"description\">\n"
				+ "      <valueString value=\"Group #1\"/>\n"
				+ "    </extension>\n"
				+ "    <extension id=\"linkage-id-1\" url=\"linkage\">\n"
				+ "      <extension url=\"reference\">\n"
				+ "        <valueString value=\"Questionnaire/1\"/>\n"
				+ "      </extension>\n"
				+ "    </extension>\n"
				+ "  </extension>\n"
				+ "</Patient>";

		PatientWithNestedBlockExtensionR4 parsed = parseXmlStrict(input);

		assertEquals("item-id-1", parsed.getItem().getId());
		assertEquals("Group #1", parsed.getItem().getDescription().getValue());
		assertEquals("linkage-id-1", parsed.getItem().getLinkage().getId());

		String encoded = ourCtx.newXmlParser().encodeResourceToString(parsed);
		assertThat(encoded).contains("id=\"item-id-1\"").contains("id=\"linkage-id-1\"");

		PatientWithNestedBlockExtensionR4 reparsed = parseXmlStrict(encoded);
		assertEquals("item-id-1", reparsed.getItem().getId());
		assertEquals("linkage-id-1", reparsed.getItem().getLinkage().getId());
	}

	/**
	 * A block extension carrying only an id still produces the block, so the id is not silently lost.
	 */
	@Test
	public void testParseJson_blockExtensionWithIdAndNoChildren_blockIsCreated() {
		String input = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"extension\": [ {\n"
				+ "    \"id\": \"item-id-1\",\n"
				+ "    \"url\": \"http://acme.org/item\"\n"
				+ "  } ]\n"
				+ "}";

		PatientWithNestedBlockExtensionR4 parsed = parseJsonStrict(input);

		assertNotNull(parsed.getItem());
		assertEquals("item-id-1", parsed.getItem().getId());
	}

	/**
	 * Out of scope: a declared extension with a single value[x] has no object of its own to hang the id
	 * on, so it keeps failing exactly as before.
	 */
	@Test
	public void testParseJson_valueTypedExtensionWithId_stillRejected() {
		String input = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"extension\": [ {\n"
				+ "    \"id\": \"label-id-1\",\n"
				+ "    \"url\": \"http://acme.org/label\",\n"
				+ "    \"valueString\": \"hello\"\n"
				+ "  } ]\n"
				+ "}";

		DataFormatException e = assertThrows(DataFormatException.class, () -> parseJsonStrict(input));
		assertEquals(Msg.code(1825) + "Unknown element 'id' found during parse", e.getMessage());
	}

	private PatientWithNestedBlockExtensionR4 parseJsonStrict(String theInput) {
		IParser parser = ourCtx.newJsonParser();
		parser.setParserErrorHandler(new StrictErrorHandler());
		return parser.parseResource(PatientWithNestedBlockExtensionR4.class, theInput);
	}

	private PatientWithNestedBlockExtensionR4 parseXmlStrict(String theInput) {
		IParser parser = ourCtx.newXmlParser();
		parser.setParserErrorHandler(new StrictErrorHandler());
		return parser.parseResource(PatientWithNestedBlockExtensionR4.class, theInput);
	}
}
