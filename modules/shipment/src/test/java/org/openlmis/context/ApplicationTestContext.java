package org.openlmis.context;


import com.natpryce.makeiteasy.MakeItEasy;
import org.junit.Ignore;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.rnr.repository.mapper.RequisitionStatusChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;


@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Ignore
public class ApplicationTestContext extends AbstractTransactionalJUnit4SpringContextTests {
  @Autowired
  protected ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;
  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  private SupplyLineMapper supplyLineMapper;

  @Autowired
  private OrderMapper orderMapper;

  protected Program insertProgram() {
    Program program = make(MakeItEasy.a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
    return program;
  }

  protected ProcessingPeriod insertPeriod(String name, ProcessingSchedule processingSchedule, Date periodStartDate, Date periodEndDate) {
    ProcessingPeriod processingPeriod = make(MakeItEasy.a(defaultProcessingPeriod,
        MakeItEasy.with(scheduleId, processingSchedule.getId()), MakeItEasy.with(startDate, periodStartDate), MakeItEasy.with(endDate, periodEndDate),
        MakeItEasy.with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);

    return processingPeriod;
  }

  protected Rnr insertRequisition(ProcessingPeriod period, Program program, RnrStatus status, Boolean emergency, Facility facility, SupervisoryNode supervisoryNode, Date modifiedDate) {
    Rnr rnr = new Rnr(facility, program, period, emergency, MODIFIED_BY, 1L);
    rnr.setStatus(status);
    rnr.setEmergency(emergency);
    rnr.setModifiedDate(modifiedDate);
    rnr.setSubmittedDate(new Date(111111L));
    rnr.setProgram(program);
    rnr.setSupplyingDepot(facility);
    requisitionMapper.insert(rnr);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    rnr.setSupervisoryNodeId(supervisoryNode.getId());
    requisitionMapper.update(rnr);

    return rnr;
  }

  protected void insertProduct(String productCode) {
    Product product = make(MakeItEasy.a(ProductBuilder.defaultProduct, MakeItEasy.with(ProductBuilder.code, productCode)));
    productMapper.insert(product);
  }

  protected ProcessingSchedule insertProcessingSchedule() {
    ProcessingSchedule processingSchedule = make(MakeItEasy.a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);
    return processingSchedule;
  }

  protected Facility insertFacility() {
    Facility facility = make(MakeItEasy.a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    return facility;
  }

  protected SupervisoryNode insertSupervisoryNode(String code, Facility facility) {
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.code, code)));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  protected SupplyLine insertSupplyLine(Facility facility, SupervisoryNode supervisoryNode) {
    SupplyLine supplyLine = make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, facility),
        with(SupplyLineBuilder.supervisoryNode, supervisoryNode)));
    supplyLineMapper.insert(supplyLine);
    return supplyLine;
  }


  protected Order insertOrder(String productCode) {
    insertProduct(productCode);
    Facility facility = insertFacility();
    Program program = insertProgram();
    ProcessingSchedule processingSchedule = insertProcessingSchedule();
    ProcessingPeriod processingPeriod = insertPeriod("Period1", processingSchedule, new Date(), new Date());
    SupervisoryNode supervisoryNode = insertSupervisoryNode("N1", facility);
    Rnr rnr = insertRequisition(processingPeriod, program, RnrStatus.INITIATED, false, facility, supervisoryNode, new Date());

    Order order = new Order(rnr);
    order.setStatus(OrderStatus.IN_ROUTE);
    order.setSupplyLine(insertSupplyLine(facility, supervisoryNode));
    orderMapper.insert(order);
    return order;
  }

}
