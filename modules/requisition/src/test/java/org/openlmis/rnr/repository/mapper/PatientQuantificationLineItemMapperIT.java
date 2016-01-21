package org.openlmis.rnr.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class PatientQuantificationLineItemMapperIT {
    @Autowired
    private PatientQuantificationLineItemMapper mapper;
    @Autowired
    private QueryExecutor queryExecutor;
    @Autowired
    private RequisitionMapper requisitionMapper;
    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProcessingScheduleMapper processingScheduleMapper;
    @Autowired
    private ProcessingPeriodMapper processingPeriodMapper;

    private Rnr rnr;

    @Test
    public void shouldInsertPatientQuantificationLineItemToDB() throws SQLException {
        setUpRequisitionData();

        PatientQuantificationLineItem patientQuantificationLineItem = new PatientQuantificationLineItem("adult", 25);
        patientQuantificationLineItem.setRnrId(rnr.getId());

        mapper.insert(patientQuantificationLineItem);

        List<PatientQuantificationLineItem> lineItemList = mapper.getPatientQuantificationLineItemsByRnrId(rnr.getId());

        assertThat(lineItemList.get(0).getCategory(), is("adult"));
        assertThat(lineItemList.get(0).getTotal(), is(25));
    }

    private void setUpRequisitionData() {
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);

        ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
        processingScheduleMapper.insert(processingSchedule);

        ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
                with(scheduleId, processingSchedule.getId()),
                with(ProcessingPeriodBuilder.name, "Period1")));

        processingPeriodMapper.insert(processingPeriod);

        rnr = new Rnr(facility, new Program(2L), processingPeriod, false, 1L, 1L);
        rnr.setStatus(RnrStatus.INITIATED);
        requisitionMapper.insert(rnr);
    }
}
