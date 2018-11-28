package org.openlmis.core.domain.moz;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Signature;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ProgramDataForm extends BaseModel {

  private Facility facility;

  private SupplementalProgram supplementalProgram;

  private Date startDate;

  private Date endDate;

  private Date submittedTime;

  private List<ProgramDataItem> programDataItems;

  private List<ProgramDataFormBasicItem> programDataFormBasicItems;

  private List<Signature> programDataFormSignatures;

  private String observation;

  public ProgramDataForm(Facility facility, SupplementalProgram supplementalProgram, Date startDate, Date endDate, Date submittedTime, String observation) {
    this.facility = facility;
    this.supplementalProgram = supplementalProgram;
    this.startDate = startDate;
    this.endDate = endDate;
    this.submittedTime = submittedTime;
    this.observation = observation;
  }
}
