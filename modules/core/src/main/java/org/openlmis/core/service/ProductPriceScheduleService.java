/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.repository.ProductPriceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@NoArgsConstructor
public class ProductPriceScheduleService {

  @Autowired
  private ProductPriceScheduleRepository repository;

  public void save(ProductPriceSchedule productPriceSchedule) {
    if (productPriceSchedule.getId() == null) {
      repository.insert(productPriceSchedule);
    } else {
      repository.update(productPriceSchedule);
    }
  }

  public BaseModel getByProductCodePriceScheduleCategory(ProductPriceSchedule productPriceSchedule) {
    return repository.getByProductCodeAndPriceSchedule(productPriceSchedule);
  }

  public List<ProductPriceSchedule> getByProductId(Long id) {
    return repository.getByProductId(id);
  }

  @Transactional
  public void saveAll(List<ProductPriceSchedule> productPriceSchedules, Product product) {

    for (ProductPriceSchedule productPriceSchedule : productPriceSchedules) {
      if (productPriceSchedule.getId() == null) {
        productPriceSchedule.setProduct(product);
      }
      productPriceSchedule.setModifiedBy(product.getModifiedBy());
      productPriceSchedule.setCreatedBy(product.getModifiedBy());
      productPriceSchedule.setModifiedDate(product.getModifiedDate());
      save(productPriceSchedule);
    }
  }

  public List<ProductPriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(Long programId, Long facilityId) {
    return repository.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);
  }
}
