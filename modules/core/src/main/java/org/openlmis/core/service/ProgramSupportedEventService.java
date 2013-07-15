package org.openlmis.core.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.event.ProgramSupportedEvent;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
public class ProgramSupportedEventService {

  @Autowired
  private EventService eventService;

  @Autowired
  private ProgramSupportedRepository programSupportedRepository;

  public void notify(ProgramSupported programSupported) {

    try {
      ProgramSupportedEventDTO programSupportedEventDTO = programSupportedRepository.getProgramSupportedEventDTO(programSupported);
      eventService.notify(new ProgramSupportedEvent(programSupportedEventDTO));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

}
