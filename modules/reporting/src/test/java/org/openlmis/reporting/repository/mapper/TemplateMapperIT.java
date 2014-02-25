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
import org.openlmis.reporting.model.Template;
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
public class TemplateMapperIT {

  @Autowired
  TemplateMapper templateMapper;

  @Test
  public void shouldGetById() throws Exception {
    Template template = createReportTemplate("Sample Report", "Consistency Report");

    Template returnedTemplate = templateMapper.getById(template.getId());

    assertThat(returnedTemplate.getName(), is(template.getName()));
    assertThat(returnedTemplate.getData(), is(template.getData()));
  }

  @Test
  public void shouldInsertConsistencyReportForXmlTemplateFile() throws Exception {
    String type = "Consistency Report";
    String name = "Requisition expectedReportTemplate";
    Long createdBy = 1L;
    Template expectedTemplate = new Template();
    expectedTemplate.setType(type);
    expectedTemplate.setName(name);
    List<String> parameters = new ArrayList<>();
    parameters.add("rnrId");
    expectedTemplate.setParameters(parameters);
    File file = new ClassPathResource("report1.jrxml").getFile();

    expectedTemplate.setData(readFileToByteArray(file));
    expectedTemplate.setCreatedDate(new Date());
    expectedTemplate.setCreatedBy(createdBy);

    templateMapper.insert(expectedTemplate);

    Template templateDB = templateMapper.getById(expectedTemplate.getId());

    assertThat(templateDB.getType(), is(type));
    assertThat(templateDB.getName(), is(name));
    assertThat(templateDB.getData(), is(readFileToByteArray(file)));
    assertThat(templateDB.getCreatedBy(), is(createdBy));
  }

  @Test
  public void shouldGetAllReportTemplatesAccordingToCreatedDate() throws Exception {
    Template template1 = createReportTemplate("report1", "Consistency Report");
    createReportTemplate("report2", "Print");

    List<Template> templates = templateMapper.getAllConsistencyReportTemplates();

    assertThat(templates.size(), is(8));
    assertThat(templates.get(0).getName(), is("Facilities Missing Supporting Requisition Group"));
    assertThat(templates.get(7).getName(), is("report1"));
    assertThat(templates.get(7).getId(), is(template1.getId()));
  }

  private Template createReportTemplate(String name, String type) {
    Template template = new Template();
    template.setName(name);
    template.setType(type);
    template.setData(new byte[1]);
    template.setCreatedBy(1L);
    templateMapper.insert(template);
    return template;
  }
}
