/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
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
      return regimenLineItem;
    }
  };
}
