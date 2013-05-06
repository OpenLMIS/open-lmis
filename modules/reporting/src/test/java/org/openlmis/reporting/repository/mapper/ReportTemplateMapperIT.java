/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.reporting.model.ReportTemplate;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-reporting.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ReportTemplateMapperIT {

  @Autowired
  ReportTemplateMapper reportTemplateMapper;

  @Test
  public void shouldGetById() throws Exception {
    ReportTemplate reportTemplate = createReportTemplate("Sample Report");

    ReportTemplate returnedTemplate = reportTemplateMapper.getByName("Sample Report");

    Assert.assertThat(returnedTemplate, is(reportTemplate));
  }

  private ReportTemplate createReportTemplate(String name) {
    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName(name);
    reportTemplate.setData(new byte[1]);
    reportTemplate.setParameters("SampleParameters");
    reportTemplate.setModifiedBy(1L);
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
    reportTemplate.setModifiedBy(1L);

    reportTemplateMapper.insert(reportTemplate);
  }

  @Test
  public void shouldGetAllReportTemplates() throws Exception {
    ReportTemplate reportTemplate1 = createReportTemplate("report1");
    createReportTemplate("report2");

    List<ReportTemplate> result = reportTemplateMapper.getAll();

    Assert.assertThat(result.size(), CoreMatchers.is(2));
    Assert.assertThat(result.get(0).getName(), CoreMatchers.is("report1"));
    Assert.assertThat(result.get(0).getId(), is(reportTemplate1.getId()));
  }
}
