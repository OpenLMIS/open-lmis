package org.openlmis.rnr.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnrMapperIT {

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProgramSupportedMapper programSupportedMapper;

    @Autowired
    private RnrMapper rnrMapper;

    @Before
    public void setUp() {
        programSupportedMapper.deleteAll();
        rnrMapper.deleteAll();
        facilityMapper.deleteAll();
        Facility facility = make(a(FacilityBuilder.facility));
        facilityMapper.insert(facility);
    }

    @Test
    public void shouldReturnRequisitionId() {
        Rnr requisition = new Rnr(FACILITY_CODE, "HIV", RnrStatus.INITIATED, "user", DateTime.now().toDate());
        int id1 = rnrMapper.insert(requisition);
        int id2 = rnrMapper.insert(new Rnr(FACILITY_CODE, "ARV", RnrStatus.INITIATED, "user", DateTime.now().toDate()));
        assertThat(id1, is(id2 - 1));
    }

    @After
    public void tearDown() throws Exception {
        programSupportedMapper.deleteAll();
        rnrMapper.deleteAll();
        facilityMapper.deleteAll();
    }

}
