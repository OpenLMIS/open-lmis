package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionRepositoryTest {

  public static final Integer FACILITY_ID = 1;
  public static final Integer PROGRAM_ID = 1;
  public static final Integer PERIOD_ID = 1;
  public static final Integer HIV = 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RequisitionMapper requisitionMapper;
  @Mock
  private RnrLineItemMapper rnrLineItemMapper;
  @Mock
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  private RequisitionRepository requisitionRepository;
  private LossesAndAdjustments lossAndAdjustmentForLineItem = new LossesAndAdjustments();
  private RnrLineItem rnrLineItem1;
  private RnrLineItem rnrLineItem2;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    requisitionRepository = new RequisitionRepository(requisitionMapper, rnrLineItemMapper, lossesAndAdjustmentsMapper);
    rnr = new Rnr();
    rnrLineItem1 = new RnrLineItem();
    rnrLineItem1.setId(1);
    rnrLineItem2 = new RnrLineItem();
    rnrLineItem2.setId(2);
    rnr.add(rnrLineItem1, true);
    rnr.add(rnrLineItem2, true);
    rnrLineItem1.addLossesAndAdjustments(lossAndAdjustmentForLineItem);
    rnrLineItem2.addLossesAndAdjustments(lossAndAdjustmentForLineItem);

    rnr.setFacility(new Facility(FACILITY_ID));
    rnr.setProgram(new Program(PROGRAM_ID));
    rnr.setPeriod(new ProcessingPeriod(PERIOD_ID));
    rnr.setStatus(INITIATED);
  }

  @Test
  public void shouldInsertRnrAndItsLineItems() throws Exception {
    rnr.setId(1);
    requisitionRepository.insert(rnr);
    assertThat(rnr.getStatus(), is(INITIATED));
    verify(requisitionMapper).insert(rnr);
    verify(rnrLineItemMapper, times(2)).insert(any(RnrLineItem.class));
    verify(lossesAndAdjustmentsMapper, never()).insert(any(RnrLineItem.class), any(LossesAndAdjustments.class));
    RnrLineItem rnrLineItem = rnr.getLineItems().get(0);
    assertThat(rnrLineItem.getRnrId(), is(1));
  }

  @Test
  public void shouldUpdateRnrAndItsLineItemsAlongWithLossesAndAdjustments() throws Exception {
    requisitionRepository.update(rnr);
    verify(requisitionMapper).update(rnr);
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem1.getId());
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem2.getId());
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem1, lossAndAdjustmentForLineItem);
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem2, lossAndAdjustmentForLineItem);
    verify(rnrLineItemMapper).update(rnrLineItem1);
    verify(rnrLineItemMapper).update(rnrLineItem2);
  }

  @Test
  public void shouldReturnRnrAndItsLineItems() {
    Facility facility = new Facility(FACILITY_ID);
    Program hivProgram = new Program(HIV);
    ProcessingPeriod period = new ProcessingPeriod(PERIOD_ID);
    Rnr initiatedRequisition = new Rnr(facility, hivProgram, period);
    initiatedRequisition.setId(1);
    when(requisitionMapper.getRequisition(facility, hivProgram, period)).thenReturn(initiatedRequisition);
    List<RnrLineItem> lineItems = new ArrayList<>();
    when(rnrLineItemMapper.getRnrLineItemsByRnrId(1)).thenReturn(lineItems);

    Rnr rnr = requisitionRepository.getRequisition(facility, hivProgram, period);

    assertThat(rnr, is(equalTo(initiatedRequisition)));
    assertThat(rnr.getLineItems(), is(equalTo(lineItems)));
  }

  @Test
  public void shouldReturnNullIfRnrNotDefined() {
    Rnr expectedRnr = null;
    Facility facility = new Facility(FACILITY_ID);
    Program program = new Program(HIV);
    when(requisitionMapper.getRequisition(facility, program, null)).thenReturn(expectedRnr);
    Rnr rnr = requisitionRepository.getRequisition(facility, program, null);
    assertThat(rnr, is(expectedRnr));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    Rnr expectedRnr = new Rnr();
    Integer rnrId = 1;
    when(requisitionMapper.getById(rnrId)).thenReturn(expectedRnr);
    Rnr returnedRnr = requisitionRepository.getById(rnrId);
    assertThat(returnedRnr, is(expectedRnr));
  }

  @Test
  public void shouldThrowExceptionIfRnrNotFound() throws Exception {
    Integer rnrId = 1;
    when(requisitionMapper.getById(rnrId)).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Requisition Not Found");
    requisitionRepository.getById(rnrId);
  }

  @Test
  public void shouldGetRequisitionByRoleAssignment() throws Exception {
    List<Rnr> requisitions = new ArrayList<>();
    RoleAssignment roleAssignment = new RoleAssignment();
    when(requisitionMapper.getAuthorizedRequisitions(roleAssignment)).thenReturn(requisitions);

    List<Rnr> actualRequisitions = requisitionRepository.getAuthorizedRequisitions(roleAssignment);

    assertThat(actualRequisitions, is(requisitions));
  }

  @Test
  public void shouldGetTheLastRequisitionToEnterThePostSubmitFlow() throws Exception {
    Rnr rnr = new Rnr();
    when(requisitionMapper.getLastRequisitionToEnterThePostSubmitFlow(FACILITY_ID, PROGRAM_ID)).thenReturn(rnr);

    assertThat(requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(FACILITY_ID, PROGRAM_ID), is(rnr));
  }


  @Test
  public void shouldSetAllFieldsExceptNonFullSupplyRnrFieldsToZeroAndInsertRequisitionLineItem(){
    RnrLineItem rnrLineItem = new RnrLineItem();
    requisitionRepository.insertNonFullSupply(rnrLineItem);
    verify(rnrLineItemMapper).insertNonFullSupply(rnrLineItem);
    assertThat(rnrLineItem.getQuantityReceived(), is(0));
    assertThat(rnrLineItem.getQuantityDispensed(), is(0));
    assertThat(rnrLineItem.getBeginningBalance(), is(0));
    assertThat(rnrLineItem.getStockInHand(), is(0));
    assertThat(rnrLineItem.getTotalLossesAndAdjustments(), is(0));
    assertThat(rnrLineItem.getCalculatedOrderQuantity(), is(0));
    assertThat(rnrLineItem.getNewPatientCount(), is(0));
    assertThat(rnrLineItem.getStockOutDays(), is(0));
    assertThat(rnrLineItem.getNormalizedConsumption(), is(0));
    assertThat(rnrLineItem.getAmc(), is(0));
    assertThat(rnrLineItem.getMaxStockQuantity(), is(0));
  }
}
