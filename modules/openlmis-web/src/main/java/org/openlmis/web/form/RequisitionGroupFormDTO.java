package org.openlmis.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RequisitionGroupFormDTO {

  private RequisitionGroup requisitionGroup;
  private List<RequisitionGroupMember> requisitionGroupMembers = new ArrayList<>();
  private List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = new ArrayList<>();

}
