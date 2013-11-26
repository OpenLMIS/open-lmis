/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright Â© 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Â 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.Â  If not, see http://www.gnu.org/licenses. Â For additional information contact info@OpenLMIS.org.Â 
 */

package org.openlmis.restapi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class RequisitionValidatorTest {


  @Mock
  private RequisitionService requisitionService;

  @Mock
  private MessageService messageService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private RequisitionValidator requisitionValidator;


  @Test
  public void shouldSkipPeriodValidationForVirtualFacility() throws Exception {
    Facility facility = new Facility();

    facility.setVirtualFacility(true);

    requisitionValidator.validatePeriod(facility, new Program());
  }

  @Test
  public void shouldNotThrowErrorForIfPeriodMatchesCurrentPeriodForNonVirtualFacility() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod(1L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();

    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(processingPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(processingPeriod);

    requisitionValidator.validatePeriod(reportingFacility, reportingProgram);
  }

  @Test
  public void shouldThrowErrorIfCurrentPeriodIsDifferentFromNextEligiblePeriod() throws Exception {
    ProcessingPeriod currentPeriod = new ProcessingPeriod(1L);
    ProcessingPeriod nextEligiblePeriod = new ProcessingPeriod(2L);
    Facility reportingFacility = new Facility();
    Program reportingProgram = new Program();

    when(requisitionService.getCurrentPeriod(any(RequisitionSearchCriteria.class))).thenReturn(currentPeriod);
    when(requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram)).thenReturn(nextEligiblePeriod);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.rnr.previous.not.filled");

    requisitionValidator.validatePeriod(reportingFacility, reportingProgram);
  }


  @Test
  public void shouldPassProductValidationIfNoneExist() throws Exception {

    Report report = new Report();
    Rnr savedRequisition = new Rnr();

    requisitionValidator.validateProducts(report.getProducts(), savedRequisition);

  }

  @Test
  public void shouldPassProductValidationIfAllProductsAreValid() throws Exception {
    Report report = new Report();
    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    RnrLineItem rnrLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P12")));

    report.setProducts(asList(rnrLineItem1, rnrLineItem2));
    Rnr savedRequisition = mock(Rnr.class);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem1)).thenReturn(rnrLineItem1);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem2)).thenReturn(rnrLineItem2);
    when(savedRequisition.getNonSkippedLineItems()).thenReturn(asList(rnrLineItem1, rnrLineItem2));

    requisitionValidator.validateProducts(report.getProducts(), savedRequisition);

    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem1);
    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem2);
  }

  @Test
  public void shouldFailProductValidationIfReportedProductDoesNotBelongToRequisition() throws Exception {
    Report report = new Report();
    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    RnrLineItem rnrLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P12")));

    report.setProducts(asList(rnrLineItem1, rnrLineItem2));
    Rnr savedRequisition = mock(Rnr.class);
    when(savedRequisition.getNonSkippedLineItems()).thenReturn(asList(new RnrLineItem(), new RnrLineItem()));
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem1)).thenReturn(rnrLineItem1);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem2)).thenReturn(null);
    when(messageService.message("invalid.product.codes", "[P12]")).thenReturn("invalid products [P12]");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("invalid products [P12]");

    requisitionValidator.validateProducts(report.getProducts(), savedRequisition);

    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem1);
    verify(savedRequisition).findCorrespondingLineItem(rnrLineItem2);
  }

  @Test
  public void shouldThrowErrorIfSkippedProductReportedForApproval() throws Exception {
    Report report = new Report();
    RnrLineItem rnrLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P11")));
    RnrLineItem skippedLineItem1 = make(a(defaultRnrLineItem, with(productCode, "P12"), with(skipped, true)));
    RnrLineItem rnrLineItem3 = make(a(defaultRnrLineItem, with(productCode, "P13")));
    RnrLineItem skippedLineItem2 = make(a(defaultRnrLineItem, with(productCode, "P14"), with(skipped, true)));

    report.setProducts(asList(skippedLineItem1, skippedLineItem2));

    Rnr savedRequisition = mock(Rnr.class);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem1)).thenReturn(rnrLineItem1);
    when(savedRequisition.findCorrespondingLineItem(skippedLineItem1)).thenReturn(skippedLineItem1);
    when(savedRequisition.findCorrespondingLineItem(rnrLineItem3)).thenReturn(rnrLineItem3);
    when(savedRequisition.findCorrespondingLineItem(skippedLineItem2)).thenReturn(skippedLineItem2);
    when(savedRequisition.getNonSkippedLineItems()).thenReturn(asList(rnrLineItem1, rnrLineItem3));
    when(messageService.message("invalid.product.codes", "[P12, P14]")).thenReturn("invalid products [P12, P14]");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("invalid products [P12, P14]");

    requisitionValidator.validateProducts(report.getProducts(), savedRequisition);
  }

}
