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
package ca.uhn.fhir.cr.config;

import ca.uhn.fhir.cr.common.IRepositoryFactory;
import ca.uhn.fhir.cr.common.RepositoryFactoryForRepositoryInterface;
import ca.uhn.fhir.cr.repo.HapiFhirRepository;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated(since = "8.1.4", forRemoval = true)
@Configuration
public class RepositoryConfig {

	@Bean
	IRepositoryFactory repositoryFactory(DaoRegistry theDaoRegistry, RestfulServer theRestfulServer) {
		return rd -> new HapiFhirRepository(theDaoRegistry, rd, theRestfulServer);
	}

	@Bean
	RepositoryFactoryForRepositoryInterface repositoryFactoryForInterface(
			DaoRegistry theDaoRegistry, RestfulServer theRestfulServer) {
		return rd -> new HapiFhirRepository(theDaoRegistry, rd, theRestfulServer);
	}
}
