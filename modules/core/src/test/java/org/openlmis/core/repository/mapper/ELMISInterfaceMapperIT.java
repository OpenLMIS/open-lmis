/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *   Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ELMISInterfaceBuilder;
import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceDataSet;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ELMISInterfaceMapperIT {

   @Autowired
   ELMISInterfaceMapper mapper;

    @Test
    public void shouldGetInterfaceAppById(){

        ELMISInterface defaultInterface = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "APP1")));
        mapper.insert(defaultInterface);

        ELMISInterfaceDataSet dt01 = new ELMISInterfaceDataSet(defaultInterface.getId(), "Dataset01Name", "Dataset01Id");
        ELMISInterfaceDataSet dt02 = new ELMISInterfaceDataSet(defaultInterface.getId(), "Dataset02Name", "Dataset02Id");
        mapper.insertDataSet(dt01);
        mapper.insertDataSet(dt02);

       ELMISInterface selectedInterface = mapper.get(defaultInterface.getId());
       assertThat(selectedInterface.getActive(), is(true));
       assertThat(selectedInterface.getName(), is("APP1"));
       assertThat(selectedInterface.getDataSets().size(), is(2));
    }

    @Test
    public void shouldGetInterfaceDatasetById(){

        ELMISInterface defaultInterface = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "APP2")));
        mapper.insert(defaultInterface);

        ELMISInterfaceDataSet dt01 = new ELMISInterfaceDataSet(defaultInterface.getId(), "Dataset03Name", "Dataset03Id");
        mapper.insertDataSet(dt01);

        List<ELMISInterfaceDataSet> datasets = mapper.getInterfaceDatasetById(defaultInterface.getId());
        assertThat(datasets.size(), is(1));
        assertThat(datasets.get(0).getDataSetname(), is("Dataset03Name"));
    }

    @Test
    public void shouldInsertELMISInterface(){

        ELMISInterface defaultInterface = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "APP3")));
        mapper.insert(defaultInterface);

        ELMISInterface interfaceApp = mapper.get(defaultInterface.getId());

        assertThat(interfaceApp.getName(), is("APP3"));
        assertThat(interfaceApp.getId(), is(notNullValue()));
    }

    @Test
    public void shouldUpdateELMISInterface(){

        ELMISInterface defaultInterface = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface")));
        mapper.insert(defaultInterface);

        Date modifiedDate = new Date();
        defaultInterface.setModifiedDate(modifiedDate);
        mapper.update(defaultInterface);

        ELMISInterface updatedInterfaceApp = mapper.get(defaultInterface.getId());
        assertThat(updatedInterfaceApp.getModifiedDate(), is(modifiedDate));

    }

    @Test
    public void shouldUpdateFacilityInterfaceDataset(){

        ELMISInterface defaultInterface = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 123")));
        mapper.insert(defaultInterface);

        ELMISInterfaceDataSet dt01 = new ELMISInterfaceDataSet(defaultInterface.getId(), "DatasetName123", "DatasetId123");
        mapper.insertDataSet(dt01);

        List<ELMISInterfaceDataSet> datasets = mapper.getInterfaceDatasetById(defaultInterface.getId());

        Date modifiedDate = new Date();
        datasets.get(0).setModifiedDate(modifiedDate);
        mapper.updateDataSet(datasets.get(0));

        List<ELMISInterfaceDataSet> UpdatedDatasets = mapper.getInterfaceDatasetById(defaultInterface.getId());
        assertThat(UpdatedDatasets.get(0).getModifiedDate(), is(modifiedDate));

    }

    @Test
    public void shouldGetAllinterfaces(){

        ELMISInterface defaultInterface01 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 01")));
        mapper.insert(defaultInterface01);

        ELMISInterface defaultInterface02 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 02")));
        mapper.insert(defaultInterface02);

        ELMISInterface defaultInterface03 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 03")));
        mapper.insert(defaultInterface03);

        ELMISInterface defaultInterface04 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 04")));
        mapper.insert(defaultInterface04);

        List<ELMISInterface> interfaces = mapper.getAllInterfaces();

        assertThat(interfaces.size(), is(4));
    }

    @Test
    public void shouldGetAllActiveInterfaces(){

        ELMISInterface defaultInterface01 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 01")));
        defaultInterface01.setActive(false);
        mapper.insert(defaultInterface01);

        ELMISInterface defaultInterface02 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 02")));
        mapper.insert(defaultInterface02);

        ELMISInterface defaultInterface03 = make(a(ELMISInterfaceBuilder.defaultELMISInterface, with(ELMISInterfaceBuilder.name, "New interface 03")));
        mapper.insert(defaultInterface03);

        List<ELMISInterface> interfaces = mapper.getAllActiveInterfaces();

        assertThat(interfaces.size(), is(2));
    }
}
