package org.openlmis.stockmanagement.service;

import org.apache.log4j.Logger;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.repository.CMMRepository;
import org.openlmis.stockmanagement.repository.StockCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CMMService {

  @Autowired
  private CMMRepository repository;
  @Autowired
  private StockCardRepository stockCardRepository;

  private static final Logger LOGGER = Logger.getLogger(CMMService.class);

  public void updateCMMEntries(List<CMMEntry> cmmEntries) {
    for (CMMEntry entry: cmmEntries) {
      if(null == stockCardRepository.getStockCardByFacilityAndProduct(entry.getFacilityId(), entry.getProductCode())) {
        LOGGER.info(String.format("invalid product code : %s", entry.getProductCode()));
        continue;
      }
      repository.createOrUpdate(entry);
    }
  }
}
