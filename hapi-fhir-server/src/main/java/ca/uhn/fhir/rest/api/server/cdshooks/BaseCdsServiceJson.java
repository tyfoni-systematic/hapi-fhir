/*-
 * #%L
 * HAPI FHIR - Server Framework
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
package ca.uhn.fhir.rest.api.server.cdshooks;

import ca.uhn.fhir.model.api.IModelJson;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see <a href=" https://cds-hooks.hl7.org/1.0/#extensions">For reading more about Extension support in CDS hooks</a>
 * Example can be found <a href="https://build.fhir.org/ig/HL7/davinci-crd/deviations.html#configuration-options-extension">here</a>
 */
public abstract class BaseCdsServiceJson implements IModelJson {

	@JsonProperty(value = "extension")
	CdsHooksExtension myExtension;

	private Class<? extends CdsHooksExtension> myExtensionClass;

	public CdsHooksExtension getExtension() {
		return myExtension;
	}

	public BaseCdsServiceJson setExtension(CdsHooksExtension theExtension) {
		this.myExtension = theExtension;
		return this;
	}

	public void setExtensionClass(Class<? extends CdsHooksExtension> theClass) {
		this.myExtensionClass = theClass;
	}

	public Class<? extends CdsHooksExtension> getExtensionClass() {
		return myExtensionClass;
	}
}
