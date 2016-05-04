package org.openlmis.stockmanagement.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.domain.StockCardEntry;
import org.openlmis.stockmanagement.domain.StockCardEntryKV;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@NoArgsConstructor
public class StockCardRepository {

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  StockCardMapper mapper;

  /**
   * Will get or create a stock card for the given facility and product.  If the facility or product do not exist,
   * an exception will be thrown.
   *
   * @param facilityId the id of the facility
   * @param productCode  the code of the product
   * @return the persisted stock card.
   */
  public StockCard getOrCreateStockCard(long facilityId, String productCode) {
    StockCard card = mapper.getByFacilityAndProduct(facilityId, productCode);
    if (null == card) {
      Facility facility = facilityRepository.getById(facilityId);
      Product product = productRepository.getByCode(productCode);
      Objects.requireNonNull(facility);
      Objects.requireNonNull(product);
      card = StockCard.createZeroedStockCard(facility, product);
      mapper.insert(card);
    }

    Objects.requireNonNull(card);
    return card;
  }

  /**
   * Gets a stock card by facility id and product id
   * @param facilityId the id of the facility
   * @param productCode the code of the product
   * @return the unique stock card, or null if stock card, facility, or product do not exist.
   */
  public StockCard getStockCardByFacilityAndProduct(long facilityId, String productCode) {
    return mapper.getByFacilityAndProduct(facilityId, productCode);
  }

  public StockCard getStockCardById(Long facilityId, Long id) {
    return mapper.getByFacilityAndId(facilityId, id);
  }

  public List<StockCard> getStockCards(Long facilityId) {
    return mapper.getAllByFacility(facilityId);
  }

  public void persistStockCardEntry(StockCardEntry entry) {
    if (entry.hasId())
      throw new IllegalArgumentException("Already persisted stock card entries can not be saved " +
          "as persisted entry is immutable");
    //add synced time from mobile application, if no using the current time
    if (entry.getCreatedDate() == null) {
      entry.setCreatedDate(new Date());
    }
    mapper.insertEntry(entry);
    for (StockCardEntryKV item : entry.getExtensions()) {
      mapper.insertEntryKeyValue(entry, item.getKey(), item.getValue());
    }
  }

  public void updateStockCard(StockCard card) {
    Objects.requireNonNull(card);
    mapper.update(card);
  }

  public List<StockCard> queryStockCardByOccurred(Long facilityId, Date startTime, Date endTime) {
    List<StockCard> basicStockCards = mapper.queryStockCardBasicInfo(facilityId);
    if (basicStockCards == null) {
      return new ArrayList<>();
    }
    for (StockCard stockCard : basicStockCards) {
      List<StockCardEntry> entries = mapper.queryStockCardEntriesByDateRange(stockCard.getId(), startTime, endTime);
      if (entries == null) {
        //if no movement return will query latest six movements
        entries = mapper.queryLatestStockCardEntries(stockCard.getId());
        entries = sortStockCardEntryById(entries);
      }
      stockCard.setEntries(entries);
    }

    return basicStockCards;
  }

  private List<StockCardEntry> sortStockCardEntryById(List<StockCardEntry> entries) {
    List<StockCardEntry> sortedEntries = new ArrayList<>();
    if (entries != null) {
      for (int i = entries.size() - 1; i >= 0; i--) {
        sortedEntries.add(entries.get(i));
      }
    }
    return sortedEntries;
  }

  public Product getProductByStockCardId(Long stockCardId) {
    return mapper.getProductByStockCardId(stockCardId);
  }

  public void updateAllStockCardSyncTimeForFacility(long facilityId) {
    mapper.updateAllStockCardSyncTimeForFacilityToNow(facilityId);
  }

  public void updateStockCardSyncTimeToNow(long facilityId, String stockCardProductCode) {
    mapper.updateStockCardToSyncTimeToNow(facilityId, stockCardProductCode);
  }
}
