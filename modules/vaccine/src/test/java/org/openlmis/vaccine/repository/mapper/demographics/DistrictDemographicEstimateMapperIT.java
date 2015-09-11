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

package org.openlmis.vaccine.repository.mapper.demographics;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.domain.demographics.DistrictDemographicEstimate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DistrictDemographicEstimateMapperIT {

    @Autowired
    GeographicZoneMapper geographicZoneMapper;

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    DemographicEstimateCategoryMapper demographicEstimateCategoryMapper;

    @Autowired
    private DistrictDemographicEstimateMapper mapper;

    private GeographicZone district;

    private Program program;

    private DemographicEstimateCategory category;

    @Before
    public void setup() {
        district = new GeographicZone(null, "code", "name",
                new GeographicLevel(2L, "state", "State", 2), null);

        geographicZoneMapper.insert(district);
        program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);

        category = new DemographicEstimateCategory("Live Birth", "", true, 1.0);
        demographicEstimateCategoryMapper.insert(category);
    }

    @Test
    public void shouldInsert() throws Exception {
        DistrictDemographicEstimate districtDemographicEstimate = createDistrictDemographicEstimate();

        Integer result = mapper.insert(districtDemographicEstimate);

        assertThat(result, is(1));
        assertThat(districtDemographicEstimate.getId(), is(notNullValue()));
    }

    private DistrictDemographicEstimate createDistrictDemographicEstimate() {
        DistrictDemographicEstimate districtDemographicEstimate = new DistrictDemographicEstimate();
        districtDemographicEstimate.setProgramId(program.getId());
        districtDemographicEstimate.setYear(2005);
        districtDemographicEstimate.setDistrictId(district.getId());
        districtDemographicEstimate.setDemographicEstimateId(category.getId());
        districtDemographicEstimate.setConversionFactor(category.getDefaultConversionFactor());
        districtDemographicEstimate.setValue(1000L);
        return districtDemographicEstimate;
    }

    @Test
    public void shouldUpdate() throws Exception {
        DistrictDemographicEstimate districtDemographicEstimate = createDistrictDemographicEstimate();
        mapper.insert(districtDemographicEstimate);

        districtDemographicEstimate.setValue(10002L);
        Integer result = mapper.update(districtDemographicEstimate);

        assertThat(result, is(1));
        // check if the value is actually saved here.
        assertThat(mapper.getById(districtDemographicEstimate.getId()).getValue(), is(districtDemographicEstimate.getValue()));

    }

    @Test
    public void shouldFinalize() throws Exception {
        DistrictDemographicEstimate districtDemographicEstimate = createDistrictDemographicEstimate();
        mapper.insert(districtDemographicEstimate);

        Integer result = mapper.finalize(districtDemographicEstimate);
        assertThat(result, is(1));
        // check if the value is actually saved here.
        assertThat(mapper.getById(districtDemographicEstimate.getId()).getIsFinal(), is(true));
    }

    @Test
    public void shouldUndoFinalize() throws Exception {
        DistrictDemographicEstimate districtDemographicEstimate = createDistrictDemographicEstimate();
        mapper.insert(districtDemographicEstimate);

        Integer result = mapper.undoFinalize(districtDemographicEstimate);
        assertThat(result, is(1));
        // check if the value is actually saved here.
        assertThat(mapper.getById(districtDemographicEstimate.getId()).getIsFinal(), is(false));
    }

    @Test
    public void shouldGetEstimatesForDistrict() throws Exception {

    }

    @Test
    public void shouldGetDistricts() throws Exception {

    }
}