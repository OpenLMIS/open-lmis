package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.StockCardEntryLotItem;
import org.openlmis.stockmanagement.domain.StockCardEntryLotItemKV;

import java.util.HashMap;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class LotMovementDTO {

  String lotNumber;
  Long quantity;
  HashMap<String,String> extensions = new HashMap<>();

  public LotMovementDTO(StockCardEntryLotItem stockCardEntryLotItem) {
    this.lotNumber = stockCardEntryLotItem.getLot().getLotCode();
    this.quantity = stockCardEntryLotItem.getQuantity();
    initCustomProps(stockCardEntryLotItem);
  }

  private void initCustomProps(StockCardEntryLotItem stockCardEntryLotItem) {
    for (StockCardEntryLotItemKV stockCardLotEntryKV :stockCardEntryLotItem.getExtensions()){
      this.extensions.put(stockCardLotEntryKV.getKey(), stockCardLotEntryKV.getValue());
    }
  }
}
