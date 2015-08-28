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

package org.openlmis.core.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceDataSet;
import org.openlmis.core.domain.ELMISInterfaceFacilityMapping;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.ELMISInterfaceMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ELMISInterfaceRepositoryTest {

    @Mock
    ELMISInterfaceMapper mapper;

    @InjectMocks
    ELMISInterfaceRepository repository;

    @Rule
    public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

    @Test
    public void shouldGetELMISInterface(){

        ELMISInterface it = new ELMISInterface();
        when(mapper.get(123L)).thenReturn(it);
        ELMISInterface elmisInterface  = repository.get(123L);

        verify(mapper).get(123L);
        assertThat(elmisInterface, is(it));
    }

    @Test
    public void shouldInsert(){
        ELMISInterface it = new ELMISInterface();
        repository.insert(it);

        verify(mapper).insert(it);
    }

    @Test
    public void shouldGetAllInterfaces(){
        repository.getAllActiveInterfaces();
        verify(mapper).getAllActiveInterfaces();
    }

    @Test
    public void shouldGetInterfaceFacilityMappings(){
        repository.getInterfaceFacilityMappings();
        verify(mapper).getInterfaceFacilityMappings();
    }

    @Test
    public void shouldGetFacilityInterfaceMappingById(){

        List<ELMISInterfaceFacilityMapping> mappingsList = new ArrayList<>();
        when(mapper.getFacilityInterfaceMappingById(123L)).thenReturn(mappingsList);
        List<ELMISInterfaceFacilityMapping> mappings = repository.getFacilityInterfaceMappingById(123L);

        verify(mapper).getFacilityInterfaceMappingById(123L);
        assertThat(mappings, is(mappingsList));
    }

    @Test
    public void shouldGetAllActiveInterfaces(){
        repository.getAllActiveInterfaces();
        verify(mapper).getAllActiveInterfaces();
    }

    @Test
    public void shouldNotInsertExistingInterfaceDataset(){

        ELMISInterface elmisInterface = new ELMISInterface();
        elmisInterface.setId(123L);

        List<ELMISInterfaceDataSet> existingDataset = new ArrayList<>();
        ELMISInterfaceDataSet dataSet = new ELMISInterfaceDataSet();
        dataSet.setId(134L);
        dataSet.setModifiedBy(1L);

        existingDataset.add(dataSet);
        elmisInterface.setDataSets(existingDataset);

        when(mapper.getInterfaceDatasetById(123L)).thenReturn(existingDataset);

        repository.updateELMISInterfaceDataSets(elmisInterface);

        verify(mapper).updateDataSet(dataSet);
        verify(mapper, never()).insertDataSet(dataSet);
        verify(mapper, never()).deleteDataset(dataSet);
    }

    @Test
    public void shouldThrowDuplicateKeyException(){

        Facility facility = new Facility();
        List<ELMISInterfaceFacilityMapping> mappingList = asList(new ELMISInterfaceFacilityMapping());
        ELMISInterfaceFacilityMapping mapping = new ELMISInterfaceFacilityMapping();
        facility.setInterfaceMappings(mappingList);

        when(mapper.getFacilityInterfaceMappingById(facility.getId())).thenReturn(mappingList);
        when(mapper.insertFacilityMapping(mapping)).thenThrow(new DuplicateKeyException(""));

        expectedEx.expect(dataExceptionMatcher("error.facility.interface.mapping.exists"));
        repository.updateFacilityInterfaceMapping(facility);

    }

}
