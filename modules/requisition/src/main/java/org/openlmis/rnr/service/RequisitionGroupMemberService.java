package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.repository.RequisitionGroupMemberRepository;
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
