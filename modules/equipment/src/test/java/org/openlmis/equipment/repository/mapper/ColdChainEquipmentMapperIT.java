/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
import static org.hamcrest.Matchers.greaterThan;
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

    List<ColdChainEquipment> results =  mapper.getAll(type.getId());
    assertThat(results.size(), greaterThan(0));
   // assertEquals(coldChainEquipment.getName(), results.get(0).getName());

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