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

import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.helper.CommaSeparator;
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

/**
 * @deprecated
 * This class is intended to return a variety of stock-related information through its getStockRequirements member. Because the calculations it uses are VIMS specific, the class may be considered depreciated.
 */
@Deprecated
@Service
public class StockRequirementsService
{
    @Autowired
    private FacilityService facilityService = null;

    @Autowired
    private FacilityRepository facilityRepository = null;

    @Autowired
    private FacilityApprovedProductRepository facilityApprovedProductRepository = null;

    @Autowired
    private FacilityProgramProductService facilityProgramProductService = null;

    @Autowired
    AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService = null;

    @Autowired
    AnnualDistrictDemographicEstimateService annualDistrictDemographicEstimateService = null;

    @Autowired
    private AnnualDistrictEstimateRepository annualDistrictEstimateRepository = null;

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
        if(facility == null)
            return null;

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

            //Set productId
            Long productId = facilityProgramProduct.getProduct().getId();
            requirements.setProductId(productId);
            requirements.setProductName(facilityProgramProduct.getProduct().getPrimaryName());

            //Set population
            Integer populationSource = (isa != null) ? isa.getPopulationSource() : null;
            requirements.setPopulation(getPopulation(facility, facilityProgramProduct.getProgram(), populationSource));

            //Set minStock, maxStock, and eop
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


    private Long getPopulation(Facility facility, Program program, Integer populationSource)
    {
        if(program == null)
            return getNonNullFacilityCatchmentPopulation(facility);

        //TODO: Currently, there isn't a robust way of determining whether a Facility is a SDP, DVS, RVS, or CVS. Doing string-comparisons on the Facility's name or code, as is done below, is a stopgap that should be removed in the future.

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String facilityCode = facility.getFacilityType().getCode().toLowerCase();
        if (facilityCode.equals("heac") || facilityCode.equals("disp")) // "heac" == Health Facility; "disp" == Dispensary
        {
            if(populationSource == null)
                return getNonNullFacilityCatchmentPopulation(facility);

            List<AnnualFacilityEstimateEntry> estimates = annualFacilityDemographicEstimateService.getEstimateValuesForFacility(facility.getId(), program.getId(), currentYear);
            for (AnnualFacilityEstimateEntry estimate : estimates)
            {
                if (estimate.getDemographicEstimateId() != null && estimate.getDemographicEstimateId().equals(new Long(populationSource)))
                {
                    if(estimate.getValue() != null)
                        return estimate.getValue(); //Note that if the user hasn't specified a value, annualFacilityDemographicEstimateService.getEstimateValuesForFacility() will have done its best to compute one which will, most likely, not equal facility.getCatchmentPopulation().
                    else
                        return getNonNullFacilityCatchmentPopulation(facility);
                }
            }
        }
        else if(facilityCode.equals("dvs"))
        {
            if(populationSource == null)
                return getNonNullFacilityCatchmentPopulation(facility);

            GeographicZone geoZone = facility.getGeographicZone();
            if(geoZone == null)
                return getNonNullFacilityCatchmentPopulation(facility);

            AnnualDistrictEstimateEntry estimateEntry = annualDistrictEstimateRepository.getEntryBy(currentYear, geoZone.getId(), program.getId(), new Long(populationSource));
            if(estimateEntry != null)
                return estimateEntry.getValue(); //Note that if the user hasn't specified a value, annualFacilityDemographicEstimateService.getEstimateValuesForFacility() will have done its best to compute one which will, most likely, not equal facility.getCatchmentPopulation().
            else
                return getNonNullFacilityCatchmentPopulation(facility);
        }
        else if(facilityCode.equals("rvs"))
        {
            //Get the supervisory node associated with our requisition group
            SupervisoryNode supNodeForReqGroup = supervisoryNodeRepository.getFor(facility, program);

            //Get the child supervisory nodes, each of which should be associated with a different requisition group.
            List<SupervisoryNode> childNodes = supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(supNodeForReqGroup);

            //Of the supervisory nodes we just found, determine which (if any) is associated with our facility
            SupervisoryNode childNode = getSupervisoryNodeAssociatedWithFacility(childNodes, facility);
            if(childNode == null)
                return getNonNullFacilityCatchmentPopulation(facility);

            //Get the requisition-groups associated with the child supervisory node we just found
            List<RequisitionGroup> requisitionGroups = requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(childNode));

            //Determine which of the requisition-groups is relevant
            /*
            RequisitionGroup requisitionGroup = getRequisitionGroupForFacilityAndProgram(requisitionGroups, facility.getId(), program.getId());
            if(requisitionGroup == null)
                return getNonNullFacilityCatchmentPopulation(facility);  */

            /*  Multiple requisition groups, regardless of their product type, may be associated with the same supervisory node. In other words,
            there's a 1:* relation.  Given a specific supervisory node and product, it's therefore not possible to return a single requisition
            group. For VIMS, it is therefore necessary to require a configuration wherein there is a 1:1 relationship between requisition groups
            and the supervisory-nodes they're associated with. Because VIMS is intended exclusively for use with the vaccine-program, this
            requirement is tenable.  */
            if(requisitionGroups == null || requisitionGroups.size() < 1)
                return getNonNullFacilityCatchmentPopulation(facility);
            RequisitionGroup requisitionGroup = requisitionGroups.get(0);

            //Get all the facilities associated with that requisition-group
            List<Facility> facilities = getFacilitiesInSpecifiedRequisitionGroup(requisitionGroup, program.getId());

            //Sum and return the above facilities' populations
            Long totalPopulation = 0L;
            for(Facility childFacility : facilities)
            {
                totalPopulation += getPopulation(childFacility, program, populationSource);
            }
            return totalPopulation;

        }
        else if(facilityCode.equals("cvs"))
        {
            List<Facility> facilities = facilityRepository.getAllByFacilityTypeCode("rvs");
            Long totalPopulation = 0L;
            for(Facility rvs : facilities)
            {
                totalPopulation += getPopulation(rvs, program, populationSource);
            }

            if(totalPopulation.intValue() > 0)
                return  totalPopulation;
            else
                return getNonNullFacilityCatchmentPopulation(facility);
        }
        else
        {
            return getNonNullFacilityCatchmentPopulation(facility);
        }

