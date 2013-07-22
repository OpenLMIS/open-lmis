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
