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

import org.hamcrest.Matchers;
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
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
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
@ContextConfiguration(locations = "classpath*:test-applicationContext-db.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class AnnualDistrictEstimateMapperIT {

  @Autowired
  GeographicZoneMapper geographicZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  EstimateCategoryMapper estimateCategoryMapper;

  @Autowired
  private AnnualDistrictEstimateMapper mapper;

  private GeographicZone district;

  private Program program;

  private EstimateCategory category;

  @Before
  public void setup() {
    district = new GeographicZone(null, "code", "name",
      new GeographicLevel(2L, "state", "State", 2), null);

    geographicZoneMapper.insert(district);
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    category = new EstimateCategory("Live Birth", "", true, 1.0);
    estimateCategoryMapper.insert(category);
  }

  @Test
  public void shouldInsert() throws Exception {
    AnnualDistrictEstimateEntry annualDistrictEstimateEntry = createDistrictDemographicEstimate();

    Integer result = mapper.insert(annualDistrictEstimateEntry);

    assertThat(result, is(1));
    assertThat(annualDistrictEstimateEntry.getId(), is(notNullValue()));
  }

  private AnnualDistrictEstimateEntry createDistrictDemographicEstimate() {
    AnnualDistrictEstimateEntry annualDistrictEstimateEntry = new AnnualDistrictEstimateEntry();
    annualDistrictEstimateEntry.setProgramId(program.getId());
    annualDistrictEstimateEntry.setYear(2005);
    annualDistrictEstimateEntry.setDistrictId(district.getId());
    annualDistrictEstimateEntry.setDemographicEstimateId(category.getId());
    annualDistrictEstimateEntry.setConversionFactor(category.getDefaultConversionFactor());
    annualDistrictEstimateEntry.setValue(1000L);
    return annualDistrictEstimateEntry;
  }

  @Test
  public void shouldUpdate() throws Exception {
    AnnualDistrictEstimateEntry annualDistrictEstimateEntry = createDistrictDemographicEstimate();
    mapper.insert(annualDistrictEstimateEntry);

    annualDistrictEstimateEntry.setValue(10002L);
    Integer result = mapper.update(annualDistrictEstimateEntry);

    assertThat(result, is(1));
    // check if the value is actually saved here.
    assertThat(mapper.getById(annualDistrictEstimateEntry.getId()).getValue(), is(annualDistrictEstimateEntry.getValue()));

  }

  @Test
  public void shouldFinalize() throws Exception {
    AnnualDistrictEstimateEntry annualDistrictEstimateEntry = createDistrictDemographicEstimate();
    mapper.insert(annualDistrictEstimateEntry);

    Integer result = mapper.finalizeEstimate(annualDistrictEstimateEntry);
    assertThat(result, is(1));
    // check if the value is actually saved here.
    assertThat(mapper.getById(annualDistrictEstimateEntry.getId()).getIsFinal(), is(true));
  }

  @Test
  public void shouldUndoFinalize() throws Exception {
    AnnualDistrictEstimateEntry annualDistrictEstimateEntry = createDistrictDemographicEstimate();
    mapper.insert(annualDistrictEstimateEntry);

    Integer result = mapper.undoFinalize(annualDistrictEstimateEntry);
    assertThat(result, is(1));
    // check if the value is actually saved here.
    assertThat(mapper.getById(annualDistrictEstimateEntry.getId()).getIsFinal(), is(false));
  }

  @Test
  public void shouldGetByYearProgramAndEstimate() throws Exception {
    AnnualDistrictEstimateEntry estimate = createDistrictDemographicEstimate();

    mapper.insert(estimate);
    AnnualDistrictEstimateEntry response = mapper.getEntryBy(estimate.getYear(), estimate.getDistrictId(), estimate.getProgramId(), estimate.getDemographicEstimateId());

    assertThat(response, Matchers.is(Matchers.notNullValue()));
    assertThat(response.getValue(), Matchers.is(estimate.getValue()));
  }

}