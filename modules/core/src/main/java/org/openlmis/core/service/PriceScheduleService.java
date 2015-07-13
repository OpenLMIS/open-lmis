/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.PriceScheduleCategory;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.PriceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@NoArgsConstructor
public class PriceScheduleService {

    @Autowired
    private PriceScheduleRepository repository;

    public void save(PriceSchedule priceSchedule) {
        if(priceSchedule.getId() == null)
          repository.insert(priceSchedule);

        else
            repository.update(priceSchedule);
    }

    public BaseModel getByProductCodePriceScheduleCategory(PriceSchedule priceSchedule) {
        return repository.getByProductCodePriceScheduleCategory(priceSchedule);
    }

    public List<PriceSchedule> getByProductId(Long id) {
        return repository.getByProductId(id);
    }

    public PriceScheduleCategory getPriceScheduleCategoryByCode(String code){
        return repository.getPriceScheduleCategoryByCode(code);
    }

    @Transactional
    public void saveAll(List<PriceSchedule> priceSchedules, Product product) {

        for(PriceSchedule priceSchedule : priceSchedules){

            if(priceSchedule.getId() == null)
            priceSchedule.setProduct(product);
            priceSchedule.setModifiedBy(product.getModifiedBy());
            priceSchedule.setCreatedBy(product.getModifiedBy());
            priceSchedule.setModifiedDate(product.getModifiedDate());
            save(priceSchedule);
        }
    }

    public List<PriceScheduleCategory> getPriceScheduleCategories() {

        return repository.getPriceScheduleCategories();
    }

   public List<PriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(Long programId, Long facilityId){
       return repository.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);
   }
}
