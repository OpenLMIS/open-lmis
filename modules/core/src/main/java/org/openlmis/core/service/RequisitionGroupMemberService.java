package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.repository.RequisitionGroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class RequisitionGroupMemberService {
    private RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Autowired
    public RequisitionGroupMemberService(RequisitionGroupMemberRepository requisitionGroupMemberRepository) {
        this.requisitionGroupMemberRepository = requisitionGroupMemberRepository;
    }

    public void save(RequisitionGroupMember requisitionGroupMember) {
        requisitionGroupMemberRepository.insert(requisitionGroupMember);
    }
}
