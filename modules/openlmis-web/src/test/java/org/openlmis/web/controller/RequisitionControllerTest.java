/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.web.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RegimenColumnService;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RequisitionStatusChangeService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.service.RequisitionService.RNR_SUBMITTED_SUCCESSFULLY;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({RnrDTO.class, RequisitionController.class})
public class RequisitionControllerTest {
  public static final String FACILITY_CODE = "F14";
  public static final String FACILITY_NAME = "Facility";
  public static final String PROGRAM_NAME = "HIV";
  private static final String USER = "user";
  private static final Long USER_ID = 1L;

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

  private MockHttpServletRequest request;

  @InjectMocks
  private RequisitionController controller;

  private Rnr rnr;


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
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1L, 2L, 3L, false, request);

    verify(requisitionService).initiate(1L, 2L, 3L, USER_ID, false);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldGetRnrByFacilityProgramAndPeriodIfExists() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria();
    criteria.setFacilityId(1L);
    criteria.setProgramId(2L);
    criteria.setPeriodId(3L);
    when(requisitionService.get(criteria)).thenReturn(asList(rnr));

    ResponseEntity<OpenLmisResponse> response = controller.get(criteria, request);

    verify(requisitionService).get(argThat(criteriaMatcher(1L, 2L, 3L)));
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }


  @Test
  public void shouldGetRequisitionById() throws Exception {
    Rnr expectedRequisition = new Rnr();
    Mockito.when(requisitionService.getFullRequisitionById(1L)).thenReturn(expectedRequisition);
    ResponseEntity<OpenLmisResponse> response = controller.getById(1L);

    assertThat((Rnr) response.getBody().getData().get(RequisitionController.RNR), is(expectedRequisition));
    verify(requisitionService).getFullRequisitionById(1L);
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
    doThrow(new DataException(errorMessage)).when(requisitionService).initiate(1L, 2L, null, USER_ID, false);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1L, 2L, null, false, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    Facility facility = new Facility(1L);
    whenNew(Facility.class).withArguments(1L).thenReturn(facility);
    Program program = new Program(2L);

    whenNew(Program.class).withArguments(2L).thenReturn(program);
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facility.getId(), program.getId(), null);
    when(requisitionService.get(criteria)).thenReturn(asList(expectedRnr));

    ResponseEntity<OpenLmisResponse> response = controller.get(criteria, request);

    assertThat((Rnr) response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    Rnr rnr = new Rnr(1L);
    whenNew(Rnr.class).withArguments(1L).thenReturn(rnr);
    Rnr submittedRnr = make(a(defaultRnr));
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
    Rnr authorizedRnr = make(a(defaultRnr));
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
    final Rnr requisition = createRequisition();
    final List<Rnr> requisitions = new ArrayList<Rnr>() {{
      add(requisition);
    }};
    when(requisitionService.listForApproval(USER_ID)).thenReturn(requisitions);
    final ResponseEntity<OpenLmisResponse> response = controller.listForApproval(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    final List<RnrDTO> requisitionsList = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    assertThat(requisitionsList.get(0).getFacilityName(), is(FACILITY_NAME));
    assertThat(requisitionsList.get(0).getFacilityCode(), is(FACILITY_CODE));
    assertThat(requisitionsList.get(0).getProgramName(), is(PROGRAM_NAME));
    verify(requisitionService).listForApproval(USER_ID);
  }

  @Test
  public void shouldApproveRequisitionAndTagWithModifiedBy() throws Exception {
    Rnr approvedrnr = new Rnr();
    when(requisitionService.approve(rnr)).thenReturn(approvedrnr);
    whenNew(Rnr.class).withArguments(rnr.getId()).thenReturn(rnr);
    OpenLmisMessage message = new OpenLmisMessage("message.key");
    when(messageService.message(message)).thenReturn("R&R saved successfully!");
    when(requisitionService.getApproveMessageBasedOnParentNode(approvedrnr)).thenReturn(message);
    final ResponseEntity<OpenLmisResponse> response = controller.approve(rnr.getId(), request);
    verify(requisitionService).approve(rnr);
    assertThat(rnr.getModifiedBy(), CoreMatchers.is(USER_ID));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }

  @Test
  public void shouldGiveErrorMessageWhenServiceThrowsSomeExceptionWhileApprovingAnRnr() throws Exception {
    doThrow(new DataException("some-error")).when(requisitionService).approve(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.approve(rnr.getId(), request);

    verify(requisitionService).approve(rnr);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some-error"));
  }

  @Test
  public void shouldReturnAllPeriodsForInitiatingRequisition() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(6L);
    List<ProcessingPeriod> periodList = asList(processingPeriod);
    Rnr rnr = new Rnr();

    Facility facility = new Facility(1L);
    whenNew(Facility.class).withArguments(1L).thenReturn(facility);
    Program program = new Program(2L);
    whenNew(Program.class).withArguments(2L).thenReturn(program);
    boolean withoutLineItems = true;
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facility.getId(), program.getId(),
      processingPeriod.getId(), withoutLineItems);
    when(requisitionService.get(criteria)).thenReturn(asList(rnr));

    when(requisitionService.getAllPeriodsForInitiatingRequisition(criteria)).thenReturn(periodList);

    ResponseEntity<OpenLmisResponse> response =
      controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(criteria);

    verify(requisitionService).getAllPeriodsForInitiatingRequisition(criteria);
    assertThat((List<ProcessingPeriod>) response.getBody().getData().get(PERIODS), is(periodList));
    assertThat((Rnr) response.getBody().getData().get(RNR), is(rnr));
  }

  @Test
  public void shouldReturnErrorResponseIfNoPeriodsFoundForInitiatingRequisition() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1L, 2L);
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(requisitionService).
      getAllPeriodsForInitiatingRequisition(criteria);

    ResponseEntity<OpenLmisResponse> response =
      controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(criteria);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnListOfApprovedRequisitionsForConvertingToOrder() {
    ArrayList<Rnr> expectedRequisitions = new ArrayList<>();
    mockStatic(RnrDTO.class);
    when(requisitionService.getApprovedRequisitions()).thenReturn(expectedRequisitions);
    List<RnrDTO> expectedRnrList = new ArrayList<>();
    when(RnrDTO.prepareForListApproval(expectedRequisitions)).thenReturn(expectedRnrList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.listForConvertToOrder();

    verify(requisitionService).getApprovedRequisitions();
    assertThat((List<RnrDTO>) responseEntity.getBody().getData().get(RNR_LIST), is(expectedRnrList));
  }

  @Test
  public void shouldGetRequisitionsForViewWithGivenFacilityIdProgramIdAndPeriodRangeAndSetUserIdInSearchCriteria() throws Exception {
    Date dateRangeStart = new Date();
    Date dateRangeEnd = new Date();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1L, 1L, dateRangeStart, dateRangeEnd);
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
    List<Comment> comments = new ArrayList<Comment>();
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
    assertThat((List<LossesAndAdjustmentsType>) responseEntity.getBody().getData().get("lossAdjustmentTypes"), is(lossesAndAdjustmentsTypes));
  }

  @Test
  public void shouldSetStatusChangesInModelForPrint() throws Exception {
    List<RequisitionStatusChange> statusChanges = new ArrayList<>();
    when(requisitionStatusChangeService.getByRnrId(1L)).thenReturn(statusChanges);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(make(a(defaultRnr)));

    ModelAndView printModel = controller.printRequisition(1L);

    assertThat((List<RequisitionStatusChange>) printModel.getModel().get("statusChanges"), is(statusChanges));
  }

  private Rnr createRequisition() {
    Rnr requisition = new Rnr();
    final Facility facility = new Facility();
    facility.setCode(FACILITY_CODE);
    facility.setName(FACILITY_NAME);
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
}

