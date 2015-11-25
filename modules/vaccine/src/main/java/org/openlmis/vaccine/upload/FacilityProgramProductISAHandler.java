package org.openlmis.vaccine.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.vaccine.dto.FacilityProgramProductISADTO;
import org.openlmis.core.repository.*;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.repository.EstimateCategoryRepository;
import org.openlmis.demographics.service.PopulationService;
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

    @Autowired
    PopulationService populationService;

    @Autowired
    EstimateCategoryRepository estimateCategoryRepository;

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
        EstimateCategory category = estimateCategoryRepository.getByName(fppISA.getPopulationSourceName());
        Long populationSourceId = (category != null) ? category.getId() : null;

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
        isa.setPopulationSource(populationSourceId);

        saveFacilityProgramProductWithISA(facility, pp, isa);

        // This method only really applies for VIMS, but is in place for all
        saveWastageFactorForSupervisingFacilities(facility, pp, program, populationSourceId);
    }

    @Override
    public String getMessageKey() {
        return "error.duplicate.facility.program.product.isa";
    }

    private void saveWastageFactorForSupervisingFacilities(Facility facility, ProgramProduct pp, Program program,
                                                           Long populationSourceId) {

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
                int totalPopulation = 0;
                Double totalWeightedWastageFactor = 0.0;

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
                    // For VIMS, SDPs are defined as having facility type code "heac" (health facility) or "disp" (dispensary)
                    Facility childFacility = facilityRepository.getById(requisitionGroupMember.getFacility().getId());
                    if (childFacility.getFacilityType().getCode().equalsIgnoreCase("heac") ||
                            childFacility.getFacilityType().getCode().equalsIgnoreCase("disp")) {
                        ISA facilityISA = repository.getOverriddenIsa(pp.getId(), childFacility.getId());
                        if (facilityISA != null) {
                            Long population = populationService.getPopulation(childFacility, program, populationSourceId);
                            totalPopulation += population;
                            totalWeightedWastageFactor += population * facilityISA.getWastageFactor();
                        }
                    }
                }

                // Calculate average wastage factor
                if (totalPopulation > 0) {
                    wastageFactor = totalWeightedWastageFactor / totalPopulation;
                }

                ISA isa = new ISA();
                isa.setWhoRatio(0.0);
                isa.setDosesPerYear(0);
                isa.setWastageFactor(wastageFactor);
                isa.setBufferPercentage(0.0);
                isa.setAdjustmentValue(0);
                isa.setPopulationSource(populationSourceId);

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
