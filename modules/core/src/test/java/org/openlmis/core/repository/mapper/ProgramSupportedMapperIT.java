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
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.*;
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
  private ProgramSupportedMapper mapper;

  @Test
  public void shouldSaveProgramSupported() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));

    mapper.insert(programSupported);

    ProgramSupported result = mapper.getBy(facility.getId(), program.getId());
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
    mapper.insert(programSupported);

    mapper.delete(facility.getId(), program.getId());

    ProgramSupported programsSupported = mapper.getBy(facility.getId(), program.getId());
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
    mapper.insert(programSupported);

    List<ProgramSupported> programsSupported = mapper.getAllByFacilityId(facility.getId());
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
    mapper.insert(programSupported);

    programSupported.setActive(Boolean.FALSE);

    mapper.update(programSupported);

    ProgramSupported programSupportedFromDb = mapper.getBy(programSupported.getFacilityId(), programSupported.getProgram().getId());

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

    Program inactiveProgram = make(a(defaultProgram, with(programCode, "globallyInactiveProgram"), with(programActive, false)));
    programMapper.insert(inactiveProgram);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    ProgramSupported programSupported2 = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program2), with(isActive, false)));
    ProgramSupported inactiveProgramSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, inactiveProgram), with(isActive, true)));

    mapper.insert(programSupported);
    mapper.insert(programSupported2);
    mapper.insert(inactiveProgramSupported);

    List<ProgramSupported> programsSupported = mapper.getActiveProgramsByFacilityId(facility.getId());

    assertThat(programsSupported.size(), is(1));
    assertThat(programsSupported.get(0).getProgram().getCode(), is(programSupported.getProgram().getCode()));
  }

  @Test
  public void shouldDeleteProgramsSupportedForVirtualFacilities() throws Exception {
    Facility parentFacility = make(a(defaultFacility));
    facilityMapper.insert(parentFacility);

    Facility virtualFacility = make(a(defaultFacility,
      with(code, "Child"),
      with(parentFacilityId, parentFacility.getId())));

    facilityMapper.insert(virtualFacility);


    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupportedParent = make(a(defaultProgramSupported,
      with(supportedFacilityId, parentFacility.getId()),
      with(supportedProgram, program)));

    ProgramSupported programSupportedVirtual = make(a(defaultProgramSupported,
      with(supportedFacilityId, virtualFacility.getId()),
      with(supportedProgram, program)));

    mapper.insert(programSupportedParent);
    mapper.insert(programSupportedVirtual);


    assertThat(mapper.deleteVirtualFacilityProgramSupported(parentFacility), is(1));

    assertThat(mapper.getAllByFacilityId(virtualFacility.getId()).size(), is(0));
    assertThat(mapper.getAllByFacilityId(parentFacility.getId()).size(), is(1));
  }

  @Test
  public void shouldCopyProgramSupportedFromParentFacility() throws Exception {
    Facility parentFacility = make(a(defaultFacility));
    facilityMapper.insert(parentFacility);

    Facility virtualFacility = make(a(defaultFacility, with(code, "Child"), with(parentFacilityId, parentFacility.getId())));
    facilityMapper.insert(virtualFacility);

    Facility virtualFacility2 = make(a(defaultFacility, with(code, "Child2"), with(parentFacilityId, parentFacility.getId())));
    facilityMapper.insert(virtualFacility2);

    Facility facility = make(a(defaultFacility, with(code, "root")));
    facilityMapper.insert(facility);


    Program program1 = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    Program program2 = make(a(defaultProgram, with(programCode, GREEN_FEVER)));
    programMapper.insert(program1);
    programMapper.insert(program2);

    ProgramSupported programSupportedParent1 = make(a(defaultProgramSupported,
      with(supportedFacilityId, parentFacility.getId()), with(supportedProgram, program1)));
    mapper.insert(programSupportedParent1);

    ProgramSupported programSupportedParent2 = make(a(defaultProgramSupported,
      with(supportedFacilityId, parentFacility.getId()), with(supportedProgram, program2)));
    mapper.insert(programSupportedParent2);

    ProgramSupported rootProgramSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()), with(supportedProgram, program2)));
    mapper.insert(rootProgramSupported);


    mapper.copyToVirtualFacilities(parentFacility);


    List<ProgramSupported> programsSupported = mapper.getAllByFacilityId(virtualFacility.getId());

    assertThat(programsSupported.size(), is(2));
    assertThat(programsSupported.get(0).getActive(), is(programSupportedParent1.getActive()));
    assertThat(programsSupported.get(0).getStartDate(), is(programSupportedParent1.getStartDate()));
    assertThat(programsSupported.get(0).getModifiedBy(), is(programSupportedParent1.getModifiedBy()));
    assertThat(programsSupported.get(0).getCreatedBy(), is(programSupportedParent1.getCreatedBy()));

    assertThat(programsSupported.get(1).getActive(), is(programSupportedParent2.getActive()));
    assertThat(programsSupported.get(1).getStartDate(), is(programSupportedParent2.getStartDate()));
    assertThat(programsSupported.get(1).getModifiedBy(), is(programSupportedParent2.getModifiedBy()));
    assertThat(programsSupported.get(1).getCreatedBy(), is(programSupportedParent2.getCreatedBy()));


    List<ProgramSupported> programsSupported2 = mapper.getAllByFacilityId(virtualFacility2.getId());
    assertThat(programsSupported2.size(), is(2));

    assertThat(mapper.getAllByFacilityId(facility.getId()).size(), is(1));
  }
}
