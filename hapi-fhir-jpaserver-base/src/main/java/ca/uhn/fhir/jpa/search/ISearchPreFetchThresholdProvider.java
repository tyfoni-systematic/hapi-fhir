/*-
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2026 Smile CDR, Inc.
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
package ca.uhn.fhir.jpa.search;

import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

import java.util.List;

/**
 * Supplies the search pre-fetch thresholds to use for an individual search, allowing a server to
 * vary them by the kind of search being performed rather than applying a single global list.
 * <p>
 * The default implementation returns {@link JpaStorageSettings#getSearchPreFetchThresholds()} for
 * every search. A server may replace the bean in order to, for example, use a single unbounded
 * pass for searches that clients are known to page through to the end of, while leaving the global
 * thresholds in place for everything else.
 * </p>
 * <p>
 * <b>Implementations must be a pure function of the arguments passed to
 * {@link #getPreFetchThresholds(String, SearchParameterMap)}, and in particular must not depend on
 * the current request.</b> A search that stops at a pre-fetch threshold is resumed by a later
 * request, potentially on another server instance, and the resumed pass re-resolves the thresholds
 * from the persisted {@link SearchParameterMap} alone. If an implementation were to return
 * different lists for the initial and resumed passes, the search would not progress correctly.
 * </p>
 *
 * @see JpaStorageSettings#setSearchPreFetchThresholds(List)
 * @since 8.6.5
 */
public interface ISearchPreFetchThresholdProvider {

	/**
	 * Returns the pre-fetch thresholds to use for the given search. The contract is the same as
	 * {@link JpaStorageSettings#setSearchPreFetchThresholds(List)}: a list of ascending positive
	 * values, optionally ending in <code>-1</code> to indicate that the final pass should load an
	 * unlimited number of results.
	 *
	 * @param theResourceType the type of resource being searched for, e.g. <code>Patient</code>
	 * @param theParams       the search being performed. Must not be modified.
	 * @return the thresholds to use. Must not be <code>null</code> or empty.
	 */
	List<Integer> getPreFetchThresholds(String theResourceType, SearchParameterMap theParams);
}
