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

import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.StockRequirements;
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;
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
    private FacilityApprovedProductRepository facilityApprovedProductRepository = null;

    @Autowired
    private FacilityProgramProductService facilityProgramProductService = null;

    @Autowired
    private PopulationService populationService = null;

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
            Long populationSource = (isa != null) ? isa.getPopulationSource() : null;
            requirements.setPopulation(populationService.getPopulation(facility, facilityProgramProduct.getProgram(), populationSource));

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
}
