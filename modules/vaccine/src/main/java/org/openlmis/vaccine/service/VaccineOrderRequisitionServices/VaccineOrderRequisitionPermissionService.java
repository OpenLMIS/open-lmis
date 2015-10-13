package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;


import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Iterables.any;
import static org.openlmis.core.utils.RightUtil.with;

@Service
public class VaccineOrderRequisitionPermissionService {

    @Autowired
    private RoleRightsService roleRightsService;
    @Autowired
    private RoleAssignmentService roleAssignmentService;
    @Autowired
    ProgramSupportedService programSupportedService;

    public Boolean hasPermission(Long userId, Facility facility, Program program, String rightName) {
        ProgramSupported programSupported = programSupportedService.getByFacilityIdAndProgramId(facility.getId(), program.getId());
        if (!(programSupported != null && programSupported.getActive() && programSupported.getProgram().getActive())) {
            return false;
        }

        List<Right> userRights = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);
        return any(userRights, with(rightName));
    }

    public Boolean hasPermission(Long userId, VaccineOrderRequisition orderRequisition, String rightName) {

        return hasPermission(userId, orderRequisition.getFacility(), orderRequisition.getProgram(), rightName);
    }


}
