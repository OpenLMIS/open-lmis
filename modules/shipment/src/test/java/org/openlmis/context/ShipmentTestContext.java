package org.openlmis.context;


import org.junit.Ignore;
import org.openlmis.core.domain.*;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.openlmis.rnr.context.RequisitionTestContext;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;

@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Ignore
public class ShipmentTestContext extends RequisitionTestContext {

  @Autowired
  private OrderMapper orderMapper;

  protected Order insertOrder(String productCode) {
    insertProduct(productCode);
    Facility facility = insertFacility();
    Program program = insertProgram();
    ProcessingSchedule processingSchedule = insertProcessingSchedule();
    ProcessingPeriod processingPeriod = insertPeriod("Period1", processingSchedule, new Date(), new Date());
    SupervisoryNode supervisoryNode = insertSupervisoryNode("N1", "Approval Point 1", facility);
    Rnr rnr = insertRequisition(processingPeriod, program, RnrStatus.INITIATED, false, facility, supervisoryNode, new Date());

    Order order = new Order(rnr);
    order.setStatus(OrderStatus.IN_ROUTE);
    order.setSupplyLine(insertSupplyLine(facility, supervisoryNode, program));
    order.setOrderNumber("OrderHIV00000001R");
    orderMapper.insert(order);
    return order;
  }

}
