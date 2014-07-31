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
import org.openlmis.core.domain.Right;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.RightMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.reporting.model.ReportRight;
import org.openlmis.reporting.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.RightType.REPORTING;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-reporting.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ReportRightMapperIT {

  @Autowired
  ReportRightMapper mapper;

  @Autowired
  RightMapper rightMapper;

  @Autowired
  TemplateMapper templateMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldInsertReportRight() throws SQLException {

    String templateName = "name";
    String description = "desc";
    Template template = new Template(templateName, new byte[]{'a'}, null, REPORTING.toString(), description);
    templateMapper.insert(template);
    Right right = new Right(templateName, REPORTING);
    rightMapper.insertRight(right);
    ReportRight reportRight = new ReportRight(template, right);

    mapper.insert(reportRight);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM report_rights WHERE templateId=?", template.getId());
    resultSet.next();
    assertThat(resultSet.getInt("templateid"), is(template.getId().intValue()));
    assertThat(resultSet.getString("rightname"), is(templateName));
  }
}
