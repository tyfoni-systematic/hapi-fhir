package ca.uhn.fhir.parser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RDFParserR4Test {
	private static final FhirContext ourCtx = FhirContext.forR4Cached();

	@Test
	public void testEncodeToString_PrimitiveDataType() {
		DecimalType object = new DecimalType("123.456000");
		String expected = "123.456000";
		String actual = ourCtx.newRDFParser().encodeToString(object);
		assertEquals(expected, actual);
	}

	@Test
	public void testEncodeToString_Resource() {
		Patient p = new Patient();
		p.setId("Patient/123");
		p.setActive(true);
		String expected = """
			@prefix fhir: <http://hl7.org/fhir/> .
			@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
			@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
			@prefix sct:  <http://snomed.info/id#> .
			@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
			   
			<http://hl7.org/fhir/Patient/123>
			        rdf:type             fhir:Patient;
			        fhir:Patient.active  [ fhir:value  true ];
			        fhir:Resource.id     [ fhir:value  "123" ];
			        fhir:nodeRole        fhir:treeRoot .
			""";

		Model expectedModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expectedModel, new StringReader(expected), null, Lang.TURTLE);

		String actual = ourCtx.newRDFParser().encodeToString(p);
		Model actualModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(actualModel, new StringReader(actual), null, Lang.TURTLE);

		assertTrue(expectedModel.isIsomorphicWith(actualModel));

		String unexpected = """
			@prefix fhir: <http://hl7.org/fhir/> .
			@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
			@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
			@prefix sct:  <http://snomed.info/id#> .
			@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
			   
			<http://hl7.org/fhir/Patient/123>
			        rdf:type             fhir:Patient;
			        fhir:Patient.active  [ fhir:value  true ];
			        fhir:Resource.id     [ fhir:value  "124" ];
			        fhir:nodeRole        fhir:treeRoot .
			""";

		Model unexpectedModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(unexpectedModel, new StringReader(unexpected), null, Lang.TURTLE);
		assertFalse(actualModel.isIsomorphicWith(unexpectedModel));
	}

	@Test
	public void testEncodeToString_GeneralPurposeDataType() {
		HumanName name = new HumanName();
		name.setFamily("Simpson").addGiven("Homer").addGiven("Jay");
		name.addExtension("http://foo", new StringType("bar"));

		assertEquals("HAPI-2363: This parser does not support encoding non-resource values", assertThrows(InternalErrorException.class, () -> ourCtx.newRDFParser().encodeToString(name)).getMessage());
	}

	@Test
	public void testEncodeToString_BackboneElement() {
		Patient.PatientCommunicationComponent communication = new Patient().addCommunication();
		communication.setPreferred(true);
		communication.getLanguage().setText("English");

		assertEquals("HAPI-2363: This parser does not support encoding non-resource values", assertThrows(InternalErrorException.class, () -> ourCtx.newRDFParser().encodeToString(communication)).getMessage());
	}

}
