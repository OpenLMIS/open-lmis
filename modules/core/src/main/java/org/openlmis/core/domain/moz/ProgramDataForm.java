package org.openlmis.core.domain.moz;

import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProgramDataForm extends BaseModel {

  private Facility facility;

  private SupplementalProgram supplementalProgram;

  private Date startDate;

  private Date endDate;

  private Date submittedTime;

  private List<ProgramDataItem> programDataItems;
}
