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
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;

import static java.util.Arrays.asList;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

public class RnrTemplateBuilder {



  public static final Instantiator<ProgramRnrTemplate> defaultRnrTemplate = new Instantiator<ProgramRnrTemplate>() {

    @Override
    public ProgramRnrTemplate instantiate(PropertyLookup<ProgramRnrTemplate> lookup) {
       return new ProgramRnrTemplate(1L, asList(
           rnrColumn(QUANTITY_REQUESTED, true, null, "Requested Quantity"),
           rnrColumn(REASON_FOR_REQUESTED_QUANTITY, true, null, "Requested Quantity Reason"),
           rnrColumn(STOCK_OUT_DAYS, true, CALCULATED, "stockOutDays"),
           rnrColumn(NORMALIZED_CONSUMPTION, false, USER_INPUT, "normalizedConsumption"),
           rnrColumn(STOCK_IN_HAND, true, USER_INPUT, "stock in hand"),
           rnrColumn(QUANTITY_DISPENSED, false, CALCULATED, "quantity dispensed"),
           rnrColumn(QUANTITY_RECEIVED, true, USER_INPUT, "quantity received"),
           rnrColumn(BEGINNING_BALANCE, true, USER_INPUT, "beginning balance"),
           rnrColumn(LOSSES_AND_ADJUSTMENTS, true, USER_INPUT, "losses and adjustment"),
           rnrColumn(COST, true, null, "cost"),
           rnrColumn(PRICE, true, null, "price"),
           rnrColumn(PRODUCT, true, null, "product")
       ));
    }
  };

  public static RnrColumn rnrColumn(String columnName, boolean visible, RnRColumnSource selectedColumnSource, String label) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setSource(selectedColumnSource);
    rnrColumn.setVisible(visible);
    rnrColumn.setName(columnName);
    rnrColumn.setLabel(label);
    rnrColumn.setFormulaValidationRequired(true);
    return rnrColumn;
  }


}
