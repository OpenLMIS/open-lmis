package org.openlmis.core.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.ProgramProduct;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@EqualsAndHashCode
@JsonSerialize(include = NON_EMPTY)
public class ProgramProductDTO {

  private String programCode;
  private String programName;
  private String productCode;
  private String productName;
  private String description;
  private Integer unit;
  private String category;

  public ProgramProductDTO(ProgramProduct programProduct) {
    this.programCode = programProduct.getProgram().getCode();
    this.programName = programProduct.getProgram().getName();
    this.productCode = programProduct.getProduct().getCode();
    this.productName = programProduct.getProduct().getPrimaryName();
    this.description = programProduct.getProduct().getDescription();
    this.unit = programProduct.getProduct().getDosesPerDispensingUnit();
    this.category = programProduct.getProduct().getCategory().getName();
  }

}
