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

package org.openlmis.vaccine.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.repository.EstimateCategoryRepository;
import org.openlmis.demographics.service.PopulationService;
import org.openlmis.demographics.test.FacilityTreeTest;
import org.openlmis.vaccine.dto.FacilityProgramProductISADTO;
import org.openlmis.core.repository.*;
import org.openlmis.db.categories.UnitTests;

import java.util.Arrays;
import java.util.Collections;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramProductISAHandlerTest extends FacilityTreeTest
{
    @Mock
    ProgramProductRepository programProductRepository;

    @Mock
    ProgramRepository programRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    EstimateCategoryRepository estimateCategoryRepository;

    @Mock
    protected PopulationService populationService;


    @InjectMocks
    FacilityProgramProductISAHandler handler;

    Facility facility;

    Product product;


    FacilityProgramProductISADTO fppISA;
    Double whoRatio;
    Integer dosesPerYear;
    Double wastageFactor;
    Double bufferPercentage;
    Integer adjustmentValue;

    private void setupPopulations()
    {
        when(populationService.getPopulation(sdp1, program, category.getId())).thenReturn(80L);
        when(populationService.getPopulation(sdp2, program, category.getId())).thenReturn(70L);
        when(populationService.getPopulation(sdp3, program, category.getId())).thenReturn(100L);
        when(populationService.getPopulation(sdp4, program, category.getId())).thenReturn(110L);
        when(populationService.getPopulation(sdp5, program, category.getId())).thenReturn(90L);
    }

    protected void setupFacilityHierarchy()
    {
        super.setupFacilityHierarchy();
        setupPopulations();
    }

    @Before
    public void setup()
    {
        super.setup();
        facility = make(a(FacilityBuilder.defaultFacility));
        product = make(a(ProductBuilder.defaultProduct));

        whoRatio = 0.0;
        dosesPerYear = 0;
        wastageFactor = 7.0;
        bufferPercentage = 0.0;
        adjustmentValue = 0;

        fppISA = new FacilityProgramProductISADTO();
        fppISA.setFacility(facility);
        fppISA.setProgram(program);
        fppISA.setProduct(product);
        fppISA.setWhoRatio(whoRatio);
        fppISA.setDosesPerYear(dosesPerYear);
        fppISA.setWastageFactor(wastageFactor);
        fppISA.setBufferPercentage(bufferPercentage);
        fppISA.setAdjustmentValue(adjustmentValue);
    }


    @Test
    public void shouldGetFacilityProgramProductISA() {
        FacilityProgramProduct expectedFpp = new FacilityProgramProduct();

        when(facilityProgramProductRepository.getByCodes(any(String.class), any(String.class), any(String.class))).thenReturn(expectedFpp);

        BaseModel returnedFpp = handler.getExisting(fppISA);

        assertThat((FacilityProgramProduct)returnedFpp, is(expectedFpp));
    }

    @Test
    public void shouldSaveFacilityProgramProductISA() {
        when(facilityRepository.getByCode(any(String.class))).thenReturn(facility);
        when(programProductRepository.getByProgramAndProductCode(any(ProgramProduct.class))).thenReturn(programProduct);
        when(programRepository.getByCode(any(String.class))).thenReturn(program);
        when(estimateCategoryRepository.getByName(any(String.class))).thenReturn(category);
        when(supervisoryNodeRepository.getFor(any(Facility.class), any(Program.class))).thenReturn(null);

        ISA isa = new ISA();
        isa.setWhoRatio(whoRatio);
        isa.setDosesPerYear(dosesPerYear);
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(bufferPercentage);
        isa.setAdjustmentValue(adjustmentValue);
        isa.setPopulationSource(category.getId());

        ProgramProductISA ppISA = new ProgramProductISA(programProduct.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(facility.getId());
        fpp.setId(programProduct.getId());
        fpp.setProgramProductIsa(ppISA);
        fpp.setOverriddenIsa(isa);

        handler.save(fppISA);

        verify(facilityProgramProductRepository).save(fpp);
    }

    @Test
    public void shouldSaveFacilityProgramProductISAAndCalculateParents() {
        setupFacilityHierarchy();

        when(facilityRepository.getByCode(any(String.class))).thenReturn(sdp1);
        when(programProductRepository.getByProgramAndProductCode(any(ProgramProduct.class))).thenReturn(programProduct);
        when(programRepository.getByCode(any(String.class))).thenReturn(program);
        when(estimateCategoryRepository.getByName(any(String.class))).thenReturn(category);

        ISA isa = new ISA();
        isa.setWhoRatio(whoRatio);
        isa.setDosesPerYear(dosesPerYear);
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(bufferPercentage);
        isa.setAdjustmentValue(adjustmentValue);
        isa.setPopulationSource(category.getId());

        ProgramProductISA ppISA = new ProgramProductISA(programProduct.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(sdp1.getId());
        fpp.setId(programProduct.getId());
        fpp.setProgramProductIsa(ppISA);
        fpp.setOverriddenIsa(isa);

        handler.save(fppISA);

        verify(facilityProgramProductRepository).save(fpp);

        isa.setWastageFactor(8.2);
        fpp.setFacilityId(dvs1.getId());
        verify(facilityProgramProductRepository).save(fpp);

        isa.setWastageFactor(8.72);
        fpp.setFacilityId(rvs1.getId());
        verify(facilityProgramProductRepository).save(fpp);

        isa.setWastageFactor(8.8);
        fpp.setFacilityId(cvs.getId());
        verify(facilityProgramProductRepository).save(fpp);
    }
}
