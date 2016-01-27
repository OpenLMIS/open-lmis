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

  @JsonIgnore
  private List<StockCardEntryKV> keyValues;

  public StockCardEntry(StockCard card, StockCardEntryType type, long quantity, Date occurred, String referenceNumber) {
    this.stockCard = Objects.requireNonNull(card);
    this.type = Objects.requireNonNull(type);
    this.quantity = Objects.requireNonNull(quantity);
    this.keyValues = new ArrayList<>();
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

  public Map<String, String> getCustomProps() {
    Map<String, String> customProps = new HashMap<>();
    for (StockCardEntryKV item : keyValues) {
      customProps.put(item.getKeyColumn(), item.getValueColumn());
    }
    return customProps.isEmpty() ? null : customProps;
  }

  public void addKeyValue(String key, String value) {
    String newKey = key.trim().toLowerCase();
    keyValues.add(new StockCardEntryKV(newKey, value, new Date()));
  }

}