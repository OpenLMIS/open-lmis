/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpiInventoryMapper {


  @Insert({"INSERT INTO epi_inventory_line_items (facilityVisitId, productCode, productName, productDisplayOrder, idealQuantity) VALUES ",
    "(#{facilityVisitId}, #{productCode}, #{productName}, #{productDisplayOrder}, #{idealQuantity})"})
  @Options(useGeneratedKeys = true)
  void saveLineItem(EpiInventoryLineItem lineItem);

  @Select({"SELECT * FROM epi_inventory_line_items WHERE facilityVisitId = #{facilityVisitId} ORDER BY productDisplayOrder, LOWER(productCode)"})
  List<EpiInventoryLineItem> getLineItemsBy(Long facilityVisitId);
}
