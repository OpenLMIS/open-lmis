package org.openlmis.stockmanagement.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@NoArgsConstructor
public class LotService {

  @Autowired
  private LotRepository lotRepository;

  public Lot getOrCreateLotByLotNumberAndProduct(String lotNumber, Date expirationDate, Product product, Long userId) {
    Lot existingLot = lotRepository.getLotByLotNumberAndProductId(lotNumber, product.getId());
    if (existingLot == null) {
      return lotRepository.createLotWithLotNumberAndExpirationDateAndProductId(lotNumber, expirationDate, product, userId);
    }

    if (!expirationDate.equals(existingLot.getExpirationDate())) {
      lotRepository.saveLotConflict(existingLot.getId(), expirationDate, userId);
    }

    return existingLot;
  }
}
