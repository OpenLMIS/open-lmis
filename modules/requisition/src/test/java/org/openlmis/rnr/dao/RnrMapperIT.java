package org.openlmis.rnr.dao;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.Requisition;
import org.openlmis.rnr.domain.RnrStatus;
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
    FacilityMapper facilityMapper;

    @Autowired
    private RnrMapper rnrMapper;

    public static final String PROGRAM_CODE = "HIV";

    @Before
    public void setUp() {
        rnrMapper.deleteAll();
        facilityMapper.deleteAll();
        Facility facility = make(a(FacilityBuilder.facility));
        facilityMapper.insert(facility);
    }

    @Test
    public void shouldCreateRequisition() {
        int status = rnrMapper.insert(new Requisition(FACILITY_CODE, PROGRAM_CODE, RnrStatus.INITIATED, "user", DateTime.now().toDate()));
        assertThat(status, is(1));
    }

}
