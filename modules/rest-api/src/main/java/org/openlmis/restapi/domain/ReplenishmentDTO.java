/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 ofthe License, or (at your option) any laterversion.
 *   
 * This program is distributed in the hope that it will be useful, but WITHOUT ANYWARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR APARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this programIf not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Transformer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.Facility;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.dto.RnrLineItemDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * This class is used to represent Data Transfer Object for replenishment, effectively composing attributes from
 * Rnr and attributes from order which are order specific like status, etc.
 */

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class ReplenishmentDTO extends RnrDTO {
  private String orderId;
  private String orderStatus;
  private String supplyingFacilityCode;

  public static ReplenishmentDTO prepareForREST(final Rnr rnr, Order order) {
    ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
    replenishmentDTO.setId(rnr.getId());
    replenishmentDTO.setAgentCode(rnr.getFacility().getCode());
    replenishmentDTO.setProgramCode(rnr.getProgram().getCode());
    replenishmentDTO.setRequisitionStatus(rnr.getStatus().name());
    replenishmentDTO.setEmergency(rnr.isEmergency());

    replenishmentDTO.setPeriodEndDate(rnr.getPeriod().getEndDate());
    replenishmentDTO.setPeriodStartDate(rnr.getPeriod().getStartDate());

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    replenishmentDTO.setStringPeriodEndDate(simpleDateFormat.format(rnr.getPeriod().getEndDate()));
    replenishmentDTO.setStringPeriodStartDate(simpleDateFormat.format(rnr.getPeriod().getStartDate()));

    ArrayList<RnrLineItem> lineItems = new ArrayList<RnrLineItem>() {{
      addAll(rnr.getNonFullSupplyLineItems());
      addAll(rnr.getFullSupplyLineItems());
    }};

    replenishmentDTO.setProducts((List<RnrLineItemDTO>) collect(lineItems, new Transformer() {
      @Override
      public Object transform(Object o) {
        return new RnrLineItemDTO((RnrLineItem) o);
      }
    }));
    if (order != null) {
      replenishmentDTO.orderId = order.getOrderNumber();
      replenishmentDTO.orderStatus = order.getStatus().name();
      Facility supplyingFacility = order.getSupplyingFacility();
      replenishmentDTO.supplyingFacilityCode = supplyingFacility == null ? null : supplyingFacility.getCode();
    }
    return replenishmentDTO;
  }
}
