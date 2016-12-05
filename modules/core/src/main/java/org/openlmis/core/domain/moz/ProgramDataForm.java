package org.openlmis.core.domain.moz;

import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ProgramDataForm extends BaseModel {
  @Getter
  @Setter
  private Facility facility;
  @Getter
  @Setter
  private SupplementalProgram supplementalProgram;
  @Getter
  @Setter
  private Date startDate;
  @Getter
  @Setter
  private Date endDate;
  @Getter
  @Setter
  private Date submittedTime;
  @Getter
  @Setter
  private List<ProgramDataItem> dataItemList;
}
