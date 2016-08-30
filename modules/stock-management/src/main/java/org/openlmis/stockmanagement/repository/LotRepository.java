package org.openlmis.stockmanagement.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.domain.LotOnHand;
import org.openlmis.stockmanagement.domain.StockCardEntryLotItem;
import org.openlmis.stockmanagement.domain.StockCardEntryLotItemKV;
import org.openlmis.stockmanagement.repository.mapper.LotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@NoArgsConstructor
public class LotRepository {

  @Autowired
  LotMapper mapper;

  LotRepository(LotMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper);
  }

  public LotOnHand getLotOnHandByStockCardAndLot(Long stockCardId, Long lotId) {
    return mapper.getLotOnHandByStockCardAndLot(stockCardId, lotId);
  }

  public LotOnHand getLotOnHandByStockCardAndLotObject(Long stockCardId, Lot lot) {
    return mapper.getLotOnHandByStockCardAndLotObject(stockCardId, lot);
  }

  public Lot getOrCreateLot(Lot lot) {
    Lot l = mapper.getByObject(lot);
    if (null == l) {
      mapper.insert(lot);
      l = lot;
    }

    return l;
  }

  public Lot getOrCreateLot(String lotNumber, Date expirationDate, Product product, Long userId) {
    Lot lot = mapper.getLotByLotNumberAndProductId(lotNumber, product.getId());
    if (null == lot) {
      lot = new Lot();
      lot.setLotCode(lotNumber);
      lot.setProduct(product);
      lot.setExpirationDate(expirationDate);
      lot.setCreatedBy(userId);
      lot.setModifiedBy(userId);
      mapper.insert(lot);
    }

    return lot;
  }

  public void saveLotOnHand(LotOnHand lotOnHand) {
    if (null == lotOnHand.getId()) {
      mapper.insertLotOnHand(lotOnHand);
    } else {
      mapper.updateLotOnHand(lotOnHand);
    }
  }

    public LotOnHand getLotOnHandByLotNumberAndProductCodeAndFacilityId(String lotCode, String productCode, Long facilityId) {
        return mapper.getLotOnHandByLotNumberAndProductCodeAndFacilityId(lotCode, productCode, facilityId);
    }

  public void createStockCardEntryLotItem(StockCardEntryLotItem stockCardEntryLotItem) {
    mapper.insertStockCardEntryLotItem(stockCardEntryLotItem);
    for (StockCardEntryLotItemKV stockCardEntryLotItemKV : stockCardEntryLotItem.getExtensions()) {
      mapper.insertStockCardEntryLotItemKV(stockCardEntryLotItem, stockCardEntryLotItemKV);
    }
  }
}
