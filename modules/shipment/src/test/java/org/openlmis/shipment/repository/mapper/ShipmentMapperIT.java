/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;

@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ShipmentMapperIT {

  @Autowired
  ShipmentMapper shipmentMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  OrderMapper orderMapper;
  @Autowired
  RequisitionMapper requisitionMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  private ProgramMapper progamMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;


  @Test
  public void shouldInsert() throws Exception {

    Integer userId = 1;
    Product product = make(a(defaultProduct));
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod period = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(period);
    Program program = make(a(defaultProgram));
    progamMapper.insert(program);
    Rnr requisition = make(a(defaultRnr, with(RequisitionBuilder.facility, facility), with(RequisitionBuilder.periodId, period.getId())));
    requisitionMapper.insert(requisition);
    RnrDTO rnrDTO = RnrDTO.populateDTOWithRequisition(requisition);
    rnrDTO.setModifiedBy(userId);
    Order order = new Order(rnrDTO);
    
    orderMapper.insert(order);

    productMapper.insert(product);
    ShippedLineItem shippedLineItem = new ShippedLineItem(order.getId(), product.getCode(), 23);
    shipmentMapper.insertShippedLineItem(shippedLineItem);

    assertThat(shippedLineItem.getId(), is(notNullValue()));
  }

}
