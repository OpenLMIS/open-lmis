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


package org.openlmis.demographics.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;
import org.openlmis.demographics.test.FacilityTreeTest;
import org.openlmis.report.mapper.lookup.FacilityLevelMapper;
import org.openlmis.report.model.dto.FacilityLevelTree;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PopulationServiceTest extends FacilityTreeTest
{
    @InjectMocks
    private PopulationService populationService;

    ProgramProduct programProduct;

    static final Long populationSourceId_1 = 1L;
    static final Long populationSourceId_2 = 2L;

    @Before
    public void setup()
    {
        super.setup();
        setupDemographyEstimates();
    }


    private List<AnnualFacilityEstimateEntry> getFacilityDemographyEstimates(Long facilityId, Long programId, Integer year)
    {
        //Create an estimate for the specified facility, program, and year
        AnnualFacilityEstimateEntry entry1 = new AnnualFacilityEstimateEntry();
        entry1.setFacilityId(facilityId);
        entry1.setProgramId(programId);
        entry1.setYear(year);

        //Set the EstimateId and Value to correlated numbers
        entry1.setDemographicEstimateId(new Long(populationSourceId_1));
        entry1.setValue(facilityId * populationSourceId_1 * 100);

        //Create another estimate for the specified facility, program, and year
        AnnualFacilityEstimateEntry entry2 = new AnnualFacilityEstimateEntry();
        entry2.setFacilityId(facilityId);
        entry2.setProgramId(programId);
        entry2.setYear(year);

        //Set the EstimateId and Value to correlated numbers
        entry2.setDemographicEstimateId(new Long(populationSourceId_2));
        entry2.setValue(facilityId * populationSourceId_2 * 100);

        //Return the estimate2
        List<AnnualFacilityEstimateEntry> estimates = new LinkedList<>();
        estimates.add(entry1);
        estimates.add(entry2);
        return estimates;
    }

    private AnnualDistrictEstimateEntry getDistrictDemographyEstimate(Integer year, Long districtId, Long programId, Long categoryId)
    {
        //Create an estimate for the specified year, districtId, programId, and categoryId
        AnnualDistrictEstimateEntry estimateEntry = new AnnualDistrictEstimateEntry();
        estimateEntry.setYear(year);
        estimateEntry.setDistrictId(districtId);
        estimateEntry.setProgramId(programId);
        estimateEntry.setDemographicEstimateId(categoryId);

        //Set the value to a number correlated with the facilityId
        estimateEntry.setValue(districtId * categoryId * 100);

        return  estimateEntry;
    }

    //Note that Facility here is meant to refer to an SDP
    private void setupFacilityDemographyEstimate(Long facilityId, Long programId, Integer year)
    {
        when(annualFacilityDemographicEstimateService.getEstimateValuesForFacility(facilityId, programId, year)).thenReturn(getFacilityDemographyEstimates(facilityId, programId, year));
    }


    private void setupDistrictDemographyEstimate(Integer year, Long districtAndGeoZoneId, Long programId, Long categoryId)
    {
        when(annualDistrictEstimateRepository.getEntryBy(year, districtAndGeoZoneId, programId, categoryId)).thenReturn(getDistrictDemographyEstimate(year, districtAndGeoZoneId, programId, categoryId));
    }

    private void setupDemographyEstimates()
    {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        setupFacilityDemographyEstimate(sdp1.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp2.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp3.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp4.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp5.getId(), program.getId(), currentYear);

        setupDistrictDemographyEstimate(currentYear, dvs1.getId(), program.getId(), populationSourceId_1);
        setupDistrictDemographyEstimate(currentYear, dvs1.getId(), program.getId(), populationSourceId_2);

        setupDistrictDemographyEstimate(currentYear, dvs2.getId(), program.getId(), populationSourceId_1);
        setupDistrictDemographyEstimate(currentYear, dvs2.getId(), program.getId(), populationSourceId_2);

        setupDistrictDemographyEstimate(currentYear, dvs3.getId(), program.getId(), populationSourceId_1);
        setupDistrictDemographyEstimate(currentYear, dvs3.getId(), program.getId(), populationSourceId_2);
    }

    @Test
    public void shouldUseSpecifiedPopulationSourceForServiceDeliveryPoint()
    {
        Long returnedPopulation = populationService.getPopulation(sdp1, program, populationSourceId_1);
        Long expectedPopulation = sdp1.getId() * populationSourceId_1 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));

        returnedPopulation = populationService.getPopulation(sdp1, program, populationSourceId_2);
        expectedPopulation = sdp1.getId() * populationSourceId_2 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseSpecifiedPopulationSourceForDistrictVaccineStore()
    {
        Long returnedPopulation = populationService.getPopulation(dvs1, program, populationSourceId_1);
        Long expectedPopulation = dvs1.getId() * populationSourceId_1 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));

        returnedPopulation = populationService.getPopulation(dvs1, program, populationSourceId_2);
        expectedPopulation = dvs1.getId() * populationSourceId_2 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseFacilityCatchmentPopulationForServiceDeliveryPointWhenPopulationSourceIsNull()
    {
        Long returnedPopulation = populationService.getPopulation(sdp1, program, null);
        Long expectedPopulation = sdp1.getId() * 900;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseFacilityCatchmentPopulationForDistrictVaccineStoreWhenPopulationSourceIsNull()
    {
        Long returnedPopulation = populationService.getPopulation(dvs1, program, null);
        Long expectedPopulation = dvs1.getId() * 900;
        assertThat(returnedPopulation, is(expectedPopulation));
    }


    @Test
    public void shouldDerivePopulationForCentralVaccineStoreFromChildFacilityPopulations()
    {
        Long returnedPopulation = populationService.getPopulation(cvs, program, populationSourceId_1);

        Long sdp1Population = sdp1.getId() * populationSourceId_1 * 100;
        Long sdp2Population = sdp2.getId() * populationSourceId_1 * 100;
        Long sdp3Population = sdp3.getId() * populationSourceId_1 * 100;
        Long sdp4Population = sdp4.getId() * populationSourceId_1 * 100;
        Long sdp5Population = sdp5.getId() * populationSourceId_1 * 100;

        Long dvs1Population = dvs1.getId() * populationSourceId_1 * 100;
        Long dvs2Population = dvs2.getId() * populationSourceId_1 * 100;
        Long dvs3Population = dvs3.getId() * populationSourceId_1 * 100;

        Long rvs1Population = dvs1Population + dvs2Population;
        Long rvs2Population = dvs3Population;
        Long expectedPopulation = rvs1Population + rvs2Population;

        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldDerivePopulationForRegionalVaccineStoreFromChildFacilityPopulations()
    {
        Long returnedPopulation = populationService.getPopulation(rvs1, program, populationSourceId_1);

        Long dvs1Population = dvs1.getId() * populationSourceId_1 * 100;
        Long dvs2Population = dvs2.getId() * populationSourceId_1 * 100;
        Long expectedPopulation = dvs1Population + dvs2Population;

        assertThat(returnedPopulation, is(expectedPopulation));
    }

}