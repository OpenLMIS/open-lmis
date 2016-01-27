package org.openlmis.stockmanagement.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.dto.StockEventType;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class StockEventBuilder {

  public static final Long DEFAULT_FACILITY_ID = 123L;
  public static final String DEFAULT_PRODUCT_CODE = "P999";

  private static final Date DEFAULT_OCCURRED = new Date();
  private static final Long DEFAULT_QUANTITY = 100L;
  private static final String DEFAULT_REASON_NAME = "some reason";
  private static final StockEventType DEFAULT_STOCK_TYPE = StockEventType.ADJUSTMENT;

  public static final Property<StockEvent, Long> facilityId = newProperty();
  public static final Property<StockEvent, String> productCode = newProperty();
  public static final Property<StockEvent, Long> quantity = newProperty();
  public static final Property<StockEvent, String> reasonName = newProperty();
  public static final Property<StockEvent, Date> occurred = newProperty();
  public static final Property<StockEvent, StockEventType> type = newProperty();

  public static final Instantiator<StockEvent> defaultStockEvent = new Instantiator<StockEvent>() {
    @Override
    public StockEvent instantiate(PropertyLookup<StockEvent> lookup) {
      StockEvent stockEvent = new StockEvent();
      stockEvent.setFacilityId(lookup.valueOf(facilityId, DEFAULT_FACILITY_ID));
      stockEvent.setProductCode(lookup.valueOf(productCode, DEFAULT_PRODUCT_CODE));
      stockEvent.setQuantity(lookup.valueOf(quantity, DEFAULT_QUANTITY));
      stockEvent.setReasonName(lookup.valueOf(reasonName, DEFAULT_REASON_NAME));
      stockEvent.setOccurred(lookup.valueOf(occurred, DEFAULT_OCCURRED));
      stockEvent.setType(lookup.valueOf(type, DEFAULT_STOCK_TYPE));
      return stockEvent;
    }
  };
}
