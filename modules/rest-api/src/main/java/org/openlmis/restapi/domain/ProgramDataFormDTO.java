package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDataFormDTO {

  private Long facilityId;
  private String programCode;
  @JsonDeserialize(using = DateDeserializer.class)
  private Date periodBegin;
  @JsonDeserialize(using = DateDeserializer.class)
  private Date periodEnd;
  private Date submittedTime;
  private List<ProgramDataFormItemDTO> programDataFormItems;

  public String getSyncUpHash() {
    String programDataString = this.programCode + this.periodBegin.toString() + this.periodEnd.toString() +
        this.submittedTime.toString() + this.facilityId.toString();
    return Encoder.hash(programDataString);
  }
}