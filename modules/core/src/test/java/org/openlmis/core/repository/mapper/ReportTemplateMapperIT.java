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
import java.util.List;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ReportTemplateMapperIT {

  @Autowired
  ReportTemplateMapper reportTemplateMapper;

  @Test
  public void shouldGetById() throws Exception {
    ReportTemplate reportTemplate = createReportTemplate("Sample Report");

    ReportTemplate returnedTemplate = reportTemplateMapper.getByName("Sample Report");

    assertThat(returnedTemplate, is(reportTemplate));
  }

  private ReportTemplate createReportTemplate(String name) {
    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName(name);
    reportTemplate.setData(new byte[1]);
    reportTemplate.setParameters("SampleParameters");
    reportTemplate.setModifiedBy(1);
    Date currentTimeStamp = new Date();
    reportTemplate.setModifiedDate(currentTimeStamp);
    reportTemplateMapper.insert(reportTemplate);
    return reportTemplate;
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

    reportTemplateMapper.insert(reportTemplate);
  }

  @Test
  public void shouldGetAllReportTemplates() throws Exception {
    ReportTemplate reportTemplate1 = createReportTemplate("report1");
    ReportTemplate reportTemplate2 = createReportTemplate("report2");

    List<ReportTemplate> result = reportTemplateMapper.getAll();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).getName(), is("report1"));
    assertThat(result.get(0).getId(), is(reportTemplate1.getId()));
//    assertThat(result, hasItem(reportTemplate1));
//    assertThat(result, hasItem(reportTemplate2));
  }
}
