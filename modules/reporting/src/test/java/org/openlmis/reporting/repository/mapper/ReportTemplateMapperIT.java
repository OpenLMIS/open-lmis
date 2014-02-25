/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.reporting.model.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-reporting.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ReportTemplateMapperIT {

  @Autowired
  ReportTemplateMapper reportTemplateMapper;

  @Test
  public void shouldGetById() throws Exception {
    ReportTemplate reportTemplate = createReportTemplate("Sample Report", "Consistency Report");

    ReportTemplate returnedTemplate = reportTemplateMapper.getById(reportTemplate.getId());

    assertThat(returnedTemplate.getName(), is(reportTemplate.getName()));
    assertThat(returnedTemplate.getData(), is(reportTemplate.getData()));
  }

  @Test
  public void shouldInsertConsistencyReportForXmlTemplateFile() throws Exception {
    String type = "Consistency Report";
    String name = "Requisition expectedReportTemplate";
    Long createdBy = 1L;
    ReportTemplate expectedReportTemplate = new ReportTemplate();
    expectedReportTemplate.setType(type);
    expectedReportTemplate.setName(name);
    List<String> parameters = new ArrayList<>();
    parameters.add("rnrId");
    expectedReportTemplate.setParameters(parameters);
    File file = new ClassPathResource("report1.jrxml").getFile();

    expectedReportTemplate.setData(readFileToByteArray(file));
    expectedReportTemplate.setCreatedDate(new Date());
    expectedReportTemplate.setCreatedBy(createdBy);

    reportTemplateMapper.insert(expectedReportTemplate);

    ReportTemplate reportTemplateDB = reportTemplateMapper.getById(expectedReportTemplate.getId());

    assertThat(reportTemplateDB.getType(), is(type));
    assertThat(reportTemplateDB.getName(), is(name));
    assertThat(reportTemplateDB.getData(), is(readFileToByteArray(file)));
    assertThat(reportTemplateDB.getCreatedBy(), is(createdBy));
  }

  @Test
  public void shouldGetAllReportTemplatesAccordingToCreatedDate() throws Exception {
    ReportTemplate reportTemplate1 = createReportTemplate("report1", "Consistency Report");
    createReportTemplate("report2", "Print");

    List<ReportTemplate> result = reportTemplateMapper.getAllConsistencyReportTemplates();

    assertThat(result.size(), is(8));
    assertThat(result.get(0).getName(), is("Facilities Missing Supporting Requisition Group"));
    assertThat(result.get(7).getName(), is("report1"));
    assertThat(result.get(7).getId(), is(reportTemplate1.getId()));
  }

  private ReportTemplate createReportTemplate(String name, String type) {
    ReportTemplate reportTemplate = new ReportTemplate();
    reportTemplate.setName(name);
    reportTemplate.setType(type);
    reportTemplate.setData(new byte[1]);
    List<String> parameterList = new ArrayList<>();
    parameterList.add("rnrId");
    reportTemplate.setParameters(parameterList);
    reportTemplate.setModifiedBy(1L);
    Date currentTimeStamp = new Date();
    reportTemplate.setModifiedDate(currentTimeStamp);
    reportTemplateMapper.insert(reportTemplate);
    return reportTemplate;
  }
}
