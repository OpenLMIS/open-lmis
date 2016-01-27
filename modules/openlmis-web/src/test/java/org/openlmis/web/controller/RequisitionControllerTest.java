/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.*;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.domain.RightName.APPROVE_REQUISITION;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;
import static org.openlmis.rnr.service.RequisitionService.RNR_SUBMITTED_SUCCESSFULLY;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RnrDTO.class, RequisitionController.class})
public class RequisitionControllerTest {

  public static final String FACILITY_CODE = "F14";
  public static final String FACILITY_NAME = "Facility";
  public static final String PROGRAM_NAME = "HIV";

  private static final String USER = "user";
  private static final Long USER_ID = 1L;
  private Rnr rnr;

  private MockHttpServletRequest request;

  @Mock
  private RequisitionService requisitionService;

  @Mock
  private RnrTemplateService rnrTemplateService;

  @Mock
  private MessageService messageService;

  @Mock
  private RegimenColumnService regimenColumnService;

  @Mock
  private RequisitionStatusChangeService requisitionStatusChangeService;

  @Mock
  private StaticReferenceDataService staticReferenceDataService;

  @Mock
  private RequisitionPermissionService requisitionPermissionService;

  @InjectMocks
  private RequisitionController controller;

  public static Matcher<RequisitionSearchCriteria> criteriaMatcher(final Long facilityId, final Long programId, final Long periodId) {
    return new ArgumentMatcher<RequisitionSearchCriteria>() {
      @Override
      public boolean matches(Object argument) {
        RequisitionSearchCriteria searchCriteria = (RequisitionSearchCriteria) argument;
        return searchCriteria.getFacilityId().equals(facilityId) && searchCriteria.getProgramId().equals(programId) && searchCriteria.getPeriodId().equals(
          periodId);
      }
    };
  }

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    rnr = new Rnr();
  }

  @Test
  public void shouldInitiateRnr() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(2L);
    ProcessingPeriod period = new ProcessingPeriod();
    Rnr initiatedRnr = new Rnr(facility, program, period);
    when(requisitionService.initiate(facility, program, USER_ID, false, null)).thenReturn(initiatedRnr);
    when(requisitionService.findM(period)).thenReturn(5);

    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1L, 2L, false, request);

    verify(requisitionService).initiate(facility, program, USER_ID, false, null);
    verify(requisitionService).findM(period);
    assertThat((Rnr) response.getBody().getData().get(RNR), is(initiatedRnr));
    assertThat((Integer) response.getBody().getData().get(NUMBER_OF_MONTHS), is(5));
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldGetRequisitionById() throws Exception {
    Rnr expectedRequisition = new Rnr();
    ProcessingPeriod period = new ProcessingPeriod();
    expectedRequisition.setPeriod(period);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(expectedRequisition);
    when(requisitionService.findM(period)).thenReturn(5);
    ResponseEntity<OpenLmisResponse> response = controller.getById(1L, request);

    assertThat((Rnr) response.getBody().getData().get(RequisitionController.RNR), is(expectedRequisition));
    assertThat((Integer) response.getBody().getData().get(NUMBER_OF_MONTHS), is(5));
    verify(requisitionService).getFullRequisitionById(1L);
    verify(requisitionService).findM(period);
  }

  @Test
  public void shouldSetCanApproveFlagTrueIfRequisitionInApprovableState() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setId(1000L);
    rnr.setStatus(RnrStatus.AUTHORIZED);

    when(requisitionService.getFullRequisitionById(1000L)).thenReturn(rnr);
    when(requisitionPermissionService.hasPermission(USER_ID, rnr, APPROVE_REQUISITION)).thenReturn(true);

    ResponseEntity<OpenLmisResponse> requisitionData = controller.getById(1000L, request);
    assertTrue((boolean) requisitionData.getBody().getData().get(CAN_APPROVE_RNR));
  }

  @Test
  public void shouldSetCanApproveFlagFalseIfRequisitionIsNotInApprovableState() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setId(1000L);
    rnr.setStatus(RnrStatus.RELEASED);

    when(requisitionService.getFullRequisitionById(1000L)).thenReturn(rnr);
    when(requisitionPermissionService.hasPermission(1L, rnr, APPROVE_REQUISITION)).thenReturn(true);

    ResponseEntity<OpenLmisResponse> requisitionData = controller.getById(1000L, request);
    assertFalse((boolean) requisitionData.getBody().getData().get(CAN_APPROVE_RNR));
  }

  @Test
  public void shouldSetCanApproveFlagFalseIfUserDoesNotHavePermission() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setId(1000L);
    rnr.setStatus(RnrStatus.AUTHORIZED);

    when(requisitionService.getFullRequisitionById(1000L)).thenReturn(rnr);
    when(requisitionPermissionService.hasPermission(1L, rnr, APPROVE_REQUISITION)).thenReturn(false);

    ResponseEntity<OpenLmisResponse> requisitionData = controller.getById(1000L, request);
    assertFalse((boolean) requisitionData.getBody().getData().get(CAN_APPROVE_RNR));
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {

    when(messageService.message(RNR_SAVE_SUCCESS)).thenReturn("R&R submitted successfully!");

    controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).initiate(new Facility(1L), new Program(2L), USER_ID, false, null);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1L, 2L, false, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    Rnr rnr = new Rnr(1L);
    whenNew(Rnr.class).withArguments(1L).thenReturn(rnr);
    Rnr submittedRnr = make(a(defaultRequisition));
    when(requisitionService.submit(rnr)).thenReturn(submittedRnr);
    OpenLmisMessage message = new OpenLmisMessage(RNR_SUBMITTED_SUCCESSFULLY);
    when(requisitionService.getSubmitMessageBasedOnSupervisoryNode(submittedRnr.getFacility(), submittedRnr.getProgram())).thenReturn(message);
    when(messageService.message(message)).thenReturn("R&R submitted successfully!");

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr.getId(), request);
    assertThat(response.getBody().getSuccessMsg(), is("R&R submitted successfully!"));
    verify(requisitionService).submit(rnr);
    verify(requisitionService).getSubmitMessageBasedOnSupervisoryNode(submittedRnr.getFacility(), submittedRnr.getProgram());
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldReturnErrorMessageIfRnrNotValid() throws Exception {
    Rnr rnr = new Rnr(1L);
    whenNew(Rnr.class).withArguments(1L).thenReturn(rnr);
    OpenLmisMessage errorMessage = new OpenLmisMessage("some error");
    when(messageService.message(errorMessage)).thenReturn("some error");
    doThrow(new DataException(errorMessage)).when(requisitionService).submit(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr.getId(), request);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveMessageAndAuthorizeRnr() throws Exception {
    String code = RequisitionService.RNR_AUTHORIZED_SUCCESSFULLY;
    String message = "R&R authorized successfully!";

    Rnr rnr = new Rnr(1L);
    whenNew(Rnr.class).withArguments(1L).thenReturn(rnr);
    Rnr authorizedRnr = make(a(defaultRequisition));
    when(requisitionService.authorize(rnr)).thenReturn(authorizedRnr);
    OpenLmisMessage openLmisMessage = new OpenLmisMessage(code);
    when(requisitionService.getAuthorizeMessageBasedOnSupervisoryNode(authorizedRnr.getFacility(),
      authorizedRnr.getProgram())).thenReturn(openLmisMessage);
    when(messageService.message(openLmisMessage)).thenReturn(message);

    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr.getId(), request);

    verify(requisitionService).authorize(rnr);
    verify(requisitionService).getAuthorizeMessageBasedOnSupervisoryNode(authorizedRnr.getFacility(), authorizedRnr.getProgram());
    assertThat(response.getBody().getSuccessMsg(), is(message));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotAuthorizeRnrAndGiveErrorMessage() throws Exception {
    String errorMessage = "some error";
    Rnr rnr = new Rnr(1L);
    whenNew(Rnr.class).withArguments(1L).thenReturn(rnr);

    doThrow(new DataException(errorMessage)).when(requisitionService).authorize(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr.getId(), request);

    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveErrorResponseIfThereIsAnyExceptionWhileSavingRnr() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).save(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveSuccessResponseIfRnrSavedSuccessfully() throws Exception {

    when(messageService.message(RNR_SAVE_SUCCESS)).thenReturn("R&R saved successfully!");

    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);
    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }

  @Test
  public void shouldReturnListOfUserSupervisedRnrForApproval() {
    final RnrDTO requisition = new RnrDTO();
    final List<RnrDTO> requisitions = new ArrayList<RnrDTO>() {{
      add(requisition);
    }};
    when(requisitionService.listForApprovalDto(USER_ID)).thenReturn(requisitions);
    final ResponseEntity<OpenLmisResponse> response = controller.listForApproval(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    final List<RnrDTO> requisitionsList = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    verify(requisitionService).listForApprovalDto(USER_ID);
  }

  @Test
  public void shouldApproveRequisitionAndTagWithModifiedBy() throws Exception {
    Rnr approvedRnr = new Rnr();
    when(requisitionService.approve(rnr, null)).thenReturn(approvedRnr);
    whenNew(Rnr.class).withArguments(rnr.getId()).thenReturn(rnr);
    OpenLmisMessage message = new OpenLmisMessage("message.key");
    when(messageService.message(message)).thenReturn("R&R saved successfully!");
    when(requisitionService.getApproveMessageBasedOnParentNode(approvedRnr)).thenReturn(message);
    final ResponseEntity<OpenLmisResponse> response = controller.approve(rnr.getId(), request);
    verify(requisitionService).approve(rnr, null);
    assertThat(rnr.getModifiedBy(), CoreMatchers.is(USER_ID));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }

  @Test
  public void shouldGiveErrorMessageWhenServiceThrowsSomeExceptionWhileApprovingAnRnr() throws Exception {
    doThrow(new DataException("some-error")).when(requisitionService).approve(rnr, null);

    ResponseEntity<OpenLmisResponse> response = controller.approve(rnr.getId(), request);

    verify(requisitionService).approve(rnr, null);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some-error"));
  }

  @Test
  public void shouldReturnAllPeriodsForInitiatingRequisition() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(6L);
    List<ProcessingPeriod> periodList = asList(processingPeriod);
    Rnr rnr = new Rnr();

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, 1L),
      with(programIdProperty, 2L),
      with(periodIdProperty, 6L),
      with(withoutLineItemFlag, true)));

    when(requisitionService.getProcessingPeriods(criteria)).thenReturn(periodList);
    when(requisitionService.getRequisitionsFor(criteria, periodList)).thenReturn(asList(rnr));

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(criteria, request);

    verify(requisitionService).getProcessingPeriods(criteria);
    assertThat((List<ProcessingPeriod>) response.getBody().getData().get(PERIODS), is(periodList));
    assertThat((List<Rnr>) response.getBody().getData().get(RNR_LIST), is(asList(rnr)));
  }

  @Test
  public void shouldReturnErrorResponseIfNoPeriodsFoundForInitiatingRequisition() throws Exception {
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria, with(facilityIdProperty, 1L), with(programIdProperty, 2L)));

    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(requisitionService).getProcessingPeriods(criteria);

    ResponseEntity<OpenLmisResponse> response =
      controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(criteria, request);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnListOfApprovedRequisitionsForConvertingToOrder() {
    ArrayList<Rnr> expectedRequisitions = new ArrayList<>();
    mockStatic(RnrDTO.class);

    String searchType = "all";
    String searchVal = "test";
    String sortBy = "program";
    String sortDirection = "asc";
    Integer pageNumber = 1;

    when(requisitionService.getNumberOfPagesOfApprovedRequisitionsForCriteria(searchType, searchVal, USER_ID,
      RightName.CONVERT_TO_ORDER)).thenReturn(1);
    when(requisitionService.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal, pageNumber,
      1, USER_ID, RightName.CONVERT_TO_ORDER, sortBy, sortDirection)).thenReturn(expectedRequisitions);
    List<RnrDTO> expectedRnrList = new ArrayList<>();
    when(RnrDTO.prepareForListApproval(expectedRequisitions)).thenReturn(expectedRnrList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.listForConvertToOrder(searchType, searchVal, pageNumber, sortBy, sortDirection, request);

    verify(requisitionService).getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal, pageNumber,
      1, USER_ID, RightName.CONVERT_TO_ORDER, sortBy, sortDirection);

    assertThat((List<RnrDTO>) responseEntity.getBody().getData().get(RNR_LIST), is(expectedRnrList));
  }

  @Test
  public void shouldGetRequisitionsForViewWithGivenFacilityIdProgramIdAndPeriodRangeAndSetUserIdInSearchCriteria() throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    String stringRangeStartDate = dateFormat.format(new Date());
    String stringDateEndDate = dateFormat.format(new Date());

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, 1L),
      with(programIdProperty, 1L),
      with(startDate, stringRangeStartDate),
      with(endDate, stringDateEndDate)));

    List<Rnr> requisitionsReturnedByService = new ArrayList<>();
    when(requisitionService.get(criteria)).thenReturn(requisitionsReturnedByService);
    mockStatic(RnrDTO.class);
    List<RnrDTO> expectedRnrList = new ArrayList<>();
    when(RnrDTO.prepareForView(requisitionsReturnedByService)).thenReturn(expectedRnrList);

    ResponseEntity<OpenLmisResponse> response = controller.getRequisitionsForView(criteria, request);

    verify(requisitionService).get(criteria);
    assertThat(criteria.getUserId(), is(USER_ID));
    List<RnrDTO> actual = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    assertThat(actual, is(expectedRnrList));
  }

  @Test
  public void shouldReturnModelAndViewForPrintingRequisitionAsPdf() {
    Long rnrId = 1L;
    Long programId = 2L;
    Program program = new Program();
    program.setId(programId);
    rnr.setProgram(program);
    ArrayList<RnrColumn> rnrTemplate = new ArrayList<>();
    when(requisitionService.getFullRequisitionById(rnrId)).thenReturn(rnr);
    when(requisitionService.getCategoryCount(rnr, true)).thenReturn(10);
    when(requisitionService.getCategoryCount(rnr, false)).thenReturn(5);
    List<LossesAndAdjustmentsType> lossesAndAdjustmentTypes = new ArrayList<>();
    when(requisitionService.getLossesAndAdjustmentsTypes()).thenReturn(lossesAndAdjustmentTypes);
    when(rnrTemplateService.fetchColumnsForRequisition(programId)).thenReturn(rnrTemplate);
    List<RegimenColumn> regimenTemplate = new ArrayList<>();
    when(regimenColumnService.getRegimenColumnsByProgramId(programId)).thenReturn(regimenTemplate);
    ModelAndView modelAndView = controller.printRequisition(rnrId);

    assertThat((Rnr) modelAndView.getModel().get(RNR), is(rnr));
    assertThat((ArrayList<LossesAndAdjustmentsType>) modelAndView.getModel().get(LOSSES_AND_ADJUSTMENT_TYPES), is(lossesAndAdjustmentTypes));
    assertThat((ArrayList<RnrColumn>) modelAndView.getModel().get(RNR_TEMPLATE), is(rnrTemplate));
  }

  @Test
  public void shouldInsertComment() throws Exception {
    Comment comment = new Comment();
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);
    when(requisitionService.getCommentsByRnrId(rnr.getId())).thenReturn(comments);

    ResponseEntity<OpenLmisResponse> response = controller.insertComment(comment, rnr.getId(), request);

    verify(requisitionService).insertComment(comment);
    assertThat((List<Comment>) response.getBody().getData().get(COMMENTS), is(comments));
    assertThat(comment.getRnrId(), is(rnr.getId()));
    assertThat(comment.getAuthor().getId(), is(USER_ID));
  }

  @Test
  public void shouldGetCommentsForARnR() throws Exception {
    List<Comment> comments = new ArrayList<>();
    when(requisitionService.getCommentsByRnrId(1L)).thenReturn(comments);

    ResponseEntity<OpenLmisResponse> response = controller.getCommentsForARnr(rnr.getId());

    verify(requisitionService).getCommentsByRnrId(rnr.getId());
    assertThat(comments, is(response.getBody().getData().get(COMMENTS)));
  }

  @Test
  public void shouldGetReferenceDataForRnr() throws Exception {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = new ArrayList<>();
    when(requisitionService.getLossesAndAdjustmentsTypes()).thenReturn(lossesAndAdjustmentsTypes);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getReferenceData();

    verify(requisitionService).getLossesAndAdjustmentsTypes();
    assertThat((List<LossesAndAdjustmentsType>) responseEntity.getBody().getData().get(LOSS_ADJUSTMENT_TYPES), is(lossesAndAdjustmentsTypes));
  }

  @Test
  public void shouldSetStatusChangesInModelForPrint() throws Exception {
    Integer numberOfMonths = 2;
    List<RequisitionStatusChange> statusChanges = new ArrayList<>();
    when(requisitionStatusChangeService.getByRnrId(1L)).thenReturn(statusChanges);
    Rnr rnr = make(a(defaultRequisition));
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(rnr);
    when(requisitionService.findM(rnr.getPeriod())).thenReturn(numberOfMonths);

    ModelAndView printModel = controller.printRequisition(1L);

    assertThat((List<RequisitionStatusChange>) printModel.getModel().get(STATUS_CHANGES), is(statusChanges));
    assertThat((Integer) printModel.getModel().get(NUMBER_OF_MONTHS), is(numberOfMonths));
  }

  private Rnr createRequisition() {
    Rnr requisition = new Rnr();
    final Facility facility = new Facility();
    facility.setCode(FACILITY_CODE);
    facility.setName(FACILITY_NAME);
    facility.setFacilityType(new FacilityType());
    facility.setGeographicZone(new GeographicZone());

    facility.getFacilityType().setId(1L);
    facility.getGeographicZone().setId(1L);

    final Program program = new Program();
    program.setName(PROGRAM_NAME);
    final ProcessingPeriod period = new ProcessingPeriod();
    period.setStartDate(new Date());
    period.setEndDate(new Date(1111232323L));
    requisition.setFacility(facility);
    requisition.setProgram(program);
    requisition.setPeriod(period);
    return requisition;
  }
}
