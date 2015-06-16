package org.openlmis.vaccine.domain.demographics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DemographicEstimateCategory extends BaseModel {

  private String name;

  private String description;

  private Boolean isPrimaryEstimate;

  private Double defaultConversionFactor;

}
