package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionStatusChange {
  private Integer id;

  private Integer rnrId;
  private RnrStatus status;
  private Integer statusChangedBy;
  private Date statusChangeDate;

  public RequisitionStatusChange(Rnr requisition) {
    this.rnrId = requisition.getId();
    this.status = requisition.getStatus();
    this.statusChangedBy = requisition.getModifiedBy();
  }
}
