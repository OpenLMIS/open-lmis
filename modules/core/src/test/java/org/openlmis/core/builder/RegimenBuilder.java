/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
