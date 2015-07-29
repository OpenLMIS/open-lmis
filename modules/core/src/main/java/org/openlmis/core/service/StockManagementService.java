/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.GeographicZoneGeometry;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.mapper.GeographicZoneGeoJSONMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyList;


/**
 * Exposes the services for handling stock management.
 */

@Service
@NoArgsConstructor
public class StockManagementService {


  public Lot getTestLot(long lotId, boolean expandProduct)
  {
    if(lotId != 100)
      return null;

    Lot lot = new Lot(100L, 1261L);
    lot.setLotCode("123-AB");
    lot.setManufacturerName("MyManufacturer");

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      lot.setManufactureDate(dateFormat.parse("03-31-1981"));
      lot.setExpirationDate(dateFormat.parse("03-31-1983"));
    }
    catch (ParseException e)
    {}

    Product product = new Product();
    product.setId(123L);
    product.setGenericName("Generic Product Name");
    product.setFullName("Full Name");
    product.setCode("product code");

    if(expandProduct)
        lot.setProduct(product);
    else
        lot.setProduct(product.getId());

    return lot;
  }
}
