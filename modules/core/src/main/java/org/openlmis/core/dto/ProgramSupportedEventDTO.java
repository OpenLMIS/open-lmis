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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.domain.ProgramSupported;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * ProgramSupportedEventDTO consolidates information about programs supported by a facility
 * like facilityCode, and list of programs to be used while displaying ProgramSupported
 * information to user, for eg. in feed.
 */
@Getter
@Setter
@JsonSerialize(include = NON_EMPTY)
public class ProgramSupportedEventDTO extends BaseFeedDTO {

  private String facilityCode;
  private List<ProgramSupportedDTO> programsSupported = new ArrayList<>();

  public static final String CATEGORY = "programs-supported";
  public static final String TITLE = "Programs Supported";


  public ProgramSupportedEventDTO(String facilityCode, List<ProgramSupported> programSupportedList) {
    this.facilityCode = facilityCode;

    for (ProgramSupported ps : programSupportedList) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      String stringStartDate = (ps.getStartDate() == null ? null : dateFormat.format(ps.getStartDate()));
      ProgramSupportedDTO psDTO = new ProgramSupportedDTO(ps.getProgram().getCode(), ps.getProgram().getName(), ps.getActive(), ps.getStartDate(), stringStartDate);

      programsSupported.add(psDTO);
    }
  }

  /**
   * ProgramSupportedDTO consolidates information about program like code, name, active etc. which is more user readable.
   */
  @AllArgsConstructor
  @Data
  @JsonSerialize(include = NON_EMPTY)
  class ProgramSupportedDTO {
    private String code;
    private String name;
    private Boolean active;
    private Date startDate;
    private String stringStartDate;
  }

  public Event createEvent() throws URISyntaxException {
    return new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), "", getSerializedContents(), CATEGORY);
  }

}
