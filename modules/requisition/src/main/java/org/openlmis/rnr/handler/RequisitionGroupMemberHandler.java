package org.openlmis.rnr.handler;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.service.RequisitionGroupMemberService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
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
    protected void save(Importable modelClass, String modifiedBy) {
        RequisitionGroupMember requisitionGroupMember = (RequisitionGroupMember) modelClass;
        requisitionGroupMember.setModifiedBy(modifiedBy);
        requisitionGroupMember.setModifiedDate(new Date());

        requisitionGroupMemberService.save(requisitionGroupMember);
    }
}
