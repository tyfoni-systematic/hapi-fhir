package ca.uhn.fhir.jpa.partition;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.IInterceptorService;

import ca.uhn.fhir.interceptor.api.Interceptor;

import ca.uhn.fhir.interceptor.api.Pointcut;

import ca.uhn.fhir.interceptor.model.RequestPartitionId;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.jpa.entity.PartitionEntity;
import ca.uhn.fhir.jpa.test.BaseJpaR4Test;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.springframework.beans.factory.annotation.Autowired;

public class PartitionSettingsSvcImplTest extends BaseJpaR4Test {
	@Autowired
	IInterceptorService myInterceptorService;

	@AfterEach
	public void after() {
		myPartitionSettings.setUnnamedPartitionMode(false);
	}

	@Test
	public void testCreateAndFetchPartition() {

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		myPartitionConfigSvc.createPartition(partition, null);

		partition = myPartitionConfigSvc.getPartitionById(123);
		assertEquals("NAME123", partition.getName());

		partition = myPartitionConfigSvc.getPartitionByName("NAME123");
		assertEquals("NAME123", partition.getName());
	}

	@Test
	public void testCreatePartition_BlockedInUnnamedPartitionMode() {
		myPartitionSettings.setUnnamedPartitionMode(true);

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		try {
			myPartitionConfigSvc.createPartition(partition, null);
		} catch (MethodNotAllowedException e) {
			assertEquals(Msg.code(1313) + "Can not invoke this operation in unnamed partition mode", e.getMessage());
		}
	}

	@Test
	public void testDeletePartition() {
		DeletedPartitionsInterceptor deletedPartitionsInterceptor = new DeletedPartitionsInterceptor();
		myInterceptorService.registerInterceptor(deletedPartitionsInterceptor);

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		myPartitionConfigSvc.createPartition(partition, null);

		partition = myPartitionConfigSvc.getPartitionById(123);
		assertEquals("NAME123", partition.getName());

		myPartitionConfigSvc.deletePartition(123);
		assertEquals(1, deletedPartitionsInterceptor.getDeletedPartitions().size());
		assertThat(deletedPartitionsInterceptor.getDeletedPartitions().get(0).getFirstPartitionIdOrNull().intValue()).isEqualTo(123);

		try {
			myPartitionConfigSvc.getPartitionById(123);
			fail();
		} catch (ResourceNotFoundException e) {
			assertEquals("No partition exists with ID 123", e.getMessage());
		}

		myInterceptorService.unregisterInterceptor(deletedPartitionsInterceptor);
	}

	@Interceptor
	public static class DeletedPartitionsInterceptor {
		private List<RequestPartitionId> myDeletedPartitions = new ArrayList<>();

		@Hook(Pointcut.STORAGE_PARTITION_DELETED)
		public void partitionDeleted(RequestPartitionId partitionId) {
			myDeletedPartitions.add(partitionId);
		}

		public List<RequestPartitionId> getDeletedPartitions() {
			return myDeletedPartitions;
		}
	}

	@Test
	public void testUpdatePartition_TryToUseExistingName() {

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		myPartitionConfigSvc.createPartition(partition, null);

		partition = new PartitionEntity();
		partition.setId(111);
		partition.setName("NAME111");
		partition.setDescription("A description");
		myPartitionConfigSvc.createPartition(partition, null);

		partition = new PartitionEntity();
		partition.setId(111);
		partition.setName("NAME123");
		partition.setDescription("A description");
		try {
			myPartitionConfigSvc.updatePartition(partition);
			fail();
		} catch (InvalidRequestException e) {
			assertEquals(Msg.code(1309) + "Partition name \"NAME123\" is already defined", e.getMessage());
		}
	}

	@Test
	public void testCreatePartition_whenPartitionIdAlreadyExists_operationNotAllowed(){
		try {
			PartitionEntity partition = new PartitionEntity();
			partition.setId(123);
			partition.setName("NAME123");
			partition.setDescription("A description");
			myPartitionConfigSvc.createPartition(partition, null);

			partition = new PartitionEntity();
			partition.setId(123);
			partition.setName("NAME111");
			partition.setDescription("A description");
			myPartitionConfigSvc.createPartition(partition, null);
		}

		catch (InvalidRequestException e) {
			assertEquals(Msg.code(2366) + "Partition ID already exists", e.getMessage());
		}
	}

	@Test
	public void testUpdatePartition_TryToRenameDefault() {
		PartitionEntity partition = new PartitionEntity();
		partition.setId(null);
		partition.setName("NAME123");
		partition.setDescription("A description");
		try {
			myPartitionConfigSvc.updatePartition(partition);
			fail();
		} catch (InvalidRequestException e) {
			assertEquals(Msg.code(1310) + "Partition must have an ID and a Name", e.getMessage());
		}
	}

	@Test
	public void testUpdatePartition() {

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		myPartitionConfigSvc.createPartition(partition, null);

		partition = myPartitionConfigSvc.getPartitionById(123);
		assertEquals("NAME123", partition.getName());

		partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME-NEW");
		partition.setDescription("A description");
		myPartitionConfigSvc.updatePartition(partition);

		partition = myPartitionConfigSvc.getPartitionById(123);
		assertEquals("NAME-NEW", partition.getName());
	}

	@Test
	public void testCreatePartition_InvalidName() {

		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME 123");
		partition.setDescription("A description");
		try {
			myPartitionConfigSvc.createPartition(partition, null);
			fail();
		} catch (InvalidRequestException e) {
			assertEquals(Msg.code(1312) + "Partition name \"NAME 123\" is not valid", e.getMessage());
		}

	}

	@Test
	public void testUpdatePartition_UnknownPartitionBlocked() {
		PartitionEntity partition = new PartitionEntity();
		partition.setId(123);
		partition.setName("NAME123");
		partition.setDescription("A description");
		try {
			myPartitionConfigSvc.updatePartition(partition);
			fail();
		} catch (InvalidRequestException e) {
			assertEquals(Msg.code(1307) + "No partition exists with ID 123", e.getMessage());
		}

	}

	@Test
	public void testListPartitions() {
		PartitionEntity partition1 = new PartitionEntity();
		partition1.setId(1);
		partition1.setName("PARTITION-1");
		partition1.setDescription("a description1");

		PartitionEntity partition2 = new PartitionEntity();
		partition2.setId(2);
		partition2.setName("PARTITION-2");
		partition2.setDescription("a description2");

		myPartitionConfigSvc.createPartition(partition1, null);
		myPartitionConfigSvc.createPartition(partition2, null);

		List<PartitionEntity> actual = myPartitionConfigSvc.listPartitions();

		assertThat(actual).hasSize(2);
		assertTrue(actual.stream().anyMatch(item -> "PARTITION-1".equals(item.getName())));
		assertTrue(actual.stream().anyMatch(item -> "PARTITION-2".equals(item.getName())));
	}
}
