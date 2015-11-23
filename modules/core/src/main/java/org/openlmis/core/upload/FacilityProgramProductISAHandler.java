package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityProgramProductISADTO;
import org.openlmis.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class FacilityProgramProductISAHandler extends AbstractModelPersistenceHandler {

    @Autowired
    FacilityProgramProductRepository repository;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    ProgramProductRepository programProductRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    SupervisoryNodeRepository supervisoryNodeRepository;

    @Autowired
    RequisitionGroupRepository requisitionGroupRepository;

    @Autowired
    RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Override
    protected BaseModel getExisting(BaseModel record) {
        FacilityProgramProductISADTO fppISA = (FacilityProgramProductISADTO)record;
        return repository.getByCodes(fppISA.getFacility().getCode(), fppISA.getProgram().getCode(), fppISA.getProduct().getCode());
    }

    @Override
    protected void save(BaseModel record) {
        FacilityProgramProductISADTO fppISA = (FacilityProgramProductISADTO)record;

        Facility facility = facilityRepository.getByCode(fppISA.getFacility().getCode());
        ProgramProduct pp = programProductRepository.getByProgramAndProductCode(new ProgramProduct(fppISA.getProgram(),
                fppISA.getProduct(), null, null));
        Program program = programRepository.getByCode(fppISA.getProgram().getCode());

        Double wastageFactor = fppISA.getWastageFactor();
        if (wastageFactor == -1.0) {
            wastageFactor = repository.getOverriddenIsa(pp.getId(), facility.getId()).getWastageFactor();
        }

        ISA isa = new ISA();
        isa.setWhoRatio(fppISA.getWhoRatio());
        isa.setDosesPerYear(fppISA.getDosesPerYear());
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(fppISA.getBufferPercentage());
        isa.setMinimumValue(fppISA.getMinimumValue());
        isa.setMaximumValue(fppISA.getMaximumValue());
        isa.setAdjustmentValue(fppISA.getAdjustmentValue());

        saveFacilityProgramProductWithISA(facility, pp, isa);

        // This method only really applies for VIMS, but is in place for all
        saveWastageFactorForSupervisingFacilities(facility, pp, program);
    }

    @Override
    public String getMessageKey() {
        return "error.duplicate.facility.program.product.isa";
    }

    private void saveWastageFactorForSupervisingFacilities(Facility facility, ProgramProduct pp, Program program) {

        // Get all parent facilities of this facility
        SupervisoryNode node = supervisoryNodeRepository.getFor(facility, program);
        List<SupervisoryNode> parentNodes = supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(node);
        for (SupervisoryNode parentNode : parentNodes) {
            Double wastageFactor = 0.0;

            // Re-calculate wastage factor if parent facility is a DVS/RVS/CVS (VIMS-specific)
            Facility parentFacility = parentNode.getFacility();
            if (parentFacility.getFacilityType().getCode().equalsIgnoreCase("cvs") ||
                    parentFacility.getFacilityType().getCode().equalsIgnoreCase("rvs") ||
                    parentFacility.getFacilityType().getCode().equalsIgnoreCase("dvs")) {
                int facilityCount = 0;
                Double totalWastageFactor = 0.0;

                // Do the calculation by looking at all children

                // First, get all child facilities by looking at all requisition groups and their members
                // NOTE: assumption here is that a supervisory node only has one requisition group assigned to it, which is true for VIMS
                List<SupervisoryNode> childNodes = supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(parentNode);
                List<RequisitionGroup> requisitionGroups = requisitionGroupRepository.getRequisitionGroups(childNodes);
                List<RequisitionGroupMember> requisitionGroupMembers = new ArrayList<>();
                for (RequisitionGroup requisitionGroup : requisitionGroups) {
                    requisitionGroupMembers.addAll(requisitionGroupMemberRepository.getMembersBy(requisitionGroup.getId()));
                }

                // For each requisition group member, check if facility is an SDP and add its wastage factor to running total
                for (RequisitionGroupMember requisitionGroupMember : requisitionGroupMembers) {
                    // This call to the database is necessary, as facility does not have SDP info
                    Facility childFacility = facilityRepository.getById(requisitionGroupMember.getFacility().getId());
                    if (childFacility.getSdp()) {
                        ISA facilityISA = repository.getOverriddenIsa(pp.getId(), childFacility.getId());
                        if (facilityISA != null) {
                            facilityCount += 1;
                            totalWastageFactor += facilityISA.getWastageFactor();
                        }
                    }
                }

                // Calculate average wastage factor
                if (facilityCount > 0) {
                    wastageFactor = totalWastageFactor / facilityCount;
                }

                ISA isa = new ISA();
                isa.setWhoRatio(0.0);
                isa.setDosesPerYear(0);
                isa.setWastageFactor(wastageFactor);
                isa.setBufferPercentage(0.0);
                isa.setAdjustmentValue(0);

                saveFacilityProgramProductWithISA(parentFacility, pp, isa);
            }
        }
    }

    private void saveFacilityProgramProductWithISA(Facility facility, ProgramProduct pp, ISA isa) {
        ProgramProductISA ppISA = new ProgramProductISA(pp.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(facility.getId());
        fpp.setId(pp.getId());
        fpp.setProgramProductIsa(ppISA);
        fpp.setOverriddenIsa(isa); // Not necessary, but done to get test case shouldSaveFacilityProgramProductISAAndCalculateParents() to work

        repository.save(fpp);
    }
}
