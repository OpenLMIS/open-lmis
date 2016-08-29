package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.stockmanagement.util.LatestSyncedStrategy;
import org.openlmis.stockmanagement.util.StockCardEntryKVReduceStrategy;
import org.openlmis.stockmanagement.util.StockManagementUtils;

import java.util.*;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include = NON_EMPTY)
public class LotOnHand extends BaseModel {

  @JsonIgnore
  private StockCard stockCard;

  private Lot lot;

  private Long quantityOnHand;

  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
  @JsonDeserialize(using=DateDeserializer.class)
  private Date effectiveDate;

  @JsonIgnore
  private List<StockCardEntryKV> extensions;

  @JsonIgnore
  private StockCardEntryKVReduceStrategy strategy;

  private LotOnHand(Lot lot, StockCard stockCard) {
    Objects.requireNonNull(lot);
    Objects.requireNonNull(stockCard);
    this.lot = lot;
    this.stockCard = stockCard;
    this.quantityOnHand = 0L;
    this.effectiveDate = new Date();
    this.extensions = new ArrayList<>();
    this.strategy = null;
  }

  public Map<String, String> getCustomProps() {
    if (null == strategy) strategy = new LatestSyncedStrategy();

    Map<String, String> customProps = StockManagementUtils.getKeyValueAggregate(extensions, strategy);

    return customProps.isEmpty() ? null : customProps;
  }

  public void addToQuantityOnHand(long quantity) {
    this.quantityOnHand += quantity;
  }

  /**
   * This method creates a zeroed lot on hand. If lot or stockCard are null, it will throw an exception, rather than
   * returning null.
   * @param lot
   * @param stockCard
   * @return
   */
  public static final LotOnHand createZeroedLotOnHand(Lot lot, StockCard stockCard) {
    return new LotOnHand(lot, stockCard);
  }
}
