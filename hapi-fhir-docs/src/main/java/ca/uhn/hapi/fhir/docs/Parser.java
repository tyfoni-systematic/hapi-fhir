/*-
 * #%L
 * HAPI FHIR - Docs
 * %%
 * Copyright (C) 2014 - 2025 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ca.uhn.hapi.fhir.docs;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.ParserOptions;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import com.google.common.collect.Sets;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;

import java.io.IOException;

public class Parser {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws DataFormatException, IOException {

		{
			// START SNIPPET: parsing
			// Create a FHIR context
			FhirContext ctx = FhirContext.forR4();

			// The following example is a simple serialized Patient resource to parse
			String input = "{" + "\"resourceType\" : \"Patient\","
					+ "  \"name\" : [{"
					+ "    \"family\": \"Simpson\""
					+ "  }]"
					+ "}";

			// Instantiate a new parser
			IParser parser = ctx.newJsonParser();

			// Parse it
			Patient parsed = parser.parseResource(Patient.class, input);
			System.out.println(parsed.getName().get(0).getFamily());
			// END SNIPPET: parsing
		}
		{
			// START SNIPPET: encoding
			// Create a FHIR context
			FhirContext ctx = FhirContext.forR4();

			// Create a Patient resource to serialize
			Patient patient = new Patient();
			patient.addName().setFamily("Simpson").addGiven("James");

			// Instantiate a new JSON parser
			IParser parser = ctx.newJsonParser();

			// Serialize it
			String serialized = parser.encodeResourceToString(patient);
			System.out.println(serialized);

			// Using XML instead
			serialized = ctx.newXmlParser().encodeResourceToString(patient);
			System.out.println(serialized);
			// END SNIPPET: encoding
		}
		{
			// Create a FHIR context
			FhirContext ctx = FhirContext.forR4();
			Patient patient = new Patient();
			patient.addName().setFamily("Simpson").addGiven("James");

			// START SNIPPET: encodingPretty
			// Create a parser
			IParser parser = ctx.newJsonParser();

			// Indent the output
			parser.setPrettyPrint(true);

			// Serialize it
			String serialized = parser.encodeResourceToString(patient);
			System.out.println(serialized);

			// You can also chain these statements together
			ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
			// END SNIPPET: encodingPretty
		}
		{
			// Create a FHIR context
			FhirContext ctx = FhirContext.forR4();
			Patient patient = new Patient();
			patient.addName().setFamily("Simpson").addGiven("James");

			// START SNIPPET: encodingConfig
			// Create a parser
			IParser parser = ctx.newJsonParser();

			// Blacklist certain fields from being encoded
			parser.setDontEncodeElements(Sets.newHashSet("Patient.identifier", "Patient.active"));

			// Don't include resource narratives
			parser.setSuppressNarratives(true);

			// Use versioned references for these reference elements
			parser.setDontStripVersionsFromReferencesAtPaths("Patient.organization");

			// Serialize it
			String serialized = parser.encodeResourceToString(patient);
			System.out.println(serialized);
			// END SNIPPET: encodingConfig
		}
		{
			// Create a FHIR context
			FhirContext ctx = FhirContext.forR4();
			Patient patient = new Patient();
			patient.addName().setFamily("Simpson").addGiven("James");

			// START SNIPPET: encodingSummary
			// Create a parser
			IParser parser = ctx.newJsonParser();

			// Instruct the parser to only include summary elements
			parser.setSummaryMode(true);

			// If you need to, you can instruct the parser to override
			// the default summary elements by adding and/or removing
			// elements from the list of elements it will include. This
			// is typically not needed, but it's shown here in case you
			// need to do this:
			// Include a non-summary element in the summary view.
			parser.setEncodeElements("Patient.maritalStatus");
			// Exclude a summary element even though it would normally
			// be included.
			parser.setDontEncodeElements("Patient.name");

			// Serialize it
			String serialized = parser.encodeResourceToString(patient);
			System.out.println(serialized);
			// END SNIPPET: encodingSummary
		}

		{
			// START SNIPPET: disableStripVersions
			FhirContext ctx = FhirContext.forR4();
			IParser parser = ctx.newJsonParser();

			// Disable the automatic stripping of versions from references on the parser
			parser.setStripVersionsFromReferences(false);
			// END SNIPPET: disableStripVersions

			// START SNIPPET: disableStripVersionsCtx
			ctx.getParserOptions().setStripVersionsFromReferences(false);
			// END SNIPPET: disableStripVersionsCtx
		}

		{
			// START SNIPPET: disableStripVersionsField
			FhirContext ctx = FhirContext.forR4();
			IParser parser = ctx.newJsonParser();

			// Preserve versions only on these two fields (for the given parser)
			parser.setDontStripVersionsFromReferencesAtPaths(
					"AuditEvent.entity.reference", "Patient.managingOrganization");

			// You can also apply this setting to the context so that it will
			// flow to all parsers
			ctx.getParserOptions()
					.setDontStripVersionsFromReferencesAtPaths(
							"AuditEvent.entity.reference", "Patient.managingOrganization");
			// END SNIPPET: disableStripVersionsField

		}

		{
			IBaseResource patient = new Patient();

			// START SNIPPET: globalParserConfig
			FhirContext ctx = FhirContext.forR4();

			// Request the ParserOptions, which store global config
			// settings applied to all parsers coming from the given
			// context.
			ParserOptions parserOptions = ctx.getParserOptions();

			// Never strip resource reference versions for the following
			// paths
			parserOptions.setDontStripVersionsFromReferencesAtPaths(
					"AuditEvent.entity.reference", "Patient.managingOrganization");

			// Never strip any resource reference versions (setting this
			// to false would make the setting above redundant since this
			// setting applies to all paths)
			parserOptions.setStripVersionsFromReferences(false);

			// Even in summary mode, always include extensions on the
			// root of Patient resources.
			parserOptions.setEncodeElementsForSummaryMode("Patient.extension");

			// Create a parser and encode, with the global config applied.
			IParser parser = ctx.newJsonParser();
			String encoded = parser.encodeResourceToString(patient);
			// END SNIPPET: globalParserConfig

		}
	}
}
