package org.openlmis.restapi.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.moz.ProgramDataItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDataFormItemDTO {
  private String name;
  private String columnCode;
  private Long value;

  public static ProgramDataFormItemDTO prepareForRest(ProgramDataItem programDataItem) {
    return new ProgramDataFormItemDTO(programDataItem.getName(), programDataItem.getProgramDataColumn().getCode(), programDataItem.getValue());
  }
}
