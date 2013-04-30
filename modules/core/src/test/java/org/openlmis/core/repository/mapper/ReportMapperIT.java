/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ReportTemplate;
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
    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName("Sample Report");
    reportTemplate.setData(new byte[1]);
    reportTemplate.setParameters("SampleParameters");
    reportTemplate.setModifiedBy(1);
    Date currentTimeStamp = new Date();
    reportTemplate.setModifiedDate(currentTimeStamp);

    reportMapper.insert(reportTemplate);

    ReportTemplate returnedTemplate = reportMapper.getByName("Sample Report");

    assertThat(returnedTemplate, is(reportTemplate));
  }

  @Test
  public void shouldInsertReportForXmlTemplateFile() throws Exception {

    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName("Requisition reportTemplate");
    reportTemplate.setParameters("<rnrId, Integer>");
    File file = new ClassPathResource("report1.jrxml").getFile();

    reportTemplate.setData(readFileToByteArray(file));
    reportTemplate.setModifiedDate(new Date());
    reportTemplate.setModifiedBy(1);

    reportMapper.insert(reportTemplate);
  }
}
