package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RegimenLineItemBuilder;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RnrMapperForSIMAMIT {

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private RnrMapperForSIMAM rnrMapperForSIMAM;
  @Autowired
  private RegimenLineItemMapper regimenLineItemMapper;

  private Rnr requisition;
  private Program program;
  private Facility facility;
  private Product product1;
  private Product product2;
  private RnrLineItem rnrLineItem1;
  private RnrLineItem rnrLineItem2;

  @Before
  public void setUp() {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    ProcessingSchedule processingSchedule = make(a(defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
        with(scheduleId, processingSchedule.getId()), with(startDate, new Date()), with(endDate, new Date()),
        with(ProcessingPeriodBuilder.name, "Period 1")));

    processingPeriodMapper.insert(processingPeriod);

    requisition = new Rnr(new Facility(facility.getId()), new Program(program.getId()), processingPeriod, false, MODIFIED_BY, 1L);
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);

    ProductCategory productCategory = new ProductCategory("C1", "Category 1", 1);
    productCategoryMapper.insert(productCategory);

    product1 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P1"), with(ProductBuilder.fullSupply, true)));
    product2 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P2"), with(ProductBuilder.fullSupply, true)));
    productMapper.insert(product1);
    productMapper.insert(product2);

    ProgramProduct programProduct1 = new ProgramProduct(program, product1, 1, true);
    ProgramProduct programProduct2 = new ProgramProduct(program, product2, 1, true);
    programProduct1.setProductCategory(productCategory);
    programProduct2.setProductCategory(productCategory);
    programProductMapper.insert(programProduct1);
    programProductMapper.insert(programProduct2);

    rnrLineItem1 = make(a(defaultRnrLineItem, with(fullSupply, true), with(productCode, product1.getCode())));
    rnrLineItem2 = make(a(defaultRnrLineItem, with(fullSupply, false), with(productCode, product2.getCode())));
    rnrLineItem1.setRnrId(requisition.getId());
    rnrLineItem2.setRnrId(requisition.getId());

    rnrLineItemMapper.insert(rnrLineItem1, Collections.EMPTY_LIST.toString());
    rnrLineItemMapper.insert(rnrLineItem2, Collections.EMPTY_LIST.toString());
    rnrLineItemMapper.update(rnrLineItem1);
    rnrLineItemMapper.update(rnrLineItem2);
    requisitionMapper.update(requisition);

    requisition.setClientSubmittedTime(DateUtil.parseDate("2015-11-11 11:11:11"));
    requisitionMapper.updateClientFields(requisition);
  }

  @Test
  public void shouldGetRnrItemsForSIMAM() {
    List<Map<String, String>> itemsData = rnrMapperForSIMAM.getRnrItemsForSIMAMImport(requisition);
    assertEquals(2, itemsData.size());
    assertEquals(requisition.getId().intValue(), itemsData.get(0).get("id"));
    assertEquals(requisition.getId().intValue(), itemsData.get(1).get("id"));
    assertEquals(program.getCode(), itemsData.get(0).get("program_code"));
    assertEquals(program.getCode(), itemsData.get(1).get("program_code"));
    assertEquals(facility.getName(), itemsData.get(0).get("facility_name"));
    assertEquals(facility.getName(), itemsData.get(1).get("facility_name"));
    assertEquals("2015-11-11", new SimpleDateFormat("yyyy-MM-dd").format(itemsData.get(0).get("date")));
    assertEquals("2015-11-11", new SimpleDateFormat("yyyy-MM-dd").format(itemsData.get(1).get("date")));
    assertEquals("a" + product2.getCode(), itemsData.get(0).get("product_code").toString());
    assertEquals("a" + product1.getCode(), itemsData.get(1).get("product_code").toString());
    assertEquals(rnrLineItem2.getBeginningBalance(), itemsData.get(0).get("beginning_balance"));
    assertEquals(rnrLineItem1.getBeginningBalance(), itemsData.get(1).get("beginning_balance"));
    assertEquals(rnrLineItem2.getQuantityDispensed(), itemsData.get(0).get("quantity_dispensed"));
    assertEquals(rnrLineItem1.getQuantityDispensed(), itemsData.get(1).get("quantity_dispensed"));
    assertEquals(rnrLineItem2.getQuantityReceived(), itemsData.get(0).get("quantity_received"));
    assertEquals(rnrLineItem1.getQuantityReceived(), itemsData.get(1).get("quantity_received"));
    assertEquals(rnrLineItem2.getTotalLossesAndAdjustments(), itemsData.get(0).get("total_losses_and_adjustments"));
    assertEquals(rnrLineItem1.getTotalLossesAndAdjustments(), itemsData.get(1).get("total_losses_and_adjustments"));
    assertEquals(rnrLineItem2.getStockInHand(), itemsData.get(0).get("stock_in_hand"));
    assertEquals(rnrLineItem1.getStockInHand(), itemsData.get(1).get("stock_in_hand"));
    assertEquals(rnrLineItem2.getQuantityApproved(), itemsData.get(0).get("quantity_approved"));
    assertEquals(rnrLineItem1.getQuantityApproved(), itemsData.get(1).get("quantity_approved"));
  }

  @Test
  public void shouldGetRegimenItemsForSIMAM() {
    RegimenLineItem regimenLineItem1 = make(a(RegimenLineItemBuilder.defaultRegimenLineItem,
        with(RegimenLineItemBuilder.name, "regimen1"),
        with(RegimenLineItemBuilder.patientsOnTreatment, 500)));
    regimenLineItem1.setRnrId(requisition.getId());
    RegimenLineItem regimenLineItem2 = make(a(RegimenLineItemBuilder.defaultRegimenLineItem,
        with(RegimenLineItemBuilder.name, "regimen2"),
        with(RegimenLineItemBuilder.patientsOnTreatment, 1000)));
    regimenLineItem2.setRnrId(requisition.getId());
    regimenLineItemMapper.insert(regimenLineItem1);
    regimenLineItemMapper.insert(regimenLineItem2);
    regimenLineItemMapper.update(regimenLineItem1);
    regimenLineItemMapper.update(regimenLineItem2);

    List<Map<String, String>> regimenItemsForSIMAMImport = rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(requisition);
    assertEquals(2, regimenItemsForSIMAMImport.size());
    assertEquals(requisition.getId().intValue(), regimenItemsForSIMAMImport.get(0).get("requisition_id"));
    assertEquals(requisition.getId().intValue(), regimenItemsForSIMAMImport.get(1).get("requisition_id"));
    assertEquals(program.getCode(), regimenItemsForSIMAMImport.get(0).get("program_code"));
    assertEquals(program.getCode(), regimenItemsForSIMAMImport.get(1).get("program_code"));
    assertEquals("2015-11-11", new SimpleDateFormat("yyyy-MM-dd").format(regimenItemsForSIMAMImport.get(0).get("date")));
    assertEquals("2015-11-11", new SimpleDateFormat("yyyy-MM-dd").format(regimenItemsForSIMAMImport.get(1).get("date")));
    assertEquals("regimen1", regimenItemsForSIMAMImport.get(0).get("regimen_name"));
    assertEquals("regimen2", regimenItemsForSIMAMImport.get(1).get("regimen_name"));
    assertEquals(500, regimenItemsForSIMAMImport.get(0).get("total"));
    assertEquals(1000, regimenItemsForSIMAMImport.get(1).get("total"));
  }

}