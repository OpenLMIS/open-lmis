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
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnOption;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

public class RnrColumnBuilder {

  public static final Property<RnrColumn, String> columnName = newProperty();
  public static final Property<RnrColumn, Boolean> visible = newProperty();
  public static final Property<RnrColumn, RnRColumnSource> source = newProperty();
  public static final Property<RnrColumn, LossesAndAdjustments> lossesAndAdjustments = newProperty();
  public static final Property<RnrColumn, RnrColumnOption> option = newProperty();

  public static final String DEFAULT_NAME = "stockInHand";
  public static final Boolean DEFAULT_VISIBLE = Boolean.TRUE;
  private static final RnrColumnOption OPTION = null;
  public static final Instantiator<RnrColumn> defaultRnrColumn = new Instantiator<RnrColumn>() {

    @Override
    public RnrColumn instantiate(PropertyLookup<RnrColumn> lookup) {
      RnrColumn rnrColumn = new RnrColumn();
      rnrColumn.setName(lookup.valueOf(columnName, DEFAULT_NAME));
      rnrColumn.setVisible(lookup.valueOf(visible, DEFAULT_VISIBLE));
      rnrColumn.setSource(lookup.valueOf(source, USER_INPUT));
      rnrColumn.setConfiguredOption(lookup.valueOf(option, OPTION));
      return rnrColumn;
    }
  };
}
