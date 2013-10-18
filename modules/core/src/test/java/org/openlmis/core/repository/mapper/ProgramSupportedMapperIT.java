/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ProgramSupportedMapperIT {

  public static final String YELLOW_FEVER = "YELL_FVR";
  public static final String GREEN_FEVER = "green_fever";

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProgramSupportedMapper programSupportedMapper;

  @Test
  public void shouldSaveProgramSupported() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));

    programSupportedMapper.insert(programSupported);

    ProgramSupported result = programSupportedMapper.getBy(facility.getId(), program.getId());
    assertThat(result.getFacilityId(), is(facility.getId()));
    assertThat(result.getProgram().getId(), is(program.getId()));
    assertThat(result.getStartDate(), is(programSupported.getStartDate()));
  }

  @Test
  public void shouldDeleteProgramMapping() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.insert(programSupported);

    programSupportedMapper.delete(facility.getId(), program.getId());

    ProgramSupported programsSupported = programSupportedMapper.getBy(facility.getId(), program.getId());
    assertThat(programsSupported, is(nullValue()));
  }

  @Test
  public void shouldGetAllProgramsSupportedForFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.insert(programSupported);

    List<ProgramSupported> programsSupported = programSupportedMapper.getAllByFacilityId(facility.getId());
    assertThat(programsSupported.size(), is(1));
    assertThat(programsSupported.get(0).getFacilityId(), is(programSupported.getFacilityId()));
    assertThat(programsSupported.get(0).getStartDate(), is(programSupported.getStartDate()));
    assertThat(programsSupported.get(0).getActive(), is(programSupported.getActive()));
    assertThat(programsSupported.get(0).getProgram().getId(), is(programSupported.getProgram().getId()));
    assertThat(programsSupported.get(0).getProgram().getCode(), is(programSupported.getProgram().getCode()));
    assertThat(programsSupported.get(0).getProgram().getName(), is(programSupported.getProgram().getName()));
  }

  @Test
  public void shouldUpdateSupportedProgram() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.insert(programSupported);

    programSupported.setActive(Boolean.FALSE);

    programSupportedMapper.update(programSupported);

    ProgramSupported programSupportedFromDb = programSupportedMapper.getBy(programSupported.getFacilityId(), programSupported.getProgram().getId());

    assertThat(programSupportedFromDb.getActive(), is(Boolean.FALSE));
  }

  @Test
  public void shouldGetAllActiveProgramsSupportedForFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);
    Program program2 = make(a(defaultProgram, with(programCode, GREEN_FEVER)));
    programMapper.insert(program2);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    ProgramSupported programSupported2 = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program2),with(isActive,false)));

    programSupportedMapper.insert(programSupported);
    programSupportedMapper.insert(programSupported2);

    List<ProgramSupported> programsSupported = programSupportedMapper.getActiveByFacilityId(facility.getId());

    assertThat(programsSupported.size(), is(1));
    assertThat(programsSupported.get(0).getFacilityId(), is(programSupported.getFacilityId()));
    assertThat(programsSupported.get(0).getStartDate(), is(programSupported.getStartDate()));
    assertThat(programsSupported.get(0).getActive(), is(programSupported.getActive()));
    assertThat(programsSupported.get(0).getProgram().getId(), is(programSupported.getProgram().getId()));
    assertThat(programsSupported.get(0).getProgram().getCode(), is(programSupported.getProgram().getCode()));
    assertThat(programsSupported.get(0).getProgram().getName(), is(programSupported.getProgram().getName()));
  }

}
