package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(transactionManager = "openLmisTransactionManager")
public class ProgramDataColumnMapperIT {

  @Autowired
  QueryExecutor queryExecutor;

  @Autowired
  ProgramDataColumnMapper programDataColumnMapper;

  @Test
  public void shouldGetProgramDataColumnByCode() throws SQLException {
    queryExecutor.executeQuery("INSERT INTO supplemental_programs (code, name, description, active) " +
        "VALUES ('RAPID_TEST', 'Rapid Test', 'Rapid test', TRUE);");
    queryExecutor.executeQuery("INSERT INTO program_data_columns (code, supplementalProgramId) VALUES " +
        "('HIV-DETERMINE-CONSUME', (SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));");

    ProgramDataColumn programDataColumn = programDataColumnMapper.getColumnByCode("HIV-DETERMINE-CONSUME");
    assertThat(programDataColumn.getCode(), is("HIV-DETERMINE-CONSUME"));
    assertThat(programDataColumn.getSupplementalProgram().getCode(), is("RAPID_TEST"));
  }


}