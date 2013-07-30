package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.ProgramSupported;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class ProgramSupportedEventDTO extends BaseFeedDTO {

  private String facilityCode;
  private List<ProgramSupportedDTO> programsSupported;

  public ProgramSupportedEventDTO(String facilityCode, List<ProgramSupported> programSupportedList) {
    this.facilityCode = facilityCode;

    this.programsSupported = new ArrayList<>();
    for (ProgramSupported ps : programSupportedList) {
      ProgramSupportedDTO psDTO = new ProgramSupportedDTO(ps.getProgram().getCode(), ps.getProgram().getName(),
        ps.getActive(), ps.getStartDate());

      programsSupported.add(psDTO);
    }
  }

  @AllArgsConstructor
  @Data
  @JsonSerialize(include = NON_EMPTY)
  class ProgramSupportedDTO {
    private String code;
    private String name;
    private Boolean active;
    private Date startDate;
  }

}
