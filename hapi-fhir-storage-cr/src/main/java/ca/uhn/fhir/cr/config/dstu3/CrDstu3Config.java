/*-
 * #%L
 * HAPI FHIR - Clinical Reasoning
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
package ca.uhn.fhir.cr.config.dstu3;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.cr.common.IRepositoryFactory;
import ca.uhn.fhir.cr.config.CrBaseConfig;
import ca.uhn.fhir.cr.config.ProviderLoader;
import ca.uhn.fhir.cr.config.ProviderSelector;
import ca.uhn.fhir.cr.config.RepositoryConfig;
import ca.uhn.fhir.cr.dstu3.IMeasureServiceFactory;
import ca.uhn.fhir.cr.dstu3.measure.MeasureOperationsProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions;
import org.opencds.cqf.fhir.cr.measure.dstu3.Dstu3MeasureService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Map;

@Deprecated(since = "8.1.4", forRemoval = true)
@Configuration
@Import({RepositoryConfig.class, CrBaseConfig.class})
public class CrDstu3Config {

	@Bean
	IMeasureServiceFactory dstu3MeasureServiceFactory(
			IRepositoryFactory theRepositoryFactory, MeasureEvaluationOptions theEvaluationOptions) {
		return rd -> new Dstu3MeasureService(theRepositoryFactory.create(rd), theEvaluationOptions);
	}

	@Bean
	MeasureOperationsProvider dstu3MeasureOperationsProvider() {
		return new MeasureOperationsProvider();
	}

	@Bean
	public ProviderLoader dstu3PdLoader(
			ApplicationContext theApplicationContext, FhirContext theFhirContext, RestfulServer theRestfulServer) {

		var selector = new ProviderSelector(
				theFhirContext, Map.of(FhirVersionEnum.DSTU3, Arrays.asList((MeasureOperationsProvider.class))));

		return new ProviderLoader(theRestfulServer, theApplicationContext, selector);
	}
}
