package org.openlmis.rnr.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.builder.RequisitionGroupBuilder;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.rnr.builder.RequisitionGroupBuilder.parent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupMapperIT {

    @Autowired
    RequisitionGroupMapper requisitionGroupMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Test
    public void shouldInsertRequisitionGroup() throws Exception {
        Facility facility = make(a(defaultFacility));
        facility.setId(facilityMapper.insert(facility));

        String nullString = null;
        RequisitionGroup requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup, with(parent, nullString)));
        requisitionGroup.setHeadFacility(facility);

        Long requisitionGroupId = requisitionGroupMapper.insert(requisitionGroup);
        RequisitionGroup fetchedRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroupId);

        assertThat(fetchedRequisitionGroup.getId(), is(requisitionGroupId));
        assertThat(fetchedRequisitionGroup.getCode(), is(requisitionGroup.getCode()));
        assertThat(fetchedRequisitionGroup.getName(), is(requisitionGroup.getName()));
        assertThat(fetchedRequisitionGroup.getDescription(), is(requisitionGroup.getDescription()));
        assertThat(fetchedRequisitionGroup.getLevelId(), is(requisitionGroup.getLevelId()));
        assertThat(fetchedRequisitionGroup.getHeadFacility().getId(), is(facility.getId()));
        assertThat(fetchedRequisitionGroup.getParent(), is(nullValue()));
        assertThat(fetchedRequisitionGroup.getActive(), is(true));
        assertThat(fetchedRequisitionGroup.getModifiedBy(), is(requisitionGroup.getModifiedBy()));
        assertThat(fetchedRequisitionGroup.getModifiedDate(), is(notNullValue()));
    }
}
