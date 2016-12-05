package org.openlmis.restapi.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDataFormItemDTO {
  private String name;
  private String columnCode;
  private Long value;
}
