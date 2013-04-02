/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.mapper.*;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.ORDERED;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(RequisitionStatusChange.class)
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
  private CommentMapper commentMapper;
  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;
  @Mock
  private CommaSeparator separator;

  @Mock
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;

  @InjectMocks
  private RequisitionRepository requisitionRepository;

  private LossesAndAdjustments lossAndAdjustmentForLineItem = new LossesAndAdjustments();
  private RnrLineItem rnrLineItem1;
  private RnrLineItem rnrLineItem2;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
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
    RnrLineItem rnrLineItem = rnr.getFullSupplyLineItems().get(0);
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
  public void shouldUpdateAllNonFullSupplyLineItems() throws Exception {
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setBeginningBalance(2);
    RnrLineItem rnrLineItem2 = new RnrLineItem();
    rnr.add(rnrLineItem, false);
    rnr.add(rnrLineItem2, false);
    RnrLineItem fullSupply = new RnrLineItem();
    fullSupply.setFullSupply(true);

    requisitionRepository.update(rnr);

    verify(rnrLineItemMapper).deleteAllNonFullSupplyForRequisition(rnr.getId());
    verify(rnrLineItemMapper).insertNonFullSupply(rnrLineItem);
    verify(rnrLineItemMapper).insertNonFullSupply(rnrLineItem2);
    verify(rnrLineItemMapper, never()).insertNonFullSupply(fullSupply);
  }

  @Test
  public void shouldGetRequisitionsForFacilityProgramAndPeriods() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    List<ProcessingPeriod> periods = asList(new ProcessingPeriod(1), new ProcessingPeriod(2));
    when(separator.commaSeparateIds(periods)).thenReturn("{1, 2}");
    List<Rnr> expected = new ArrayList<>();
    when(requisitionMapper.get(facility, program, "{1, 2}")).thenReturn(expected);

    List<Rnr> actual = requisitionRepository.get(facility, program, periods);

    assertThat(actual, is(expected));
    verify(requisitionMapper).get(facility, program, "{1, 2}");
  }

  @Test
  public void shouldCreateOrderBatch() throws Exception {
    OrderBatch orderBatch = new OrderBatch();
    requisitionRepository.createOrderBatch(orderBatch);
    verify(requisitionMapper).createOrderBatch(orderBatch);
  }

  @Test
  public void shouldGetOrderById() throws Exception {
    OrderBatch orderBatch = new OrderBatch();
    orderBatch.setCreatedByUserId(1);
    when(requisitionMapper.getOrderBatchById(1)).thenReturn(orderBatch);
    OrderBatch orderBatchReturned = requisitionRepository.getOrderBatchById(1);
    verify(requisitionMapper).getOrderBatchById(1);
    assertThat(orderBatchReturned, is(orderBatch));
  }

  @Test
  public void shouldGetOrderBatches() throws Exception {
    List<Rnr> expectedRequisitions = new ArrayList<>();
    when(requisitionMapper.getByStatus(ORDERED)).thenReturn(expectedRequisitions);

    List<Rnr> rnrs = requisitionRepository.getByStatus(ORDERED);

    assertThat(rnrs, is(expectedRequisitions));
    verify(requisitionMapper).getByStatus(ORDERED);
  }

  @Test
  public void shouldGetCategoryCount(){

    boolean fullSupply = true;
    when(rnrLineItemMapper.getCategoryCount(rnr, fullSupply)).thenReturn(10);

    Integer categoryCount = requisitionRepository.getCategoryCount(rnr, fullSupply);

    assertThat(categoryCount, is(10));
    verify(rnrLineItemMapper).getCategoryCount(rnr, fullSupply);

  }

  @Test
  public void shouldGetCommentsForARnR() throws Exception {
     requisitionRepository.getCommentsByRnrID(1);
     verify(commentMapper).getAllCommentsByRnrId(1);
  }

  @Test
  public void shouldInsertAComment() throws Exception {
    Comment comment = new Comment();
    requisitionRepository.insertComment(comment);

    verify(commentMapper).insert(comment);
  }

  @Test
  public void shouldLogRequisitionStatusChanges() throws Exception {
    RequisitionStatusChange requisitionStatusChange = new RequisitionStatusChange();
    Rnr requisition = new Rnr();
    whenNew(RequisitionStatusChange.class).withArguments(requisition).thenReturn(requisitionStatusChange);
    requisitionRepository.logStatusChange(requisition);
    verify(requisitionStatusChangeMapper).insert(requisitionStatusChange);
  }
}
