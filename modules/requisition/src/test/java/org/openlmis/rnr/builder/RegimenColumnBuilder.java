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
import org.openlmis.rnr.domain.RegimenColumn;

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
