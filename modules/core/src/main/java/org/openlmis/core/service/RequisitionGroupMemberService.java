/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
