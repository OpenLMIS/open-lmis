/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.builder.EquipmentInventoryBuilder;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentInventoryStatus;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.EquipmentInventoryMapper;
import org.openlmis.equipment.repository.mapper.EquipmentInventoryStatusMapper;
import org.openlmis.equipment.repository.mapper.EquipmentMapper;
import org.openlmis.equipment.repository.mapper.EquipmentTypeMapper;
import org.openlmis.rnr.domain.EquipmentLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentLineItemMapperIT {

  @Autowired
  EquipmentLineItemMapper mapper;

  @Autowired
  private RequisitionMapper requisitionMapper;

  @Autowired
  private EquipmentMapper equipmentMapper;

  @Autowired
  private EquipmentTypeMapper equipmentTypeMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private EquipmentInventoryMapper equipmentInventoryMapper;

  @Autowired
  private EquipmentInventoryStatusMapper equipmentInventoryStatusMapper;

  EquipmentLineItem equipmentLineItem;

  Rnr rnr;

  @Before
  public void setUp() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);

    rnr = new Rnr(facility, new Program(2L), processingPeriod, false, 1L, 1L);
    rnr.setStatus(RnrStatus.INITIATED);
    requisitionMapper.insert(rnr);
    RegimenCategory category = new RegimenCategory("categoryCode", "categoryName", 1);
    category.setId(1L);


    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setName("Name");
    equipmentType.setCode("23");
    equipmentTypeMapper.insert(equipmentType);

    Equipment equipment = new Equipment();
    equipment.setName("cd 4 counter");
    equipment.setEquipmentType(equipmentType);
    equipment.setEquipmentTypeId(equipmentType.getId());

    equipmentMapper.insert(equipment);

    EquipmentInventory inventory = make(a(EquipmentInventoryBuilder.defaultEquipmentInventory));
    inventory.setProgramId(2L);
    inventory.setFacilityId(facility.getId());
    inventory.setEquipmentId(equipment.getId());
    equipmentInventoryMapper.insert(inventory);

    long statusId = 1L;
    EquipmentInventoryStatus status = new EquipmentInventoryStatus();
    status.setInventoryId(inventory.getId());
    status.setStatusId(statusId);
    equipmentInventoryStatusMapper.insert(status);

    equipmentLineItem = new EquipmentLineItem();

    equipmentLineItem.setRnrId(rnr.getId());
    equipmentLineItem.setCode("eq123");
    equipmentLineItem.setEquipmentName("CD4 Counter");
    equipmentLineItem.setEquipmentCategory("The Category");
    equipmentLineItem.setEquipmentInventoryId(inventory.getId());
    equipmentLineItem.setInventoryStatusId(status.getId());
    equipmentLineItem.setDaysOutOfUse(2L);
  }

  @Test
  public void shouldInsert() throws Exception {
    Integer count = mapper.insert(equipmentLineItem);
    assertThat(count, is(1));
    assertThat(equipmentLineItem.getId(), is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    mapper.insert(equipmentLineItem);
    equipmentLineItem.setRemarks("the new remark");
    mapper.update(equipmentLineItem);

    EquipmentLineItem lineItem = mapper.getById(equipmentLineItem.getId());
    assertThat(lineItem.getRemarks(), is(equipmentLineItem.getRemarks()));
  }

  @Test
  public void shouldGetEquipmentLineItemsByRnrId() throws Exception {
    mapper.insert(equipmentLineItem);
    List<EquipmentLineItem> lineItems = mapper.getEquipmentLineItemsByRnrId(rnr.getId());
    assertThat(lineItems.size(), is(1));
  }
  
}