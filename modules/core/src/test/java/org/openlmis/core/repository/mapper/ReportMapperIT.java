/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Report;
import org.openlmis.core.repository.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ReportMapperIT {

  @Autowired
  ReportMapper reportMapper;

  @Test
  public void shouldGetById() throws Exception {
    Report report = new Report();
    report.setName("Sample Report");
    report.setData(new byte[1]);
    report.setParameters("SampleParameters");
    report.setModifiedBy(1);
    Date currentTimeStamp = new Date();
    report.setModifiedDate(currentTimeStamp);

    reportMapper.insertReport(report);

    Report returnedReport = reportMapper.getByName("Sample Report");

    assertThat(returnedReport, is(report));
  }

  @Test
  public void shouldInsertReportForXmlTemplateFile() throws Exception {

    Report report = new Report();
    report.setName("Requisition report");
    report.setParameters("<rnrId, Integer>");
    File file = new ClassPathResource("report1.jrxml").getFile();

    report.setData(readFileToByteArray(file));
    report.setModifiedDate(new Date());
    report.setModifiedBy(1);

    reportMapper.insertReport(report);
  }
}
