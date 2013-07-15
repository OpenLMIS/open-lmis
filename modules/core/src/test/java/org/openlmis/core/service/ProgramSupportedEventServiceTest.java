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
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
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
    ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO();
    ProgramSupportedEvent programSupportedEvent = mock(ProgramSupportedEvent.class);
    when(programSupportedRepository.getProgramSupportedEventDTO(programSupported)).thenReturn(programSupportedEventDTO);
    whenNew(ProgramSupportedEvent.class).withArguments(programSupportedEventDTO).thenReturn(programSupportedEvent);

    service.notify(programSupported);

    verify(eventService).notify(programSupportedEvent);
  }

  @Test
  public void shouldGetProgramSupportedEventDTO() throws Exception {

    ProgramSupported programSupported = new ProgramSupported();
    ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO();
    ProgramSupportedEvent programSupportedEvent = mock(ProgramSupportedEvent.class);

    when(programSupportedRepository.getProgramSupportedEventDTO(programSupported)).thenReturn(programSupportedEventDTO);
    whenNew(ProgramSupportedEvent.class).withArguments(programSupportedEventDTO).thenReturn(programSupportedEvent);

    service.notify(programSupported);

    verify(programSupportedRepository).getProgramSupportedEventDTO(programSupported);

  }
}
