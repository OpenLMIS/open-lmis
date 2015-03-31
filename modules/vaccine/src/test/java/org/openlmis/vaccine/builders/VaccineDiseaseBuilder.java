package org.openlmis.vaccine.builders;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.VaccineDisease;

public class VaccineDiseaseBuilder {

  public static final Instantiator<VaccineDisease> defaultDisease = new Instantiator<VaccineDisease>() {

    @Override
    public VaccineDisease instantiate(PropertyLookup<VaccineDisease> lookup) {
      VaccineDisease item = new VaccineDisease();
      item.setName("Polio");
      item.setDescription("The Polio");
      item.setDisplayOrder(1);
      return item;
    }
  };
}
