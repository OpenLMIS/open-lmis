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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.service.BudgetFileTemplateService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class BudgetFileTemplateControllerTest {

  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  @Mock
  BudgetFileTemplateService budgetFileTemplateService;

  @InjectMocks
  BudgetFileTemplateController controller;
  private Long userId = 111L;

  @Before
  public void setUp() throws Exception {
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldGetBudgetFileTemplate() {
    EDIFileTemplate expectedBudgetFileTemplate = new EDIFileTemplate();

    when(budgetFileTemplateService.get()).thenReturn(expectedBudgetFileTemplate);

    ResponseEntity<OpenLmisResponse> response = controller.get();

    assertThat((EDIFileTemplate) response.getBody().getData().get("budget_template"), is(expectedBudgetFileTemplate));
    verify(budgetFileTemplateService).get();
  }

  @Test
  public void shouldUpdateBudgetFileTemplate() {
    EDIFileColumn ediFileColumn1 = new EDIFileColumn("name", "Label", false, true, 1, "dd/mm/yy");
    EDIFileColumn ediFileColumn2 = new EDIFileColumn("name", "Label", false, true, 2, "dd/mm/yy");
    EDIFileTemplate budgetFileTemplate = new EDIFileTemplate(
      new EDIConfiguration(true),
      asList(ediFileColumn1, ediFileColumn2));

    ResponseEntity<OpenLmisResponse> response = controller.update(budgetFileTemplate, httpServletRequest);

    assertThat(response.getStatusCode(), is(OK));
    assertThat(response.getBody().getSuccessMsg(), is("budget.file.configuration.success"));
    assertThat(budgetFileTemplate.getConfiguration().getModifiedBy(), is(userId));
    for (EDIFileColumn column : budgetFileTemplate.getColumns()) {
      assertThat(column.getModifiedBy(), is(userId));
    }
    verify(budgetFileTemplateService).update(budgetFileTemplate);
  }
}
