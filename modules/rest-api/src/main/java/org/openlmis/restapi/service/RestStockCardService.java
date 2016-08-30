package org.openlmis.restapi.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;
import org.openlmis.core.repository.SyncUpHashRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.restapi.domain.StockCardDTO;
import org.openlmis.restapi.domain.StockCardMovementDTO;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.dto.LotEvent;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.service.LotService;
import org.openlmis.stockmanagement.service.StockCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@NoArgsConstructor
public class RestStockCardService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private SyncUpHashRepository syncUpHashRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private StockAdjustmentReasonRepository stockAdjustmentReasonRepository;

    @Autowired
    private StockCardService stockCardService;

    @Autowired
    private LotService lotService;

  @Transactional
    public List<StockCardEntry> adjustStock(Long facilityId, List<StockEvent> stockEvents, Long userId) {
        if (!validFacility(facilityId)) {
            throw new DataException("error.facility.unknown");
        }

        List<StockCardEntry> entries = createStockCardEntries(stockEvents, facilityId, userId);
        stockCardService.addStockCardEntries(entries);
        stockCardService.updateAllStockCardSyncTimeForFacilityToNow(facilityId);

        return entries;
    }

    @Transactional
    public void updateStockCardSyncTime(Long facilityId, List<String> stockCardProductCodeList) {
        if (facilityId == null || stockCardProductCodeList == null) {
            throw new DataException("");
        }

        if (stockCardProductCodeList.isEmpty()) {
            stockCardService.updateAllStockCardSyncTimeForFacilityToNow(facilityId);
        } else {
            stockCardService.updateStockCardSyncTimeToNow(facilityId, stockCardProductCodeList);
        }
    }

    public List<StockCardDTO> queryStockCardByOccurred(long facilityId, Date startTime, Date endTime) {
        List<StockCard> stockCards = stockCardService.queryStockCardByOccurred(facilityId, startTime, endTime);

        return transformStockCardsToDTOs(stockCards);
    }

    private List<StockCardEntry> createStockCardEntries(List<StockEvent> stockEvents, Long facilityId, Long userId) {
        Map<String, StockCard> stockCardMap = new HashMap<>();
        Map<String, Lot> lotMap = new HashMap<>();

        List<StockCardEntry> entries = new ArrayList<>();
        for (StockEvent stockEvent : stockEvents) {
            if (syncUpHashRepository.hashExists(stockEvent.getSyncUpHash())) {
                continue;
            }

            syncUpHashRepository.save(stockEvent.getSyncUpHash());
            String errorInStockEvent = validateStockEvent(stockEvent);
            if (errorInStockEvent != null) {
                throw new DataException(errorInStockEvent);
            }

            StockCard stockCard = getOrCreateStockCard(facilityId, stockEvent.getProductCode(), stockCardMap, userId);

            StockCardEntry entry = processStockEvent(stockEvent, stockCard, lotMap, userId);
            entries.add(entry);
        }
        return entries;
    }

    private StockCard getOrCreateStockCard(Long facilityId, String productCode, Map<String, StockCard> stockCardMap, Long userId) {
        StockCard stockCard;

        if (stockCardMap.get(productCode) == null) {
            stockCard = stockCardService.getOrCreateStockCard(facilityId, productCode, userId);
            stockCardMap.put(productCode, stockCard);
        } else {
            stockCard = stockCardMap.get(productCode);
        }
        return stockCard;
    }

    private StockCardEntry processStockEvent(StockEvent stockEvent, final StockCard stockCard, Map<String, Lot> lotMap, Long userId) {
        final StockAdjustmentReason stockAdjustmentReason = stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockEvent.getReasonName());

        long quantity = stockEvent.getQuantity();
        quantity = stockAdjustmentReason.getAdditive() ? quantity : quantity * -1;

        final StockCardEntry entry = new StockCardEntry(stockCard, StockCardEntryType.ADJUSTMENT, quantity, stockEvent.getOccurred(), stockEvent.getReferenceNumber(), stockEvent.getRequestedQuantity());
        entry.setAdjustmentReason(stockAdjustmentReason);
        entry.setCreatedBy(userId);
        entry.setModifiedBy(userId);
        entry.setCreatedDate(stockEvent.getCreatedTime());
        if (stockEvent.getLotEventList() != null) {
            processLotEvents(stockEvent.getLotEventList(), stockAdjustmentReason, entry, lotMap, userId);
        }

        Map<String, String> customProps = stockEvent.getCustomProps();
        if (null != customProps) {
            for (String k : customProps.keySet()) {
                entry.addKeyValue(k, customProps.get(k));
            }
        }
        return entry;
    }

    private void processLotEvents(List<LotEvent> lotEvents, StockAdjustmentReason stockAdjustmentReason, StockCardEntry entry, Map<String, Lot> lotMap, Long userId) {
        for (final LotEvent lotEvent : lotEvents) {
            StockCardEntryLotItem stockCardEntryLotItem;
            long lotMovementQuantity = stockAdjustmentReason.getAdditive() ? lotEvent.getQuantity() : lotEvent.getQuantity() * -1;

            Lot lot = getOrCreateLot(lotEvent, lotMap, entry.getStockCard().getProduct(), userId);
            LotOnHand lotOnHand = stockCardService.getOrCreateLotOnHand(lot, entry.getStockCard());
            entry.getStockCard().getLotsOnHand().add(lotOnHand);

            stockCardEntryLotItem = new StockCardEntryLotItem(lot, lotMovementQuantity);
            stockCardEntryLotItem.setCreatedBy(userId);
            stockCardEntryLotItem.setModifiedBy(userId);

            if (lotEvent.getCustomProps() != null) {
                for (String key : lotEvent.getCustomProps().keySet()) {
                    stockCardEntryLotItem.addKeyValue(key, lotEvent.getCustomProps().get(key));
                }
            }
            entry.getStockCardEntryLotItems().add(stockCardEntryLotItem);
        }
    }

  private Lot getOrCreateLot(LotEvent lotEvent, Map<String, Lot> lotMap, Product product, Long userId) {
      Lot lot;

      if (lotMap.get(lotEvent.getLotNumber()) == null) {
          lot = lotService.getOrCreateLot(lotEvent.getLotNumber(), lotEvent.getExpirationDate(), product, userId);
          lotMap.put(lotEvent.getLotNumber(), lot);
      } else {
          lot = lotMap.get(lotEvent.getLotNumber());
      }
      return lot;
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

    private List<StockCardDTO> transformStockCardsToDTOs(List<StockCard> stockCards) {
        List<StockCardDTO> stockCardDTOs = new ArrayList<>();
        for (StockCard stockCard : stockCards) {
            StockCardDTO stockCardDTO = new StockCardDTO(stockCard);
            stockCardDTOs.add(stockCardDTO);
            stockCardDTO.setStockMovementItems(transformStockCardEntries(stockCard.getEntries()));
        }
        return stockCardDTOs;
    }

    private List<StockCardMovementDTO> transformStockCardEntries(List<StockCardEntry> stockCardEntries) {
        ArrayList<StockCardMovementDTO> stockCardMovementDTOs = new ArrayList<>();
        for (StockCardEntry stockCardEntry : stockCardEntries) {
            stockCardMovementDTOs.add(new StockCardMovementDTO(stockCardEntry));
        }
        return stockCardMovementDTOs;
    }
}
