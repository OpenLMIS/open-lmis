package org.openlmis.vaccine.builders.reports;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;

public class DiseaseLineItemBuilder {

  public static final Instantiator<DiseaseLineItem> defaultDiseaseLineItem = new Instantiator<DiseaseLineItem>() {

    @Override
    public DiseaseLineItem instantiate(PropertyLookup<DiseaseLineItem> lookup) {
      DiseaseLineItem item = new DiseaseLineItem();

      item.setDiseaseName("Yellow Fever");
      item.setDiseaseId(1L);
      item.setDisplayOrder(1);
      item.setReportId(1L);
      item.setCases(20L);
      item.setCumulative(20L);
      item.setDeath(1L);

      return item;
    }
  };
}
