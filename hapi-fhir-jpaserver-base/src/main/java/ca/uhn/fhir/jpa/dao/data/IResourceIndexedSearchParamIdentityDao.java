/*
 * #%L
 * HAPI FHIR JPA Server
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
package ca.uhn.fhir.jpa.dao.data;

import ca.uhn.fhir.jpa.model.entity.IndexedSearchParamIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface IResourceIndexedSearchParamIdentityDao
		extends JpaRepository<IndexedSearchParamIdentity, Integer>, IHapiFhirJpaRepository {

	@Query("select i.myHashIdentity, i.mySpIdentityId from IndexedSearchParamIdentity i")
	Collection<Object[]> getAllHashIdentities();

	@Query("select i from IndexedSearchParamIdentity i where i.myHashIdentity = :hash_identity")
	IndexedSearchParamIdentity getSearchParameterIdByHashIdentity(@Param("hash_identity") long theHashIdentity);
}
