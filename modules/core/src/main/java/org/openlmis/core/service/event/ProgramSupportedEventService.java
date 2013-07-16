package org.openlmis.core.service.event;

import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.event.ProgramSupportedEvent;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
public class ProgramSupportedEventService {

  @Autowired
  private EventService eventService;

  @Autowired
  private ProgramSupportedRepository programSupportedRepository;

  public void notify(List<ProgramSupported> programSupportedList) {

    try {
      ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO(
        programSupportedRepository.getAllByFacilityId(programSupportedList.get(0).getFacilityId()));
      eventService.notify(new ProgramSupportedEvent(programSupportedEventDTO));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

}
