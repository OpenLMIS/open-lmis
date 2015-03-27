package org.openlmis.vaccine.builders.reports;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;

public class LogisticsLineItemBuilder {

  public static final Instantiator<LogisticsLineItem> defaultLogisticsLineItem = new Instantiator<LogisticsLineItem>() {

    @Override
    public LogisticsLineItem instantiate(PropertyLookup<LogisticsLineItem> lookup) {
      LogisticsLineItem item = new LogisticsLineItem();

      item.setProductId(1L);
      item.setProductName("The Product");
      item.setProductCategory("Vaccine");
      item.setProductCode("PCode");
      item.setDisplayOrder(1);
      return item;
    }
  };
}
