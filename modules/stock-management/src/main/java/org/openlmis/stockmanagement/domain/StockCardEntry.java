package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.StockAdjustmentReason;

import java.util.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown=true)
public class StockCardEntry extends BaseModel {

  @JsonIgnore
  private StockCard stockCard;

  // TODO: will need to determine during stock receipt code if this is necessary to track
  private StockCardEntryType type;

  private Long quantity;

  private String referenceNumber;

  private StockAdjustmentReason adjustmentReason;

  private LotOnHand lotOnHand;

  String notes;

  private Date occurred;

  private List<StockCardEntryKV> extensions;

  public StockCardEntry(StockCard card, StockCardEntryType type, long quantity, Date occurred, String referenceNumber) {
    this.stockCard = Objects.requireNonNull(card);
    this.type = Objects.requireNonNull(type);
    this.quantity = Objects.requireNonNull(quantity);
    this.extensions = new ArrayList<>();
    this.occurred = occurred;
    this.referenceNumber = referenceNumber;
  }

  @JsonIgnore
  public final boolean isValid() {
    if(null == type) return false;
    if(null == quantity) return false;

    return true;
  }

  @JsonIgnore
  public final boolean isValidAdjustment() {
    if(false == isValid()) return false;
    if(StockCardEntryType.ADJUSTMENT == type && null == adjustmentReason) return false;

    return true;
  }

  public void addKeyValue(String key, String value) {
    String newKey = key.trim().toLowerCase();
    extensions.add(new StockCardEntryKV(newKey, value, new Date()));
  }

}