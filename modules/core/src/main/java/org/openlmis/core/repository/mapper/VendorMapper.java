/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Vendor;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {

  @Select("SELECT name, id FROM vendors WHERE name = #{name} AND active = TRUE")
  Vendor getByName(String name);

  @Select("SELECT authToken FROM vendors WHERE name = #{name}")
  String getToken(String name);

  @Select("SELECT V.name, V.active FROM vendors V INNER JOIN users U ON V.id = U.vendorId WHERE U.id = #{id}")
  Vendor getByUserId(long id);
}
