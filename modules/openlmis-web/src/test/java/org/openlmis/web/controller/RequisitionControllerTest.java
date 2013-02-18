package org.openlmis.web.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrDTO.class)
public class RequisitionControllerTest {
  public static final String FACILITY_CODE = "F14";
  public static final String FACILITY_NAME = "Facility";
  public static final String PROGRAM_NAME = "HIV";
  private static final String USER = "user";
  private static final Integer USER_ID = 1;

  private RequisitionService requisitionService;
  private MockHttpServletRequest request;
  private RequisitionController controller;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    requisitionService = mock(RequisitionService.class);
    controller = new RequisitionController(requisitionService);
    rnr = new Rnr();
  }

  @Test
  public void shouldInitiateRnr() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, 3, request);

    verify(requisitionService).initiate(1, 2, 3, USER_ID);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldGetRnrByFacilityProgramAndPeriodIfExists() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, 3);

    verify(requisitionService).get(argThat(facilityMatcher(1)), argThat(programMatcher(2)), argThat(periodMatcher(3)));
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }





  @Test
  public void shouldGetRnrByIdIfExists() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.getRnrForApprovalById(1, request);

    verify(requisitionService).getRnrForApprovalById(1, USER_ID);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldReturnErrorResponseIfServiceThrowsException() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).getRnrForApprovalById(1, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.getRnrForApprovalById(1, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {
    controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).initiate(1, 2, null, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, null, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    Facility facility = new Facility(1);
    whenNew(Facility.class).withArguments(1).thenReturn(facility);
    Program program = new Program(2);
    whenNew(Program.class).withArguments(2).thenReturn(program);

    when(requisitionService.get(facility, program, null)).thenReturn(expectedRnr);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, null);
    assertThat((Rnr) response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    when(requisitionService.submit(rnr)).thenReturn(new OpenLmisMessage("test.msg.key"));
    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getBody().getSuccessMsg(), is("test.msg.key"));
    verify(requisitionService).submit(rnr);
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldReturnErrorMessageIfRnrNotValid() throws Exception {
    doThrow(new DataException(new OpenLmisMessage("some error"))).when(requisitionService).submit(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveMessageAndAuthorizeRnr() throws Exception {
    String code = RequisitionService.RNR_AUTHORIZED_SUCCESSFULLY;
    String message = "R&R authorized successfully!";

    when(requisitionService.authorize(rnr)).thenReturn(new OpenLmisMessage(code));

    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    verify(requisitionService).authorize(rnr);
    assertThat(response.getBody().getSuccessMsg(), is(message));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotAuthorizeRnrAndGiveErrorMessage() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).authorize(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
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
    when(requisitionService.approve(rnr)).thenReturn(new OpenLmisMessage("some message"));
    final ResponseEntity<OpenLmisResponse> response = controller.approve(rnr, rnr.getId(), request);
    verify(requisitionService).approve(rnr);
    assertThat(rnr.getModifiedBy(), CoreMatchers.is(USER_ID));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("some message"));
  }

  @Test
  public void shouldGiveErrorMessageWhenServiceThrowsSomeExceptionWhileApprovingAnRnr() throws Exception {
    doThrow(new DataException("some-error")).when(requisitionService).approve(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.approve(rnr, rnr.getId(), request);

    verify(requisitionService).approve(rnr);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some-error"));
  }

  @Test
  public void shouldReturnAllPeriodsForInitiatingRequisition() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(6);
    List<ProcessingPeriod> periodList = Arrays.asList(processingPeriod);
    Rnr rnr = new Rnr();

    Facility facility = new Facility(1);
    whenNew(Facility.class).withArguments(1).thenReturn(facility);
    Program program = new Program(2);
    whenNew(Program.class).withArguments(2).thenReturn(program);
    when(requisitionService.get(facility, program, processingPeriod)).thenReturn(rnr);

    when(requisitionService.getAllPeriodsForInitiatingRequisition(1, 2)).thenReturn(periodList);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    verify(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);
    assertThat((List<ProcessingPeriod>) response.getBody().getData().get(PERIODS), is(periodList));
    assertThat((Rnr) response.getBody().getData().get(RNR), is(rnr));
  }

  @Test
  public void shouldReturnErrorResponseIfNoPeriodsFoundForInitiatingRequisition() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnListOfApprovedRequisitionsForConvertingToOrder(){
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
  public void shouldGetRequisitionsForViewWithGivenFacilityIdProgramIdAndPeriodRange() throws Exception {
    Integer facilityId = 1;
    List<Integer> programIds = asList(1, 2);
    Date periodStartDate = DateTime.parse("2013-02-01").toDate();
    Date periodEndDate = DateTime.parse("2013-02-14").toDate();
    List<Rnr> requisitionsReturnedByService = new ArrayList<>();
    Facility facility = new Facility(1);
    whenNew(Facility.class).withArguments(1).thenReturn(facility);
    Program program = new Program(2);
    whenNew(Program.class).withArguments(2).thenReturn(program);

    when(requisitionService.get(facility, program, periodStartDate, periodEndDate)).thenReturn(requisitionsReturnedByService);
    mockStatic(RnrDTO.class);
    List<RnrDTO> expectedRnrList = mock(List.class);
    when(RnrDTO.prepareForView(requisitionsReturnedByService)).thenReturn(expectedRnrList);

    ResponseEntity<OpenLmisResponse> response = controller.getRequisitionsForView(facility.getId(), program.getId(), periodStartDate, periodEndDate);

    verify(requisitionService).get(facility, program, periodStartDate, periodEndDate);
    List<RnrDTO> actual = (List<RnrDTO>) response.getBody().getData().get(RNR_LIST);
    assertThat(actual, is(expectedRnrList));
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

  private Matcher<Program> programMatcher(final int id) {
    return new ArgumentMatcher<Program>() {
      @Override
      public boolean matches(Object argument) {
        Program program = (Program) argument;
        return program.getId().equals(id);
      }
    };
  }

  private Matcher<ProcessingPeriod> periodMatcher(final int id) {
    return new ArgumentMatcher<ProcessingPeriod>() {
      @Override
      public boolean matches(Object argument) {
        ProcessingPeriod period = (ProcessingPeriod) argument;
        return period.getId().equals(id);
      }
    };
  }

  private Matcher<Facility> facilityMatcher(final int id) {
    return new ArgumentMatcher<Facility>() {
      @Override
      public boolean matches(Object argument) {
        Facility facility = (Facility) argument;
        return facility.getId().equals(id);
      }
    };
  }

}

