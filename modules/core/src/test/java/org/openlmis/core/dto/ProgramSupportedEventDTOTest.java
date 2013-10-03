/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;

import org.junit.Test;
import org.openlmis.core.domain.ProgramSupported;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;

public class ProgramSupportedEventDTOTest {
  @Test
  public void shouldCreateProgramSupportedEventDTO() throws Exception {
    ProgramSupported programSupported = make(a(defaultProgramSupported));
    List<ProgramSupported> programSupportedList = asList(programSupported);

    ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO(
      programSupportedList.get(0).getFacilityCode(), programSupportedList);

    assertThat(programSupportedEventDTO.getFacilityCode(), is(programSupported.getFacilityCode()));
    ProgramSupportedEventDTO.ProgramSupportedDTO programSupportedDTO = programSupportedEventDTO.getProgramsSupported().get(0);
    assertThat(programSupportedDTO.getActive(), is(programSupported.getActive()));
    assertThat(programSupportedDTO.getName(), is(programSupported.getProgram().getName()));
    assertThat(programSupportedDTO.getCode(), is(programSupported.getProgram().getCode()));
    assertThat(programSupportedDTO.getStartDate(), is(programSupported.getStartDate()));

  }

}
