package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class RnrMapperIT {

    public static final String HIV = "HIV";
    Integer facilityId;

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProgramSupportedMapper programSupportedMapper;

    @Autowired
    private RnrMapper rnrMapper;

    @Before
    public void setUp() {
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityId = facilityMapper.insert(facility);
    }

    @Test
    public void shouldReturnRequisitionId() {
        Rnr requisition = new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user");
        Integer id1 = rnrMapper.insert(requisition);
        Integer id2 = rnrMapper.insert(new Rnr(facilityId, "ARV", RnrStatus.INITIATED, "user"));
        assertThat(id1, is(id2 - 1));
    }

    @Test
    public void shouldReturnRequisitionById() {
        Rnr requisition = new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user");
        Integer id = rnrMapper.insert(requisition);
        Rnr requisitionById = rnrMapper.getRequisitionById(id);
        assertThat(requisitionById.getId(), is(id));
        assertThat(requisitionById.getProgramCode(), is(equalTo("HIV")));
        assertThat(requisitionById.getFacilityId(), is(equalTo(facilityId)));
        assertThat(requisitionById.getModifiedBy(), is(equalTo("user")));
        assertThat(requisitionById.getStatus(), is(equalTo(RnrStatus.INITIATED)));
    }

    @Test
    public void shouldUpdateRequisition() {
        Rnr requisition = new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user");
        Integer id = rnrMapper.insert(requisition);
        requisition.setId(id);
        requisition.setModifiedBy("user1");
        requisition.setStatus(RnrStatus.CREATED);

        rnrMapper.update(requisition);

        Rnr updatedRequisition = rnrMapper.getRequisitionById(id);

        assertThat(updatedRequisition.getId(), is(id));
        assertThat(updatedRequisition.getModifiedBy(), is(equalTo("user1")));
        assertThat(updatedRequisition.getStatus(), is(equalTo(RnrStatus.CREATED)));
    }

    @Test
    public void shouldReturnRequisitionByFacilityAndProgramAndIfExists() {
        Rnr requisition = new Rnr(facilityId, HIV, RnrStatus.INITIATED, "user");
        Integer rnrId = rnrMapper.insert(requisition);
        Rnr rnr = rnrMapper.getRequisitionByFacilityAndProgram(facilityId, HIV);
        assertThat(rnr.getId(), is(rnrId));
    }

}
