/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.springframework.mock.web.MockMultipartFile;

import static org.openlmis.core.domain.ReportTemplate.CREATE_REPORT_ERROR_FILE_EMPTY;
import static org.openlmis.core.domain.ReportTemplate.CREATE_REPORT_ERROR_FILE_TYPE;

public class ReportTemplateTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfFileNotOfTypeJasperXML() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(CREATE_REPORT_ERROR_FILE_TYPE);
    new ReportTemplate("report",new MockMultipartFile("report.pdf", new byte[1]), 1L);
  }

  @Test
  public void shouldThrowErrorIfFileEmpty() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(CREATE_REPORT_ERROR_FILE_EMPTY);
    MockMultipartFile file = new MockMultipartFile("report.jrxml", "report.jrxml","",new byte[0]);

    new ReportTemplate("report", file, 1L);
  }

  @Test
  public void shouldThrowErrorIfFileNotPresent() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(ReportTemplate.CREATE_REPORT_ERROR_FILE_MISSING);
    new ReportTemplate("report", null, 1L);
  }
}
