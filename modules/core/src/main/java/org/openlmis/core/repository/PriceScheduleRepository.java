/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.PriceScheduleCategory;
import org.openlmis.core.repository.mapper.PriceScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class PriceScheduleRepository {

    @Autowired
    private PriceScheduleMapper mapper;

    @Autowired
    private ProductRepository productRepository;

    public void insert(PriceSchedule priceSchedule) {
        mapper.insert(priceSchedule);
    }

    public void update(PriceSchedule priceSchedule) {  mapper.update(priceSchedule); }

    public BaseModel getByProductCodePriceScheduleCategory(PriceSchedule priceSchedule) {

       Long productId = productRepository.getByCode(priceSchedule.getProduct().getCode()).getId();

        //TODO: price category needs to be moved to appropriate class
        Long priceCategoryId = mapper.getPriceCategoryIdByName(priceSchedule.getPriceScheduleCategory().getPrice_category());

        priceSchedule.getPriceScheduleCategory().setId(priceCategoryId);
        priceSchedule.getProduct().setId(productId);

        return mapper.getByProductCodePriceScheduleCategory(priceSchedule.getProduct().getId(), priceCategoryId);
    }

    public List<PriceSchedule> getByProductId(Long id) {
        return mapper.getByProductId(id);
    }


    public List<PriceScheduleCategory> getPriceScheduleCategories() {
        return mapper.getPriceScheduleCategories();
    }

    public PriceScheduleCategory getPriceScheduleCategoryByCode(String code) {
        return mapper.getPriceScheduleCategoryByCode(code);
    }

    public List<PriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(Long programId, Long facilityId) {
        return mapper.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);
    }
}
