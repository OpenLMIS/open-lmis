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
import org.openlmis.core.dto.StockCard;
import org.openlmis.core.dto.StockCardLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Exposes the services for handling stock management.
 */

@Service
@NoArgsConstructor
public class StockManagementService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  ProductService productService;

  public Lot getLot(long lotId, boolean expandProduct)
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

  public StockCard getStockCard(Long facilityId, Long productId) {
    return getTestStockCardData(1L, facilityId, productId);
  }

  public List<StockCard> getStockCards(Long facilityId) {
    Long[] productIdData = {2412L,2420L,2417L,2414L,2416L};

    List<StockCard> stockCards = new ArrayList<>();
    for (int i = 0; i < productIdData.length; i++) {
      stockCards.add(getTestStockCardData((long)i, facilityId, productIdData[i]));
    }

    return stockCards;
  }

  private StockCard getTestStockCardData(Long id, Long facilityId, Long productId) {
    StockCard stockCard = new StockCard();

    stockCard.setId(id);
    stockCard.setFacility(facilityService.getById(facilityId));
    Product product = productService.getById(productId);
    stockCard.setProduct(product);
    stockCard.setTotalQuantityOnHand(105L);
    stockCard.setEffectiveDate(new Date());
    stockCard.setNotes("Test stock card for " + product.getPrimaryName());

    String[][] lineItemData = {
        {"1","RECEIPT","200"},
        {"2","ISSUE","10"},
        {"3","ISSUE","5"},
        {"4","ISSUE","15"},
        {"5","ISSUE","10"},
        {"6","ISSUE","5"},
        {"7","ISSUE","5"},
        {"8","ISSUE","10"},
        {"9","ISSUE","15"},
        {"10","ISSUE","10"},
        {"11","ISSUE","10"}
    };
    List<StockCardLineItem> lineItems = new ArrayList<>();
    for (String[] item : lineItemData) {
      StockCardLineItem lineItem = new StockCardLineItem();
      lineItem.setId(Long.parseLong(item[0]));
      lineItem.setType(StockCardLineItemType.valueOf(item[1]));
      lineItem.setQuantity(Long.parseLong(item[2]));
      lineItems.add(lineItem);
    }
    stockCard.setLineItems(lineItems);

    return stockCard;
  }
}
