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
    return lotRepository.getOrCreateLotByLotNumberAndProductId(lotNumber, expirationDate, product, userId);
  }
}
