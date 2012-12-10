package org.openlmis.rnr.repository.mapper;

import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionGroupMemberMapper {

    void insert(RequisitionGroupMember requisitionGroupMember);
}
