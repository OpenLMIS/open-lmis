package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@NoArgsConstructor
@Component("requisitionGroupMemberHandler")
public class RequisitionGroupMemberHandler extends AbstractModelPersistenceHandler {

    private RequisitionGroupMemberService requisitionGroupMemberService;

    @Autowired
    public RequisitionGroupMemberHandler(RequisitionGroupMemberService requisitionGroupMemberService) {

        this.requisitionGroupMemberService = requisitionGroupMemberService;
    }

    @Override
    protected void save(Importable modelClass, AuditFields auditFields) {
        RequisitionGroupMember requisitionGroupMember = (RequisitionGroupMember) modelClass;
        requisitionGroupMember.setModifiedBy(auditFields.getUser());
        requisitionGroupMember.setModifiedDate(new Date());

        requisitionGroupMemberService.save(requisitionGroupMember);
    }
}
