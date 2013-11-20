/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Data
@NoArgsConstructor
public class Report {
  private Long requisitionId;
  private String facilityCode;
  private Long facilityId;
  private String programCode;
  private Long programId;
  private Long periodId;
  private String userName;
  private Boolean emergency;
  private List<RnrLineItem> products;

  private String agentCode;


  public void validate() {
    if (isEmpty(agentCode) || isEmpty(programCode)) {
      throw new DataException("error.restapi.mandatory.missing");
    }
  }

  @JsonIgnore
  public Rnr getRequisition() {
    Rnr rnr = new Rnr();
    rnr.setId(requisitionId);
    rnr.setFullSupplyLineItems(products);
    return rnr;
  }

  public void validateForApproval() {
    if (products == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }
    for (RnrLineItem rnrLineItem : products) {
      if (isEmpty(rnrLineItem.getProductCode()) || rnrLineItem.getQuantityApproved() == null)
        throw new DataException("error.restapi.mandatory.missing");
      if (rnrLineItem.getQuantityApproved() < 0)
        throw new DataException("error.restapi.quantity.approved.negative");
    }
  }

  public Rnr getRnrWithSkippedProducts(Rnr rnr) {
    List<RnrLineItem> rnrLineItemsFromReport = new ArrayList<>();
    for (final RnrLineItem fullSupplyLineItem : rnr.getFullSupplyLineItems()) {
      RnrLineItem productLineItem = (RnrLineItem) find(getProducts(), new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((RnrLineItem) o).getProductCode().equals(fullSupplyLineItem.getProductCode());
        }
      });
      if (productLineItem == null) {
        fullSupplyLineItem.setSkipped(true);
        rnrLineItemsFromReport.add(fullSupplyLineItem);
      } else {
        productLineItem.setId(fullSupplyLineItem.getId());
        rnrLineItemsFromReport.add(productLineItem);
      }
    }
    rnr.setFullSupplyLineItems(rnrLineItemsFromReport);
    return rnr;
  }
}
