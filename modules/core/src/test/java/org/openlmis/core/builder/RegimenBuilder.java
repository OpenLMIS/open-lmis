/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RegimenBuilder {


  public static final Property<Regimen, String> regimenCode = newProperty();
  public static final Property<Regimen, String> regimenName = newProperty();
  public static final Property<Regimen, Boolean> active = newProperty();
  public static final Property<Regimen, Long> programId = newProperty();
  public static final Property<Regimen, Integer> displayOrder = newProperty();
  public static final Property<Regimen, RegimenCategory> category = newProperty();

  private static final String REGIMEN_CODE = "3TC/AZT";
  private static final String REGIMEN_NAME = "3TC/AZT + EFV";
  public static final Instantiator<Regimen> defaultRegimen = new Instantiator<Regimen>() {
    @Override
    public Regimen instantiate(PropertyLookup<Regimen> lookup) {
      Regimen regimen = new Regimen();
      regimen.setCode(lookup.valueOf(regimenCode, REGIMEN_CODE));
      regimen.setName(lookup.valueOf(regimenName, REGIMEN_NAME));
      regimen.setActive(lookup.valueOf(active, true));
      regimen.setProgramId(lookup.valueOf(programId, 1L));
      RegimenCategory regimenCategory = new RegimenCategory("ADULTS", "Adults",1);
      regimenCategory.setId(1l);
      regimen.setCategory(lookup.valueOf(category, regimenCategory));
      regimen.setDisplayOrder(lookup.valueOf(displayOrder, 1));
      return regimen;
    }
  };
}
