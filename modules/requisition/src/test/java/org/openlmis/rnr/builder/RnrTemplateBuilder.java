/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
