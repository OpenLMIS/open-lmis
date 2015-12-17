package org.openlmis.restapi.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryType;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.service.StockCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
public class RestStockCardService {

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private StockAdjustmentReasonRepository stockAdjustmentReasonRepository;

  @Autowired
  private StockCardService stockCardService;

  public List<StockCardEntry> adjustStock(Long facilityId, List<StockEvent> stockEventList, Long userId) {
    if (!validFacility(facilityId)) {
      throw new DataException("error.facility.unknown");
    }

    Map<String, StockCard> stockCardMap = new HashMap<>();
    List<StockCardEntry> entries = new ArrayList<>();

    for (StockEvent stockEvent : stockEventList) {
      String errorInStockEvent = validateStockEvent(stockEvent);
      if (errorInStockEvent != null) {
        throw new DataException(errorInStockEvent);
      }

      String productCode = stockEvent.getProductCode();
      StockCard stockCard;

      if (stockCardMap.get(productCode) == null) {
        stockCard = stockCardService.getOrCreateStockCard(facilityId, productCode);
        stockCardMap.put(productCode, stockCard);
      } else {
        stockCard = stockCardMap.get(productCode);
      }

      StockCardEntry entry = createStockCardEntry(stockEvent, stockCard, userId);
      entries.add(entry);
    }
    stockCardService.addStockCardEntries(entries);
    return entries;
  }


  private StockCardEntry createStockCardEntry(StockEvent stockEvent, StockCard stockCard, Long userId) {
    StockAdjustmentReason stockAdjustmentReason = stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockEvent.getReasonName());

    long quantity = stockEvent.getQuantity();
    quantity = stockAdjustmentReason.getAdditive() ? quantity : quantity * -1;

    StockCardEntry entry = new StockCardEntry(stockCard, StockCardEntryType.ADJUSTMENT, quantity, stockEvent.getOccurred(), stockEvent.getReferenceNumber());
    entry.setAdjustmentReason(stockAdjustmentReason);
    entry.setCreatedBy(userId);
    entry.setModifiedBy(userId);

    Map<String, String> customProps = stockEvent.getCustomProps();
    if (null != customProps) {
      for (String k : customProps.keySet()) {
        entry.addKeyValue(k, customProps.get(k));
      }
    }
    return entry;
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
