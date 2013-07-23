package org.openlmis.core.event;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(DateTime.class)
@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
public class ProgramSupportedEventTest {

  @Mock
  ProgramSupportedEventDTO programSupportedEventDTO;

  @Test
  public void shouldCreateProgramSupportedEvent() throws Exception {

    mockStatic(DateTime.class);
    DateTime dateTime = new DateTime();
    when(programSupportedEventDTO.getSerializedContents()).thenReturn("serializedContents");
    when(DateTime.now()).thenReturn(dateTime);

    ProgramSupportedEvent programSupportedEvent = new ProgramSupportedEvent(programSupportedEventDTO);

    assertThat(programSupportedEvent.getTitle(), is("ProgramSupported"));
    assertNotNull(programSupportedEvent.getUuid());
    assertThat(programSupportedEvent.getCategory(), is("programSupported"));
    assertThat(programSupportedEvent.getTimeStamp(),is(dateTime));
    assertThat(programSupportedEvent.getContents(),is("serializedContents"));
    verify(programSupportedEventDTO).getSerializedContents();
  }
}
