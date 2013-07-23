package org.openlmis.core.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.event.ProgramSupportedEvent;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.core.service.event.ProgramSupportedEventService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(ProgramSupportedEventService.class)
public class ProgramSupportedEventServiceTest {

  @InjectMocks
  ProgramSupportedEventService service;

  @Mock
  EventService eventService;

  @Mock
  private ProgramSupportedRepository programSupportedRepository;


  @Test
  public void shouldNotifyEventWithProgramSupportedEvent() throws Exception {

    ProgramSupported programSupported = new ProgramSupported();
    List<ProgramSupported> programSupportedList = new ArrayList<>();
    programSupportedList.add(programSupported);
    ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO(
      programSupportedList.get(0).getFacilityCode(),programSupportedList);
    ProgramSupportedEvent programSupportedEvent = mock(ProgramSupportedEvent.class);
    whenNew(ProgramSupportedEvent.class).withArguments(programSupportedEventDTO).thenReturn(programSupportedEvent);

    service.notify(asList(programSupported));

    verify(eventService).notify(programSupportedEvent);
  }

}