        return getNonNullFacilityCatchmentPopulation(facility);
    }

    private Long getNonNullFacilityCatchmentPopulation(Facility facility)
    {
        Long catchment = facility.getCatchmentPopulation();
        return (catchment != null) ? catchment : 0L;
    }

    private List<Facility> getFacilitiesInSpecifiedRequisitionGroup(RequisitionGroup group, Long programId)
    {
        List<Facility> facilities = new LinkedList<>();
        String formattedRequisitionGroupId = "{" +  group.getId().toString() + "}"; //Format the ID as an array with a single member
        List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, formattedRequisitionGroupId);
        for(FacilityLevelTree level : facilityLevels)
        {
            Facility facility = facilityService.getById(level.getFacilityId());
            facilities.add(facility);
        }
        return facilities;
    }

    private SupervisoryNode getSupervisoryNodeAssociatedWithFacility(List<SupervisoryNode> supervisoryNodes, Facility facility)
    {
        for(SupervisoryNode node : supervisoryNodes)
        {
            Facility relatedFacility = node.getFacility();
            if(relatedFacility != null && relatedFacility.getId().equals(facility.getId())) {
                return node;
            }
        }
        return null;
    }

    //Look through the specified requisitionGroups for one associated with the specified program and facility. Return the result, if found.
    private RequisitionGroup getRequisitionGroupForFacilityAndProgram(List<RequisitionGroup> requisitionGroups, Long facilityId, Long programId)
    {
        //For each of the requisition groups in the list...
        for(RequisitionGroup group : requisitionGroups)
        {
            //...get all of its associated facilties...
            String formattedRequisitionGroupId = "{" +  group.getId().toString() + "}"; //Format the ID as an array with a single member
            List<FacilityLevelTree> facilityLevels = levelMapper.getFacilitiesByLevel(programId, formattedRequisitionGroupId);

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
