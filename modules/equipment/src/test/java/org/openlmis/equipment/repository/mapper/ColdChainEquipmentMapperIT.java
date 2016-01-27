/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.equipment.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ColdChainEquipmentMapperIT {

  @Autowired
  ColdChainEquipmentMapper mapper;

 @Autowired
 EquipmentTypeMapper typeMapper;

  @Autowired
  EquipmentMapper equipmentMapper;

  @Autowired
  ColdChainEquipmentDesignationMapper designationMapper;

  @Autowired
  ColdChainEquipmentPqsStatusMapper statusMapper;

  @Autowired
  EquipmentEnergyTypeMapper energyTypeMapper;

  @Autowired
  DonorMapper donorMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldGetAllColdChainEquipments() throws Exception{
    Pagination page1=new Pagination(0,2);
    Pagination page2=new Pagination(2,1);

    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    type.setColdChain(true);
    typeMapper.insert(type);

    ColdChainEquipmentDesignation designation=new ColdChainEquipmentDesignation();
    designation.setName("TestDesignation");
    designationMapper.insert(designation);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    ColdChainEquipmentPqsStatus pqsStatus=new ColdChainEquipmentPqsStatus();
    pqsStatus.setName("TestStatus");
    statusMapper.insert(pqsStatus);

    ColdChainEquipment coldChainEquipment = new ColdChainEquipment();
    coldChainEquipment.setName("Equipment Name");
    coldChainEquipment.setEquipmentType(type);
    coldChainEquipment.setManufacturer("Manufacturer");
    coldChainEquipment.setModel("Model");
    coldChainEquipment.setEnergyTypeId(energyType.getId());
    coldChainEquipment.setDesignationId(designation.getId());
    coldChainEquipment.setPqsStatusId(pqsStatus.getId());

    ColdChainEquipment coldChainEquipment2 = new ColdChainEquipment();
    coldChainEquipment2.setName("Equipment Name2");
    coldChainEquipment2.setEquipmentType(type);
    coldChainEquipment2.setManufacturer("Manufacturer2");
    coldChainEquipment2.setModel("Model2");
    coldChainEquipment2.setEnergyTypeId(energyType.getId());
    coldChainEquipment2.setDesignationId(designation.getId());
    coldChainEquipment2.setPqsStatusId(pqsStatus.getId());

    equipmentMapper.insert(coldChainEquipment);
    mapper.insert(coldChainEquipment);
    equipmentMapper.insert(coldChainEquipment2);
    mapper.insert(coldChainEquipment2);


    List<ColdChainEquipment> results =  mapper.getAll(type.getId(),page1);
    assertEquals(results.size(), 2);

    List<ColdChainEquipment> results2 = mapper.getAll(type.getId(),page2);
    assertEquals(results2.size(), 1);

  }

  @Test
  public void shouldGetColdChainEquipmentById() throws Exception {

    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    ColdChainEquipmentDesignation designation=new ColdChainEquipmentDesignation();
    designation.setName("TestDesignation");
    designationMapper.insert(designation);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    ColdChainEquipmentPqsStatus pqsStatus=new ColdChainEquipmentPqsStatus();
    pqsStatus.setName("TestStatus");
    statusMapper.insert(pqsStatus);

    Donor donor=new Donor();
    donor.setCode("DONOR");
    donor.setCountOfDonations(1);
    donor.setLongName("TEST DONOR");
    donor.setShortName("DONOR");
    donorMapper.insert(donor);

    ColdChainEquipment coldChainEquipment = new ColdChainEquipment();
    coldChainEquipment.setName("Equipment Name");
    coldChainEquipment.setEquipmentType(type);
    coldChainEquipment.setManufacturer("Manufacturer");
    coldChainEquipment.setModel("Model");
    coldChainEquipment.setEnergyTypeId(energyType.getId());
    coldChainEquipment.setDesignationId(designation.getId());
    coldChainEquipment.setCceCode("CR");
    coldChainEquipment.setPqsCode("Domestic");
    coldChainEquipment.setRefrigeratorCapacity(10.00F);
    coldChainEquipment.setFreezerCapacity(5.00F);
    coldChainEquipment.setRefrigerant("NH3");
    coldChainEquipment.setTemperatureZone("HT");
    coldChainEquipment.setEnergyConsumption("12kW/h");
    coldChainEquipment.setMaxTemperature(4L);
    coldChainEquipment.setMaxTemperature(-4L);
    coldChainEquipment.setHoldoverTime(12.30F);
    coldChainEquipment.setDimension("2M X 1M X 1.5M");
    coldChainEquipment.setPrice(2000.00F);
    coldChainEquipment.setDonorId(donor.getId());
    coldChainEquipment.setPqsStatusId(pqsStatus.getId());

    equipmentMapper.insert(coldChainEquipment);
    mapper.insert(coldChainEquipment);

    ColdChainEquipment result = mapper.getById(coldChainEquipment.getId());

    assertEquals(result.getModel(), coldChainEquipment.getModel());
    assertEquals(result.getManufacturer(), coldChainEquipment.getManufacturer());
    assertEquals(result.getName(), coldChainEquipment.getName());
  }

  @Test
  public void shouldInsertColdChainEquipment()throws Exception {

    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    ColdChainEquipmentDesignation designation=new ColdChainEquipmentDesignation();
    designation.setName("TestDesignation");
    designationMapper.insert(designation);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    ColdChainEquipmentPqsStatus pqsStatus=new ColdChainEquipmentPqsStatus();
    pqsStatus.setName("TestStatus");
    statusMapper.insert(pqsStatus);

    Donor donor=new Donor();
    donor.setCode("DONOR");
    donor.setCountOfDonations(1);
    donor.setLongName("TEST DONOR");
    donor.setShortName("DONOR");
    donorMapper.insert(donor);

    ColdChainEquipment coldChainEquipment = new ColdChainEquipment();
    coldChainEquipment.setName("Equipment Name");
    coldChainEquipment.setEquipmentType(type);
    coldChainEquipment.setManufacturer("Manufacturer");
    coldChainEquipment.setModel("Model");
    coldChainEquipment.setEnergyTypeId(energyType.getId());
    coldChainEquipment.setDesignationId(designation.getId());
    coldChainEquipment.setCceCode("CR");
    coldChainEquipment.setPqsCode("Domestic");
    coldChainEquipment.setRefrigeratorCapacity(10.00F);
    coldChainEquipment.setFreezerCapacity(5.00F);
    coldChainEquipment.setRefrigerant("NH3");
    coldChainEquipment.setTemperatureZone("HT");
    coldChainEquipment.setEnergyConsumption("12kW/h");
    coldChainEquipment.setMaxTemperature(4L);
    coldChainEquipment.setMaxTemperature(-4L);
    coldChainEquipment.setHoldoverTime(12.30F);
    coldChainEquipment.setDimension("2M X 1M X 1.5M");
    coldChainEquipment.setPrice(2000.00F);
    coldChainEquipment.setDonorId(donor.getId());
    coldChainEquipment.setPqsStatusId(pqsStatus.getId());

    equipmentMapper.insert(coldChainEquipment);
    mapper.insert(coldChainEquipment);

    assertThat(coldChainEquipment.getId(), CoreMatchers.is(notNullValue()));

    ResultSet rs = queryExecutor.execute("Select * from equipment_cold_chain_equipments ce JOIN equipments e ON ce.equipmentid=e.id where equipmentid = " + coldChainEquipment.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("model"), "Model");
    assertEquals(rs.getString("refrigerant"), "NH3");
  }

  @Test
  public void shouldUpdateColdChainEquipment() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    ColdChainEquipmentDesignation designation=new ColdChainEquipmentDesignation();
    designation.setName("TestDesignation");
    designationMapper.insert(designation);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    ColdChainEquipmentPqsStatus pqsStatus=new ColdChainEquipmentPqsStatus();
    pqsStatus.setName("TestStatus");
    statusMapper.insert(pqsStatus);

    Donor donor=new Donor();
    donor.setCode("DONOR");
    donor.setCountOfDonations(1);
    donor.setLongName("TEST DONOR");
    donor.setShortName("DONOR");
    donorMapper.insert(donor);

    ColdChainEquipment coldChainEquipment = new ColdChainEquipment();
    coldChainEquipment.setName("Equipment Name");
    coldChainEquipment.setEquipmentType(type);
    coldChainEquipment.setManufacturer("Manufacturer");
    coldChainEquipment.setModel("Model");
    coldChainEquipment.setEnergyTypeId(energyType.getId());
    coldChainEquipment.setDesignationId(designation.getId());
    coldChainEquipment.setCceCode("CR");
    coldChainEquipment.setPqsCode("Domestic");
    coldChainEquipment.setRefrigeratorCapacity(10.00F);
    coldChainEquipment.setFreezerCapacity(5.00F);
    coldChainEquipment.setRefrigerant("NH3");
    coldChainEquipment.setTemperatureZone("HT");
    coldChainEquipment.setEnergyConsumption("12kW/h");
    coldChainEquipment.setMaxTemperature(4L);
    coldChainEquipment.setMaxTemperature(-4L);
    coldChainEquipment.setHoldoverTime(12.30F);
    coldChainEquipment.setDimension("2M X 1M X 1.5M");
    coldChainEquipment.setPrice(2000.00F);
    coldChainEquipment.setDonorId(donor.getId());
    coldChainEquipment.setPqsStatusId(pqsStatus.getId());

    equipmentMapper.insert(coldChainEquipment);
    mapper.insert(coldChainEquipment);

    coldChainEquipment.setRefrigerant("NewRefrigerant");
    coldChainEquipment.setModel("NewModel");

    equipmentMapper.update(coldChainEquipment);
    mapper.update(coldChainEquipment);

    assertThat(coldChainEquipment.getId(), CoreMatchers.is(notNullValue()));

    ResultSet rs = queryExecutor.execute("Select * from equipment_cold_chain_equipments ce JOIN equipments e ON ce.equipmentid=e.id  where equipmentid = " + coldChainEquipment.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("refrigerant"), "NewRefrigerant");
    assertEquals(rs.getString("model"), "NewModel");
  }
}