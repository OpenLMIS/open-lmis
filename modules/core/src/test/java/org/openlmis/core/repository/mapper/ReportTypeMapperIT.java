
package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ReportTypeBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ReportType;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programId;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ReportTypeMapperIT {


    @Autowired
    ReportTypeMapper mapper;

    @Autowired
    ProgramMapper programMapper;

    @Test
    public void shouldFetchAllReportTypeAvailable() throws Exception {
        Program program = make(a(defaultProgram, with(programId, 1L)));
        programMapper.insert(program);
        ReportType trz001 = make(a(ReportTypeBuilder.defaultReportType));
        trz001.setProgramId(program.getId());
        mapper.insert(trz001);
        List<ReportType> reportTypes = mapper.getAll();
        assertThat(reportTypes, notNullValue());
    }

}