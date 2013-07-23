package org.openlmis.core.dto;

import org.junit.Test;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.ProgramSupported;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

public class ProgramSupportedEventDTOTest {
  @Test
  public void shouldCreateProgramSupportedEventDTO() throws Exception {

    List<ProgramSupported> programSupportedList = new ArrayList<>();
    ProgramSupported programSupported = make(a(defaultProgramSupported));
    programSupportedList.add(programSupported);

    ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO(
      programSupportedList.get(0).getFacilityCode(), programSupportedList);

    assertThat(programSupportedEventDTO.getFacilityCode(), is(programSupported.getFacilityCode()));
    assertThat(programSupportedEventDTO.getProgramSupportedList(), hasItem(programSupported));
  }
}
