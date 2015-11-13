/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.demographics.service;

import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.demographics.domain.*;
import org.openlmis.demographics.repository.*;

import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.core.service.FacilityService;

import org.openlmis.report.mapper.lookup.FacilityLevelMapper;
import org.openlmis.report.model.dto.FacilityLevelTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StockRequirementsService
{
    @Autowired
    private FacilityService facilityService = null;

    @Autowired
    private FacilityApprovedProductRepository facilityApprovedProductRepository= null;

    @Autowired
    private FacilityProgramProductService facilityProgramProductService= null;

    @Autowired
    AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService= null;

    @Autowired
    AnnualDistrictDemographicEstimateService annualDistrictDemographicEstimateService= null;

    @Autowired
    private AnnualDistrictEstimateRepository annualDistrictEstimateRepository= null;

    @Autowired
    SupervisoryNodeRepository supervisoryNodeRepository;

    @Autowired
    RequisitionGroupRepository requisitionGroupRepository;

    @Autowired
    private FacilityLevelMapper levelMapper;


    public List<StockRequirements> getStockRequirements(final Long facilityId, Long programId)
    {
        //Get facility in order to access its catchment population
        Facility facility = facilityService.getById(facilityId);

        List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = facilityApprovedProductRepository.getAllByFacilityAndProgramId(facilityId, programId);

        List<StockRequirements> stockRequirements = new ArrayList<>();

        List<FacilityProgramProduct> programProductsByProgram = facilityProgramProductService.getActiveProductsForProgramAndFacility(programId, facilityId);
        for (FacilityProgramProduct facilityProgramProduct : programProductsByProgram)
        {
            StockRequirements requirements = new StockRequirements();

            //Set facility info
            requirements.setFacilityId(facilityId);
            requirements.setFacilityCode(facility.getFacilityType().getCode());

            //Set our ISA to the most specific one possible
            ISA isa = facilityProgramProduct.getOverriddenIsa();
            if(isa == null) {
                if(facilityProgramProduct.getProgramProductIsa() != null) {
                    isa = facilityProgramProduct.getProgramProductIsa().getIsa();
                }
            }

            requirements.setIsa(isa);

            //set productId
            Long productId = facilityProgramProduct.getProduct().getId();
            requirements.setProductId(productId);
            requirements.setProductName(facilityProgramProduct.getProduct().getPrimaryName());

            //set population
            Integer populationSource = (isa != null) ? isa.getPopulationSource() : null;
            requirements.setPopulation(getPopulation(facility, /*programId*/ facilityProgramProduct.getProgram() , /*isa*/ populationSource));

            //set minStock, maxStock, and eop
            for(FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts)
            {
                if(productId.equals(facilityTypeApprovedProduct.getProgramProduct().getProduct().getId()))
                {
                    ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
                    ProductCategory category = programProduct.getProductCategory();
                    requirements.setProductCategory(category.getName());
                    requirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
                    requirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
                    requirements.setEop(facilityTypeApprovedProduct.getEop());
                    break;
                }
            }
            stockRequirements.add(requirements);
        }

        return stockRequirements;
    }


    Long getPopulation(Facility facility, Program program, Integer populationSource)
    {
        if(program == null)
            return facility.getCatchmentPopulation();

        //TODO: Currently, there isn't a robust way of determining whether a Facility is a SDP, DVS, RVS, or CVS. Doing string-comparisons on the Facility's name or code, as is done below, is a stopgap that should be removed in the future.

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String facilityCode = facility.getFacilityType().getCode().toLowerCase();
        if (facilityCode.equals("heac") || facilityCode.equals("disp")) //Health Facility ("heac") or Dispensary ("disp")
        {
            if(populationSource == null)
                return facility.getCatchmentPopulation();

            List<AnnualFacilityEstimateEntry> estimates = annualFacilityDemographicEstimateService.getEstimateValuesForFacility(facility.getId(), program.getId(), currentYear);
            for (AnnualFacilityEstimateEntry estimate : estimates)
            {
                if (estimate.getDemographicEstimateId() != null && estimate.getDemographicEstimateId().equals(new Long(populationSource)))
                {
                    if(estimate.getValue() != null)
                        return estimate.getValue();
                    else
                        return facility.getCatchmentPopulation();
                }
            }
        }
        else if(facilityCode.equals("dvs"))
        {
            if(populationSource == null)
                return facility.getCatchmentPopulation();

            GeographicZone geoZone = facility.getGeographicZone();
            if(geoZone == null)
                return facility.getCatchmentPopulation();

            AnnualDistrictEstimateEntry estimateEntry = annualDistrictEstimateRepository.getEntryBy(currentYear, geoZone.getId(), program.getId(), new Long(populationSource));
            if(estimateEntry != null)
                return estimateEntry.getValue();
            else
                return facility.getCatchmentPopulation();
        }
        else if(facilityCode.equals("rvs"))
        {
            //Get the supervisory node associated with our requisition group
            SupervisoryNode supNodeForReqGroup = supervisoryNodeRepository.getFor(facility, program);

            //Get the child supervisory nodes, each of which should be associated with a different requisition group.
            List<SupervisoryNode> childNodes = supervisoryNodeRepository.getSupervisoryNodeChildren(supNodeForReqGroup.getId());

            //Find the child node we care about
            SupervisoryNode childNode = null;
            for(SupervisoryNode node : childNodes)
            {
                if(node.getFacility().getId().equals(facility.getId()))
                {
                    childNode = node;
                    break;
                }
            }

            if(childNode == null)
                return facility.getCatchmentPopulation();

            //Get the requisition-groups associated with the child supervosory node we just found
            List<RequisitionGroup> requisitionGroups = requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(childNode));

            //Determine which of the requisition-groups is relevant
            RequisitionGroup requisitionGroup = getRequisitionGroupForFacilityAndProgram(requisitionGroups, facility.getId(), program.getId());
            if(requisitionGroup == null)
                return facility.getCatchmentPopulation();

            //Get all the facilities associated with that requisition-group
            List<Facility> facilities = getFacilitiesInSpecifiedRequisitionGroup(requisitionGroup, program.getId());

            //Sum and return the above facility's populations
            Long childPopulation = 0L;
            for(Facility childFacility : facilities)
            {
                childPopulation += getPopulation(childFacility, program, populationSource);
            }
            return childPopulation;

        }
        else if(facilityCode.equals("cvs"))
        {
            //TODO: Get all RVS nodes in the system and sum their populations
            return facility.getCatchmentPopulation();
        }
        else
        {
            return facility.getCatchmentPopulation();
        }

        return facility.getCatchmentPopulation();
    }

    private List<Facility> getFacilitiesInSpecifiedRequisitionGroup(RequisitionGroup group, Long programId)
    {
        List<Facility> facilities = new LinkedList<>();
        List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, group.getId().toString());
        for(FacilityLevelTree level : facilityLevels)
        {
            Facility facility = facilityService.getById(level.getFacilityId());
            facilities.add(facility);
        }
        return facilities;
    }

    //Look through the specified requisitionGroups for one associated with the specified program and facility. Return the result, if found.
    private RequisitionGroup getRequisitionGroupForFacilityAndProgram(List<RequisitionGroup> requisitionGroups, Long facilityId, Long programId)
    {
        //For each of the requisition groups in the list...
        for(RequisitionGroup group : requisitionGroups)
        {
            //...get all of its associated facilties...
            List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, group.getId().toString());

            //...then, check whether our facility is amongst them. If it is, we've found the requisition group we care about
            if (facilityLevelTreeListContainsFacility(facilityLevels, facilityId) )
            {
                return group;
            }
        }
        return null;
    }

    private boolean facilityLevelTreeListContainsFacility(List<FacilityLevelTree> list, Long facilityId)
    {
        for(FacilityLevelTree level : list)
        {
            if(level.getFacilityId().equals(facilityId)) {
                return true;
            }
        }
        return false;
    }
}
