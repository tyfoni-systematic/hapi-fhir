/*-
 * #%L
 * HAPI FHIR JPA Model
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
package ca.uhn.fhir.jpa.model.entity;

import jakarta.persistence.MappedSuperclass;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

@MappedSuperclass
public abstract class BaseResourceIndex extends BasePartitionable implements Serializable {

	public abstract Long getId();

	public abstract void setId(Long theId);

	public abstract void calculateHashes();

	public abstract void clearHashes();

	@Override
	public void setPartitionId(PartitionablePartitionId thePartitionId) {
		if (ObjectUtils.notEqual(getPartitionId(), thePartitionId)) {
			super.setPartitionId(thePartitionId);
			clearHashes();
		}
	}

	/**
	 * Subclasses must implement
	 */
	@Override
	public abstract int hashCode();

	/**
	 * Subclasses must implement
	 */
	@Override
	public abstract boolean equals(Object obj);

	public abstract <T extends BaseResourceIndex> void copyMutableValuesFrom(T theSource);

	/**
	 * This is called when reindexing a resource on the previously existing index rows. This method
	 * should set zero/0 values for the hashes, in order to avoid any calculating hashes on existing
	 * rows failing. This is important only in cases where hashes are not present on the existing rows,
	 * which would only be the case if new hash columns have been added.
	 */
	public void setPlaceholderHashesIfMissing() {
		// nothing by default
	}

	public abstract void setResourceId(Long theId);
}
