/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.pod.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * OrderPODDTO holds order and POD related attributes like facilityCode, facilityName, supplyingDepot, periodStartDate,
 * periodEndDate, emergency etc.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPODDTO {

  private Long id;
  private String orderNumber;
  private String stringCreatedDate;
  private String facilityCode;
  private String facilityName;
  private String supplyingDepot;
  private String periodStartDate;
  private String periodEndDate;
  private String programName;
  private String district;
  private String region;
  private Boolean emergency;
  private Boolean alreadyReceived;


  public static OrderPODDTO getOrderDetailsForPOD(Order order) throws ParseException {
    OrderPODDTO orderPODDTO = new OrderPODDTO();
    orderPODDTO.setId(order.getId());
    orderPODDTO.setOrderNumber(order.getOrderNumber());

    String createdDate = order.getCreatedDate() == null ? null : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getCreatedDate());
    orderPODDTO.setStringCreatedDate(createdDate);
    orderPODDTO.setSupplyingDepot(order.getSupplyLine().getSupplyingFacility().getName());

    orderPODDTO.setFacilityCode(order.getRnr().getFacility().getCode());
    orderPODDTO.setFacilityName(order.getRnr().getFacility().getName());

    orderPODDTO.setProgramName(order.getRnr().getProgram().getName());
    orderPODDTO.setDistrict(order.getRnr().getFacility().getGeographicZone().getName());
    orderPODDTO.setRegion(order.getRnr().getFacility().getGeographicZone().getParent().getName());

    orderPODDTO.setPeriodStartDate(order.getRnr().getPeriod().getStringStartDate());
    orderPODDTO.setPeriodEndDate(order.getRnr().getPeriod().getStringEndDate());
    orderPODDTO.setEmergency(order.getRnr().isEmergency());
    orderPODDTO.setAlreadyReceived(order.getStatus() == OrderStatus.RECEIVED);
    return orderPODDTO;
  }
}
