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

package org.openlmis.equipment.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.builder.MaintenanceLogBuilder;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;

import static org.junit.Assert.*;
@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class MaintenanceLogMapperIT {

  @Autowired
  private MaintenanceLogMapper mapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private EquipmentTypeMapper equipmentTypeMapper;

  @Autowired
  private EquipmentMapper equipmentMapper;

  private Equipment equipment;

  @Before
  public void setup() {
    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setCode("1");
    equipmentTypeMapper.insert(equipmentType);

    equipment = new Equipment();
    equipment.setName("Name");
    equipment.setEquipmentType(equipmentType);
    equipmentMapper.insert(equipment);
  }

  @Test
  public void testGetById() throws Exception {
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    MaintenanceLog log2 = mapper.getById(log.getId());
    assertEquals(log.getRecommendation(), log2.getRecommendation());
  }

  @Test
  public void testGetAll() throws Exception {
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    List<MaintenanceLog> logs = mapper.getAll();
    assertEquals(0, logs.size());

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    logs = mapper.getAll();
    assertEquals(1, logs.size());
  }

  @Test
  public void testGetAllForFacility() throws Exception {
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    assertEquals(0, mapper.getAllForFacility(facility.getId()).size());

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    List<MaintenanceLog> logs = mapper.getAllForFacility(facility.getId());
    assertEquals(1, logs.size());

  }

  @Test
  public void testGetAllForVendor() throws Exception {
    int initialSize = mapper.getAllForVendor(1L).size();
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setVendorId(1L);
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    List<MaintenanceLog> logs = mapper.getAllForVendor(1L);
    assertEquals(initialSize + 1, logs.size());
  }

  @Test
  public void testInsert() throws Exception {
    int initialSize = mapper.getAllForVendor(1L).size();
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setVendorId(1L);
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    List<MaintenanceLog> logs = mapper.getAll();
    assertEquals(initialSize + 1, logs.size());
  }

  @Test
  public void testUpdate() throws Exception {
    int initialSize = mapper.getAllForVendor(1L).size();
    Facility facility  = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    log.setFacilityId(facility.getId());
    log.setVendorId(1L);
    log.setEquipmentId(equipment.getId());
    mapper.insert(log);

    log.setFinding("New Finding");
  }
}