/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.repository.mapper.PriceScheduleMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProductPriceScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class ProductPriceScheduleRepository {

  @Autowired
  private ProductPriceScheduleMapper mapper;

  @Autowired
  private PriceScheduleMapper priceScheduleMapper;

  @Autowired
  private ProductMapper productMapper;

  public void insert(ProductPriceSchedule productPriceSchedule) {
    mapper.insert(productPriceSchedule);
  }

  public void update(ProductPriceSchedule productPriceSchedule) {
    mapper.update(productPriceSchedule);
  }

  //TODO: QUESTION? does this belong here? should this not move to a service?
  public ProductPriceSchedule getByProductCodeAndPriceSchedule(ProductPriceSchedule productPriceSchedule) {
    Product product = productMapper.getByCode(productPriceSchedule.getProduct().getCode());
    PriceSchedule schedule = priceScheduleMapper.getByCode(productPriceSchedule.getPriceSchedule().getCode());
    productPriceSchedule.getPriceSchedule().setId(schedule.getId());
    productPriceSchedule.getProduct().setId(product.getId());
    return mapper.getByProductCodePriceSchedule(productPriceSchedule.getProduct().getId(), schedule.getId());
  }

  public List<ProductPriceSchedule> getByProductId(Long id) {
    return mapper.getByProductId(id);
  }


  public List<ProductPriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(Long programId, Long facilityId) {
    return mapper.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);
  }
}
