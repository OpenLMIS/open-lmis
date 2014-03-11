/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.EDIConfiguration;
import org.springframework.stereotype.Repository;

/**
 * It maps the Shipment File Configuration(EDIConfiguration) entity to corresponding representation in database.
 */

@Repository
public interface ShipmentConfigurationMapper {

  @Select("SELECT * FROM shipment_configuration")
  EDIConfiguration get();

  @Update({"UPDATE shipment_configuration SET",
    "headerInFile =  #{headerInFile},",
    "modifiedDate = DEFAULT,",
    "modifiedBy = #{modifiedBy}"})
  void update(EDIConfiguration shipmentConfiguration);

}
