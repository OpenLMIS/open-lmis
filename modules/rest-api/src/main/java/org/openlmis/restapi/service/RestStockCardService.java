package org.openlmis.restapi.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.service.StockCardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RestStockCardService {

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private StockAdjustmentReasonRepository stockAdjustmentReasonRepository;

  @Autowired
  private StockCardService stockCardService;

  public StockCard adjustStock(Long facilityId, List<StockEvent> stockEventList, Long loggedInUserId) {
    if (!validFacility(facilityId)) {
      throw new DataException("error.facility.unknown");
    }

    for (StockEvent stockEvent: stockEventList) {
      String errorInStockEvent = validateStockEvent(stockEvent);
      if (errorInStockEvent != null) {
        throw new DataException(errorInStockEvent);
      }

      stockCardService.getOrCreateStockCard(facilityId, stockEvent.getProductCode());
    }

    return null;
  }

  private boolean validFacility(Long facilityId) {
    return facilityRepository.getById(facilityId) != null;
  }

  private String validateStockEvent(StockEvent stockEvent) {
    if (!stockEvent.isValidAdjustment()) return "error.stockmanagement.invalidadjustment";
    if (!validProduct(stockEvent)) return "error.product.unknown";
    if (!validAdjustmentReason(stockEvent)) return "error.stockadjustmentreason.unknown";
    return null;
  }

  private boolean validProduct(StockEvent stockEvent) {
    return productService.getByCode(stockEvent.getProductCode()) != null;
  }

  private boolean validAdjustmentReason(StockEvent stockEvent) {
    return stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockEvent.getReasonName()) != null;
  }
}
