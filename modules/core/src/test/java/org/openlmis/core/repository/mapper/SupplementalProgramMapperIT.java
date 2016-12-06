package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.moz.SupplementalProgram;
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
public class SupplementalProgramMapperIT {

  @Autowired
  QueryExecutor queryExecutor;

  @Autowired
  SupplementalProgramMapper supplementalProgramMapper;

  @Test
  public void shouldGetSupplementalProgramByCode() throws SQLException {
    queryExecutor.executeQuery("INSERT INTO supplemental_programs (code, name, description, active) " +
        "VALUES ('RAPID_TEST', 'Rapid Test', 'Rapid test', TRUE);");
    SupplementalProgram supplementalProgram = supplementalProgramMapper.getSupplementalProgramByCode("RAPID_TEST");
    assertThat(supplementalProgram.getCode(), is("RAPID_TEST"));
    assertThat(supplementalProgram.getName(), is("Rapid Test"));
  }

}