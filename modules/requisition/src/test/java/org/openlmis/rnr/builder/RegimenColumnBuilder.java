/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.RegimenColumn;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RegimenColumnBuilder {
  public static final Property<RegimenColumn, String> name = newProperty();
  public static final Property<RegimenColumn, Boolean> visible = newProperty();
  public static final Property<RegimenColumn, String> label = newProperty();

  public static final String DEFAULT_NAME = "patientsOnTreatment";
  public static final Boolean VISIBLE = true;
  public static final String LABEL = "patients on treatment";
  public static final Instantiator<RegimenColumn> defaultRegimenColumn = new Instantiator<RegimenColumn>() {

    @Override
    public RegimenColumn instantiate(PropertyLookup<RegimenColumn> lookup) {
      RegimenColumn regimenColumn = new RegimenColumn();
      regimenColumn.setName(lookup.valueOf(name, DEFAULT_NAME));
      regimenColumn.setVisible(lookup.valueOf(visible, VISIBLE));
      regimenColumn.setLabel(lookup.valueOf(label, LABEL));

      return regimenColumn;
    }
  };
}
