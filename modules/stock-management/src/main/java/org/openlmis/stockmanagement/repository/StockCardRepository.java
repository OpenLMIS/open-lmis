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

import java.util.List;
import java.util.Objects;

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
    mapper.insertEntry(entry);
    for (StockCardEntryKV item : entry.getKeyValues()) {
      mapper.insertEntryKeyValue(entry, item.getKeyColumn(), item.getValueColumn());
    }
  }

  public void updateStockCard(StockCard card) {
    Objects.requireNonNull(card);
    mapper.update(card);
  }

  public Product getProductByStockCardId(Long stockCardId) {
    return mapper.getProductByStockCardId(stockCardId);
  }
}
