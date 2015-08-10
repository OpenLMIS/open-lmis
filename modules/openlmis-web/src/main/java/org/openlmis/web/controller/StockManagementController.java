/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openlmis.core.dto.Lot;
import org.openlmis.core.dto.StockCard;
import org.openlmis.core.dto.StockCardLineItem;
import org.openlmis.core.service.StockManagementService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller provides GET, POST, and DELETE endpoints related to stock management.
 */

@Controller
@Api(value = "Stock Management", description = "Track the stock on hand at various facilities.")
@RequestMapping(value = "/api/v2/")
public class StockManagementController extends BaseController
{
  @Autowired
  private StockManagementService service;

  //TODO: Determine what the permissions associated with @PreAuthorize should be. (MANAGE_PROGRAM_PRODUCT, below, is just a placeholder).

  @RequestMapping(value = "products/{productId}/lots", method = GET, headers = ACCEPT_JSON)
  @ApiOperation(value = "Get information about all lots (batches) for a specified product.",
          notes = "(This endpoint is not yet ready for use.) <p /> Note that the products property will always be an object with an ID value. Optionally, it may be expanded to include all of the product's other properties (and associated values) as well. To specify that such an expansion should occur, add \"?expand=product\" to the query parameter. For example: <p> /api/2/lots/{lotId}?expand=product")
  public ResponseEntity getLots(@PathVariable Long productId,
                               @RequestParam(value = "expand", required = false) String expand)
  {
    boolean expandProduct = (expand != null && expand.contains("product"));
    List<Lot> lots = service.getLots(productId);

    if (lots != null) {
      return OpenLmisResponse.response(lots);
    } else {
      return OpenLmisResponse.error("The specified lots do not exist." , HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "facilities/{facilityId}/products/{productId}/stockcard", method = GET, headers = ACCEPT_JSON)
  @ApiOperation(value = "Get information about the stock card for the specified facility and product.",
      notes = "(This endpoint is not yet ready for use.)")
  public ResponseEntity getStockCard(@PathVariable Long facilityId, @PathVariable Long productId,
                                     @RequestParam(value = "lineItems", defaultValue = "1")Integer lineItems)
  {
    StockCard stockCard = service.getStockCard(facilityId, productId);

    if (stockCard != null) {
      filterLineItems(stockCard, lineItems);
      return OpenLmisResponse.response(stockCard);
    }
    else {
      return OpenLmisResponse.error("The specified stock card does not exist." , HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "facilities/{facilityId}/stockcards", method = GET, headers = ACCEPT_JSON)
  @ApiOperation(value = "Get information about all stock cards for the specified facility.",
      notes = "(This endpoint is not yet ready for use.)")
  public ResponseEntity getStockCards(@PathVariable Long facilityId,
                                      @RequestParam(value = "lineItems", defaultValue = "1")Integer lineItems,
                                      @RequestParam(value = "countOnly", defaultValue = "false")Boolean countOnly)
  {
    List<StockCard> stockCards = service.getStockCards(facilityId);

    if (countOnly) {
      return OpenLmisResponse.response("count", stockCards.size());
    }

    if (stockCards != null) {
      for (StockCard stockCard : stockCards) {
        filterLineItems(stockCard, lineItems);
      }
      return OpenLmisResponse.response("stockCards", stockCards);
    }
    else {
      return OpenLmisResponse.error("The specified stock cards do not exist." , HttpStatus.NOT_FOUND);
    }
  }

  private void filterLineItems(StockCard stockCard, Integer lineItemCount) {
    List<StockCardLineItem> lineItems = stockCard.getLineItems();
    if (lineItemCount < 0) {
      stockCard.setLineItems(lineItems.subList(0, 1));
    } else if (lineItemCount < lineItems.size()) {
      stockCard.setLineItems(lineItems.subList(0, lineItemCount));
    }
  }
}
