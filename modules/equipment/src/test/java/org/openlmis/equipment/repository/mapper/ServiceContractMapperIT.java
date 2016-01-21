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
import org.openlmis.equipment.builder.EquipmentTypeBuilder;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.domain.ServiceType;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.dto.ContractDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ServiceContractMapperIT {

    @Autowired
    ServiceContractMapper mapper;

    @Autowired
    VendorMapper vendorMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ServiceTypeMapper serviceTypeMapper;

    @Autowired
    EquipmentTypeMapper equipmentTypeMapper;

    Vendor vendor;

    @Before
    public void setup() {
        vendor = new Vendor();
        vendor.setName("The Vendor");
        vendor.setEmail("vendor@nowhere.nohow");
        vendor.setWebsite("1.com");
        vendorMapper.insert(vendor);
    }


    private ServiceContract createServiceContract() {
        ServiceContract contract = new ServiceContract();
        contract.setDescription("The service description");
        contract.setContractDate(new Date());
        contract.setCoverage("The coverage");
        contract.setEndDate(new Date());
        contract.setStartDate(new Date());
        contract.setIdentifier("123");
        contract.setTerms("The terms of service goes here");
        contract.setVendorId(vendor.getId());
        return contract;
    }

    @Test
    public void shouldGetById() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        ServiceContract result = mapper.getById(contract.getId());

        assertThat(result.getCoverage(), is(contract.getCoverage()));
        assertThat(result.getDescription(), is(contract.getDescription()));
    }


    @Test
    public void shouldGetFacilityOptions() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        List<ContractDetail> facilities = mapper.getFacilityOptions(contract.getId());
        assertThat(facilities.size(), is(notNullValue()));

    }

    @Test
    public void shouldGetServiceTypes() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        List<?> list = mapper.getServiceTypes(contract.getId());
        assertThat(list.size(), is(notNullValue()));
    }

    @Test
    public void shouldGetEquipments() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        List<?> equipments = mapper.getEquipments(contract.getId());
        assertThat(equipments.size(), is(notNullValue()));
    }

    @Test
    public void shouldGetAll() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        List<?> contracts = mapper.getAll();
        assertThat(contracts.size(), is(greaterThan(0)));
    }

    @Test
    public void shouldGetAllForFacility() throws Exception {
        mapper.getAllForEquipment(2L);
    }

    @Test
    public void shouldGetAllForVendor() throws Exception {
        mapper.getAllForVendor(2L);
    }

    @Test
    public void shouldGetAllForEquipment() throws Exception {
        mapper.getAllForEquipment(2L);
    }

    @Test
    public void shouldInsert() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);
        assertThat(contract.getId(), is(notNullValue()));
    }

    @Test
    public void shouldUpdate() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        contract.setTerms("Term 2");
        contract.setIdentifier("Identifier 2");

        mapper.update(contract);
        ServiceContract result = mapper.getById(contract.getId());

        assertThat(result.getTerms(), is(contract.getTerms()));
        assertThat(result.getIdentifier(), is(contract.getIdentifier()));
    }

    @Test
    public void shouldDeleteEquipments() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        mapper.deleteEquipments(contract.getId());

        // there is no verification here, however this test is still effective as it checks if the schema supports this specific query.
    }

    @Test
    public void shouldDeleteServiceTypes() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        mapper.deleteServiceTypes(contract.getId());

        // there is no verification here, however this test is still effective as it checks if the schema supports this specific query.
    }

    @Test
    public void shouldDeleteFacilities() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        mapper.deleteFacilities(contract.getId());

        // there is no verification here, however this test is still effective as it checks if the schema supports this specific query.
    }

    @Test
    public void shouldInsertEquipment() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        EquipmentType type = make(a(EquipmentTypeBuilder.defaultEquipmentType));
        equipmentTypeMapper.insert(type);

        mapper.insertEquipment(contract.getId(), type.getId());

    }

    @Test
    public void shouldInsertServiceTypes() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        ServiceType type = new ServiceType();
        type.setName("Maintenance 1");
        type.setDescription("Sample Description");
        serviceTypeMapper.insert(type);

        mapper.insertServiceTypes(contract.getId(), type.getId());
    }

    @Test
    public void shouldInsertFacilities() throws Exception {
        ServiceContract contract = createServiceContract();
        mapper.insert(contract);

        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);

        mapper.insertFacilities(contract.getId(), facility.getId());
    }
}