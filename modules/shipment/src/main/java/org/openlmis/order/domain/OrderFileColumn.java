package org.openlmis.order.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
public class OrderFileColumn extends BaseModel{

  private String dataFieldLabel;
  private Boolean openLmisField;
  private int position;
  private String columnLabel;
  private String format;
  private String keyPath;
  private String nested;
  private Boolean includeInOrderFile;

}
