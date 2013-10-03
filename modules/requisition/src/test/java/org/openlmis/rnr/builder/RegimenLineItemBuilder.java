/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.rnr.domain.RegimenLineItem;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RegimenLineItemBuilder {


  public static final Property<RegimenLineItem, String> code = newProperty();
  public static final Property<RegimenLineItem, String> name = newProperty();
  public static final Property<RegimenLineItem, String> remarks = newProperty();
  public static final Property<RegimenLineItem, Integer> patientsOnTreatment = newProperty();
  public static final Property<RegimenLineItem, Integer> patientsToInitiateTreatment = newProperty();
  public static final Property<RegimenLineItem, Integer> patientsStoppedTreatment = newProperty();

  public static final Integer PATIENTS_ON_TREATMENT = 3;
  public static final Integer PATIENTS_TO_INITIATE_TREATMENT = 3;
  public static final Integer PATIENTS_STOPPED_TREATMENT = 3;
  private static final String CODE = "R01";
  private static final String NAME = "Regimen";
  private static final String REMARKS = "remarks";
  public static final Instantiator<RegimenLineItem> defaultRegimenLineItem = new Instantiator<RegimenLineItem>() {

    @Override
    public RegimenLineItem instantiate(PropertyLookup<RegimenLineItem> lookup) {
      RegimenLineItem regimenLineItem = new RegimenLineItem();
      regimenLineItem.setCode(lookup.valueOf(code, CODE));
      regimenLineItem.setName(lookup.valueOf(name, NAME));
      regimenLineItem.setPatientsOnTreatment(lookup.valueOf(patientsOnTreatment, PATIENTS_ON_TREATMENT));
      regimenLineItem.setPatientsToInitiateTreatment(lookup.valueOf(patientsToInitiateTreatment, PATIENTS_TO_INITIATE_TREATMENT));
      regimenLineItem.setPatientsStoppedTreatment(lookup.valueOf(patientsStoppedTreatment, PATIENTS_STOPPED_TREATMENT));
      regimenLineItem.setRemarks(lookup.valueOf(remarks, REMARKS));
      regimenLineItem.setCategory(new RegimenCategory("RCode", "Category Name", 6));
      return regimenLineItem;
    }
  };
}
