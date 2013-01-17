package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@RunWith(MockitoJUnitRunner.class)
public class RnrRepositoryTest {

  public static final Integer FACILITY_ID = 1;
  public static final Integer PROGRAM_ID = 1;
  public static final Integer PERIOD_ID = 1;
  public static final Integer HIV = 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RnrMapper rnrMapper;
  @Mock
  RnrLineItemMapper rnrLineItemMapper;
  @Mock
  LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Mock
  SupervisoryNodeRepository supervisoryNodeRepository;

  private RnrRepository rnrRepository;
  private LossesAndAdjustments lossAndAdjustmentForLineItem = new LossesAndAdjustments();
  private RnrLineItem rnrLineItem1;
  private RnrLineItem rnrLineItem2;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    rnrRepository = new RnrRepository(rnrMapper, rnrLineItemMapper, lossesAndAdjustmentsMapper);
    rnr = new Rnr();
    rnrLineItem1 = new RnrLineItem();
    rnrLineItem1.setId(1);
    rnrLineItem2 = new RnrLineItem();
    rnrLineItem2.setId(2);
    rnr.add(rnrLineItem1);
    rnr.add(rnrLineItem2);
    rnrLineItem1.addLossesAndAdjustments(lossAndAdjustmentForLineItem);
    rnrLineItem2.addLossesAndAdjustments(lossAndAdjustmentForLineItem);
    rnr.setFacilityId(FACILITY_ID);
    rnr.setProgramId(PROGRAM_ID);
    rnr.setPeriodId(PERIOD_ID);
    rnr.setStatus(INITIATED);
  }

  @Test
  public void shouldInsertRnrAndItsLineItems() throws Exception {
    rnr.setId(1);
    rnrRepository.insert(rnr);
    assertThat(rnr.getStatus(), is(INITIATED));
    verify(rnrMapper).insert(rnr);
    verify(rnrLineItemMapper, times(2)).insert(any(RnrLineItem.class));
    verify(lossesAndAdjustmentsMapper, never()).insert(any(RnrLineItem.class), any(LossesAndAdjustments.class));
    RnrLineItem rnrLineItem = rnr.getLineItems().get(0);
    assertThat(rnrLineItem.getRnrId(), is(1));
  }

  @Test
  public void shouldUpdateRnrAndItsLineItemsAlongWithLossesAndAdjustments() throws Exception {
    rnrRepository.update(rnr);
    verify(rnrMapper).update(rnr);
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem1.getId());
    verify(lossesAndAdjustmentsMapper).deleteByLineItemId(rnrLineItem2.getId());
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem1, lossAndAdjustmentForLineItem);
    verify(lossesAndAdjustmentsMapper).insert(rnrLineItem2, lossAndAdjustmentForLineItem);
    verify(rnrLineItemMapper).update(rnrLineItem1);
    verify(rnrLineItemMapper).update(rnrLineItem2);
  }

  @Test
  public void shouldReturnRnrAndItsLineItems() {
    int modifiedBy = 1;
    Rnr initiatedRequisition = new Rnr(FACILITY_ID, HIV, PERIOD_ID, modifiedBy);
    initiatedRequisition.setId(1);
    when(rnrMapper.getRequisition(FACILITY_ID, HIV, PERIOD_ID)).thenReturn(initiatedRequisition);
    List<RnrLineItem> lineItems = new ArrayList<>();
    when(rnrLineItemMapper.getRnrLineItemsByRnrId(1)).thenReturn(lineItems);

    Rnr rnr = rnrRepository.getRequisition(FACILITY_ID, HIV, PERIOD_ID);

    assertThat(rnr, is(equalTo(initiatedRequisition)));
    assertThat(rnr.getLineItems(), is(equalTo(lineItems)));
  }

  @Test
  public void shouldReturnNullIfRnrNotDefined() {
    Rnr expectedRnr = null;
    when(rnrMapper.getRequisition(FACILITY_ID, HIV, null)).thenReturn(expectedRnr);
    Rnr rnr = rnrRepository.getRequisition(FACILITY_ID, HIV, null);
    assertThat(rnr, is(expectedRnr));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    Rnr expectedRnr = new Rnr();
    Integer rnrId = 1;
    when(rnrMapper.getById(rnrId)).thenReturn(expectedRnr);
    Rnr returnedRnr = rnrRepository.getById(rnrId);
    assertThat(returnedRnr, is(expectedRnr));
  }

  @Test
  public void shouldThrowExceptionIfRnrNotFound() throws Exception {
    Integer rnrId = 1;
    when(rnrMapper.getById(rnrId)).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Requisition Not Found");
    rnrRepository.getById(rnrId);
  }

  @Test
  public void shouldGetRequisitionByFacilitiesAndPrograms() throws Exception {
    List<Rnr> expectedRequisitions = new ArrayList<>();
    when(rnrMapper.getSubmittedRequisitionsForFacilitiesAndPrograms("{1, 2}", "{1, 2}")).thenReturn(expectedRequisitions);

    List<Facility> facilities = new ArrayList<>();
    Facility facility1 = new Facility();
    facility1.setId(1);
    Facility facility2 = new Facility();
    facility2.setId(2);

    facilities.add(facility1);
    facilities.add(facility2);

    List<Program> programs = new ArrayList<>();
    Program program1 = new Program();
    program1.setId(1);
    Program program2 = new Program();
    program2.setId(2);

    programs.add(program1);
    programs.add(program2);

    List<Rnr> resultRequisitions = rnrRepository.getSubmittedRequisitionsForFacilitiesAndPrograms(facilities, programs);

    verify(rnrMapper).getSubmittedRequisitionsForFacilitiesAndPrograms("{1, 2}", "{1, 2}");
    assertThat(resultRequisitions, is(expectedRequisitions));
  }
}
