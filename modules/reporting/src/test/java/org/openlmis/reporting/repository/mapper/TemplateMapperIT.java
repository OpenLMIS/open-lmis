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
import org.openlmis.core.domain.RightType;
import org.openlmis.core.domain.Role;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  @Autowired
  ReportRightMapper reportRightMapper;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  QueryExecutor queryExecutor;

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
    List<TemplateParameter> parameters = new ArrayList<>();
    parameters.add(new TemplateParameter());
    expectedTemplate.setParameters(parameters);
    File file = new ClassPathResource("report1.jrxml").getFile();

    expectedTemplate.setData(readFileToByteArray(file));
    expectedTemplate.setCreatedDate(new Date());
    expectedTemplate.setCreatedBy(createdBy);
    expectedTemplate.setDescription("description");

    templateMapper.insert(expectedTemplate);

    Template templateDB = templateMapper.getById(expectedTemplate.getId());

    assertThat(templateDB.getType(), is(type));
    assertThat(templateDB.getName(), is(name));
    assertThat(templateDB.getData(), is(readFileToByteArray(file)));
    assertThat(templateDB.getCreatedBy(), is(createdBy));
    assertThat(templateDB.getDescription(), is("description"));
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

  @Test
  public void shouldGetByName() throws Exception {
    Template template = createReportTemplate("Sample Report", "Consistency Report");

    Template returnedTemplate = templateMapper.getByName("sample report");

    assertThat(returnedTemplate.getName(), is(template.getName()));
    assertThat(returnedTemplate.getData(), is(template.getData()));
  }

  @Test
  public void shouldInsertTemplateParameteres() throws Exception {
    String type = "REPORTING";
    String name = "Requisition Report Template";
    Long createdBy = 1L;
    Template template = new Template();
    template.setType(type);
    template.setName(name);
    template.setCreatedBy(createdBy);
    template.setDescription("description");
    template.setData(new byte[]{'a'});
    templateMapper.insert(template);

    TemplateParameter templateParameter = new TemplateParameter();
    templateParameter.setName(name);
    templateParameter.setDescription("desc");
    templateParameter.setDisplayName("Template");
    templateParameter.setDefaultValue("value");
    templateParameter.setCreatedBy(createdBy);
    templateParameter.setTemplateId(template.getId());
    templateParameter.setDataType("String");

    templateMapper.insertParameter(templateParameter);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM template_parameters where templateId=?", template.getId());
    resultSet.next();

    assertThat(resultSet.getString("name"), is("Requisition Report Template"));
    assertThat(resultSet.getString("displayName"), is("Template"));
    assertThat(resultSet.getString("description"), is("desc"));
    assertThat(resultSet.getString("defaultValue"), is("value"));
    assertThat(resultSet.getString("dataType"), is("String"));
    assertThat(resultSet.getLong("createdBy"), is(createdBy));
  }

  @Test
  public void shouldGetAllTemplatesForUserOrderedByName() throws SQLException {
    Long userId = 1L;
    Template reportTemplate1 = createReportTemplate("DFacility Template1", RightType.REPORTING.toString());
    Template reportTemplate2 = createReportTemplate("AFacility Template2", RightType.REPORTING.toString());
    Template reportTemplate3 = createReportTemplate("CFacility Template3", RightType.REPORTING.toString());
    Template reportTemplate4 = createReportTemplate("EFacility Template4", RightType.REPORTING.toString());

    roleRightsMapper.insertRight(reportTemplate1.getName(), RightType.REPORTING);
    roleRightsMapper.insertRight(reportTemplate2.getName(), RightType.REPORTING);
    roleRightsMapper.insertRight(reportTemplate3.getName(), RightType.REPORTING);
    roleRightsMapper.insertRight(reportTemplate4.getName(), RightType.REPORTING);

    reportRightMapper.insert(reportTemplate1);
    reportRightMapper.insert(reportTemplate2);
    reportRightMapper.insert(reportTemplate3);
    reportRightMapper.insert(reportTemplate4);

    Role role = new Role();
    role.setName("Reporting User Role");
    roleRightsMapper.insertRole(role);
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), reportTemplate1.getName());
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), reportTemplate2.getName());
    queryExecutor.executeUpdate("INSERT INTO role_rights(roleId, rightName) VALUES (?,?)", role.getId(), reportTemplate3.getName());

    queryExecutor.executeUpdate("INSERT INTO role_assignments(userId, roleId) VALUES (?,?)", userId, role.getId());

    List<Template> templateList = templateMapper.getAllTemplatesForUser(userId);

    assertThat(templateList.size(),is(3));
    assertThat(templateList.get(0).getName(),is(reportTemplate2.getName()));
    assertThat(templateList.get(1).getName(),is(reportTemplate3.getName()));
    assertThat(templateList.get(2).getName(),is(reportTemplate1.getName()));
  }

  private Template createReportTemplate(String name, String type) {
    Template template = new Template();
    template.setName(name);
    template.setType(type);
    template.setData(new byte[1]);
    template.setCreatedBy(2L);
    template.setDescription("description");

    templateMapper.insert(template);

    return template;
  }
}
