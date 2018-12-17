package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.stockmanagement.util.StockCardEntryKVReduceStrategy;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockCard extends BaseModel {

  @JsonIgnore
  private Facility facility;

  private Product product;

  private Long totalQuantityOnHand;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = DateDeserializer.class)
  private Date effectiveDate;

  private String notes;

  private List<StockCardEntry> entries;

  private List<LotOnHand> lotsOnHand;

  @JsonIgnore
  private StockCardEntryKVReduceStrategy strategy;

  @JsonIgnore
  private StockCardEntry lastestStockCardEntry;

  private StockCard(Facility facility, Product product) {
    Objects.requireNonNull(facility);
    Objects.requireNonNull(product);
    this.facility = facility;
    this.product = product;
    this.totalQuantityOnHand = 0L;
    this.effectiveDate = new Date();
    this.notes = "";
    this.entries = null;
    this.lotsOnHand = null;
    this.strategy = null;
  }

  public void addToTotalQuantityOnHand(long quantity) {
    this.totalQuantityOnHand += quantity;
  }

  public void setLatestStockCardEntry(StockCardEntry entry) {
    this.lastestStockCardEntry = entry;
  }

  public static final StockCard createZeroedStockCard(Facility facility, Product product) {
    return new StockCard(facility, product);
  }
}