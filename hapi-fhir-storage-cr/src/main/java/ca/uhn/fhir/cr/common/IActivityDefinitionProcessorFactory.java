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
package ca.uhn.fhir.cr.common;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.opencds.cqf.fhir.cr.activitydefinition.ActivityDefinitionProcessor;

/**
 * This interface takes a RequestDetails object and uses it to create a Repository which is passed to the constructor of the processor class being instantiated.
 */
@Deprecated(since = "8.1.4", forRemoval = true)
@FunctionalInterface
public interface IActivityDefinitionProcessorFactory {
	ActivityDefinitionProcessor create(RequestDetails theRequestDetails);
}
