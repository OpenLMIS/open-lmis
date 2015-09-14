package org.openlmis.vaccine.repository.Inventory;

import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.inventory.*;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryRepository {

    @Autowired
    VaccineInventoryMapper mapper;


    public StockCard getStockCard(Long facilityId, Long productId) {
        return mapper.getStockCardByFacilityAndProduct(facilityId, productId);
    }

    public StockCard getStockCardById(Long id) {
        return mapper.getStockCardById(id);
    }

    public List<StockCard> getStockCards(Long facilityId) {
        return mapper.getAllStockCardsByFacility(facilityId);
    }

    public LotOnHand getLotOnHand(Long stockCardId, Long lotId) {
        return mapper.getLotOnHand(stockCardId, lotId);
    }

    public Integer updateStockCard(StockCard stockCard) {
        return mapper.updateStockCard(stockCard);
    }

    public Integer updateLotOnHand(LotOnHand lotOnHand) {
        return mapper.updateLotOnHand(lotOnHand);
    }

    public Integer insertStockCardEntry(StockCardEntry stockCardEntry) {
        return mapper.insertStockCardEntry(stockCardEntry);
    }

    public Lot getLotByCode(String lotCode) {
        return mapper.getLotByCode(lotCode);
    }

    public Integer insertLot(Lot lot) {
        return mapper.insertLot(lot);
    }

    public Integer insertStockCard(StockCard stockCard) {
        return mapper.insertStockCard(stockCard);
    }

    public Integer insertLotsOnHand(LotOnHand lotOnHand) {
        return mapper.insertLotsOnHand(lotOnHand);
    }

    public Integer insertVVM(Long lotOnHandId, Integer vvmStatus) {
        return mapper.insertVVM(lotOnHandId, vvmStatus);

    }

    public Integer updateVVM(Long lotOnHandId, Integer vvmStatus) {
        return mapper.updateVVM(lotOnHandId, vvmStatus);
    }

    public Integer insertAdjustmentReason(AdjustmentReason reason) {
        return mapper.insertAdjustmentReasons(reason);
    }

}
