/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.rnr.domain.LineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.joda.time.format.DateTimeFormat.forPattern;

/**
 *  OrderPOD represents an entity that holds a list of OrderPODLineItem. It keeps track of delivery details of an order
 *  generated for a facility, program and period.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderPOD extends BaseModel {

  private Long orderId;
  private String orderNumber;
  private Long facilityId;
  private Long programId;
  private Long periodId;
  private List<OrderPODLineItem> podLineItems = new ArrayList<>();
  private String deliveredBy;
  private String receivedBy;

  @JsonDeserialize(using = DateDeserializer.class)
  private Date receivedDate;

  public OrderPOD(Long id) {
    this.id = id;
  }

  public OrderPOD(Long orderId, String orderNumber, Long userId) {
    this.orderId = orderId;
    this.orderNumber = orderNumber;
    this.createdBy = userId;
    this.modifiedBy = userId;
  }

  public void validate() {
    if (orderId == null) {
      throw new DataException("error.mandatory.fields.missing");
    }
    for (OrderPODLineItem lineItem : podLineItems) {
      lineItem.validate();
    }
  }

  public void fillPOD(Rnr requisition) {
    this.facilityId = requisition.getFacility().getId();
    this.programId = requisition.getProgram().getId();
    this.periodId = requisition.getPeriod().getId();
  }

  public void fillPODLineItems(List<? extends LineItem> lineItems) {
    this.podLineItems = new ArrayList<>();
    for (LineItem lineItem : lineItems) {
      if (!validPacksToShip(lineItem)) continue;
      this.podLineItems.add(new OrderPODLineItem(lineItem, this.createdBy));
    }
  }

  private boolean validPacksToShip(LineItem lineItem) {
    return !lineItem.isRnrLineItem() || ((RnrLineItem) lineItem).getPacksToShip() > 0;
  }

  public void copy(OrderPOD orderPOD) throws ParseException {
    this.modifiedBy = orderPOD.getModifiedBy();
    this.receivedDate = trimHoursFromDate(orderPOD.getReceivedDate());
    this.receivedBy = orderPOD.getReceivedBy();
    this.deliveredBy = orderPOD.getDeliveredBy();
    for (final OrderPODLineItem newLineItem : orderPOD.podLineItems) {
      newLineItem.setModifiedBy(orderPOD.getModifiedBy());
      OrderPODLineItem existingLineItem = (OrderPODLineItem) find(this.podLineItems, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((OrderPODLineItem) o).getId().equals(newLineItem.getId());
        }
      });
      if (existingLineItem != null) existingLineItem.copy(newLineItem);
    }
  }

  @JsonIgnore
  public String getStringReceivedDate() throws ParseException {
    if (this.receivedDate == null)
      return null;
    return forPattern("yyyy-MM-dd").print(new DateTime(this.receivedDate));
  }

  public Date trimHoursFromDate(Date receivedDate) throws ParseException {
    if (receivedDate == null)
      return null;
    SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd/MM/yyyy");
    return dateFormatWithoutTime.parse(dateFormatWithoutTime.format(receivedDate));
  }
}