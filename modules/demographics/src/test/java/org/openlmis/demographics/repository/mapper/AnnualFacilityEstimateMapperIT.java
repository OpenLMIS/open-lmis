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

package org.openlmis.demographics.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-db.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class AnnualFacilityEstimateMapperIT {

  @Autowired
  AnnualFacilityEstimateMapper mapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  EstimateCategoryMapper estimateCategoryMapper;

  private Facility facility;

  private EstimateCategory category;

  private Program program;

  @Before
  public void setup() {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    category = new EstimateCategory();
    category.setName("1 - 2 years of age");
    category.setDescription("Random Description");
    category.setDefaultConversionFactor(1.0);
    category.setIsPrimaryEstimate(false);
    estimateCategoryMapper.insert(category);
  }

  private AnnualFacilityEstimateEntry createAFacilityDemographicEstimate() {
    AnnualFacilityEstimateEntry estimate = new AnnualFacilityEstimateEntry();

    estimate.setFacilityId(facility.getId());
    estimate.setProgramId(program.getId());
    estimate.setDemographicEstimateId(category.getId());
    estimate.setYear(2005);
    estimate.setConversionFactor(1.0);
    estimate.setValue(1231L);
    return estimate;
  }

  @Test
  public void shouldInsert() throws Exception {
    AnnualFacilityEstimateEntry estimate = createAFacilityDemographicEstimate();

    Integer result = mapper.insert(estimate);

    assertThat(result, is(1));
    assertThat(estimate.getId(), is(notNullValue()));
  }


  @Test
  public void shouldUpdate() throws Exception {
    AnnualFacilityEstimateEntry estimate = createAFacilityDemographicEstimate();

    mapper.insert(estimate);

    estimate.setValue(0L);
    mapper.update(estimate);

    List<AnnualFacilityEstimateEntry> list = mapper.getEstimatesForFacility(2005, facility.getId(), program.getId());
    assertThat(list.size(), is(1));
    assertThat(list.get(0).getValue(), is(0L));
  }

  @Test
  public void shouldGetEstimatesForFacility() throws Exception {
    AnnualFacilityEstimateEntry estimate = createAFacilityDemographicEstimate();

    mapper.insert(estimate);

    List<AnnualFacilityEstimateEntry> list = mapper.getEstimatesForFacility(2005, facility.getId(), program.getId());
    assertThat(list.size(), is(1));
    assertThat(list.get(0).getValue(), is(estimate.getValue()));
  }

  @Test
  public void shouldGetByYearProgramAndEstimate() throws Exception {
    AnnualFacilityEstimateEntry estimate = createAFacilityDemographicEstimate();

    mapper.insert(estimate);
    AnnualFacilityEstimateEntry response = mapper.getEntryBy(estimate.getYear(), estimate.getFacilityId(), estimate.getProgramId(), estimate.getDemographicEstimateId());

    assertThat(response, is(notNullValue()));
    assertThat(response.getValue(), is(estimate.getValue()));
  }
}