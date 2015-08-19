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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class ProcessingPeriodControllerTest {

  @Rule
  public ExpectedException exException = none();

  @Mock
  private ProcessingScheduleService service;
  @Mock
  private MessageService messageService;

  @InjectMocks
  private ProcessingPeriodController controller;

  private MockHttpServletRequest request = new MockHttpServletRequest();

  private final Long SCHEDULE_ID = 123L;
  private final Long USER_ID = 5L;
  private final Long PROCESSING_PERIOD_ID = 1L;

  @Before
  public void setUp() throws Exception {
    MockHttpSession session = new MockHttpSession();
    request.setSession(session);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> mockedList = new ArrayList<>();
    when(service.getAllPeriods(SCHEDULE_ID)).thenReturn(mockedList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getAll(SCHEDULE_ID);

    List<ProcessingPeriod> periodList = (List<ProcessingPeriod>) responseEntity.getBody().getData().get(ProcessingPeriodController.PERIODS);
    verify(service).getAllPeriods(SCHEDULE_ID);
    assertThat(periodList, is(mockedList));
  }

  @Test
  public void shouldSaveAPeriodForGivenSchedule() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();

    when(messageService.message("message.period.added.success")).thenReturn("Period added successfully");
    ResponseEntity<OpenLmisResponse> responseEntity = controller.save(SCHEDULE_ID, processingPeriod, request);

    verify(service).savePeriod(processingPeriod);
    assertThat(processingPeriod.getScheduleId(), is(SCHEDULE_ID));
    assertThat(processingPeriod.getModifiedBy(), is(USER_ID));
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody().getSuccessMsg(), is("Period added successfully"));
  }

  @Test
  public void shouldGiveErrorResponseWhenCreatingANewPeriodResultsThrowsDataException() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    doThrow(new DataException("error-message")).when(service).savePeriod(processingPeriod);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.save(SCHEDULE_ID, processingPeriod, request);

    verify(service).savePeriod(processingPeriod);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(responseEntity.getBody().getErrorMsg(), is("error-message"));
  }

  @Test
  public void shouldReturnErrorResponseIfStartDateLessThanOrEqualToCurrentDateWhenDeletingAPeriod() {
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(service).deletePeriod(PROCESSING_PERIOD_ID);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.delete(PROCESSING_PERIOD_ID);

    verify(service).deletePeriod(PROCESSING_PERIOD_ID);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(responseEntity.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldDeletePeriodIfStartDateGreaterThanCurrentDate() {
    when(messageService.message("message.period.deleted.success")).thenReturn("Period deleted successfully");
    ResponseEntity<OpenLmisResponse> responseEntity = controller.delete(PROCESSING_PERIOD_ID);

    verify(service).deletePeriod(PROCESSING_PERIOD_ID);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody().getSuccessMsg(), is("Period deleted successfully"));

  }

  @Test
  public void shouldReturnNextStartDateWithPeriodListIfListNotEmpty() throws Exception {
    List<ProcessingPeriod> periods = new ArrayList<ProcessingPeriod>() {{
      add(new ProcessingPeriod(2l, now().minusDays(4).toDate(), now().toDate(), 1, "blah"));
    }};

    when(service.getAllPeriods(3l)).thenReturn(periods);

    ResponseEntity<OpenLmisResponse> response = controller.getAll(3l);
    assertThat((String) response.getBody().getData().get("nextStartDate"), is(forPattern("yyyy-MM-dd").print(now().plusDays(1))));
  }

  @Test
  public void shouldGetPeriodById() throws Exception {
    ProcessingPeriod period = new ProcessingPeriod();
    when(service.getPeriodById(1L)).thenReturn(period);

    ResponseEntity<OpenLmisResponse> response = controller.get(1L);

    verify(service).getPeriodById(1L);
    assertThat((ProcessingPeriod) response.getBody().getData().get("period"), is(period));
  }
}
