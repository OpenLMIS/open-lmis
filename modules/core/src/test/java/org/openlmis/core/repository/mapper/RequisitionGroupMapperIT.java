package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.RequisitionGroupBuilder.*;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupMapperIT {

    SupervisoryNode supervisoryNode;

    @Autowired
    RequisitionGroupMapper requisitionGroupMapper;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    FacilityMapper facilityMapper;
    private RequisitionGroup requisitionGroup;

    @Before
    public void setUp() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        supervisoryNode = make(a(defaultSupervisoryNode));
        supervisoryNode.setFacility(facility);
        supervisoryNodeMapper.insert(supervisoryNode);
        requisitionGroup = make(a(defaultRequisitionGroup));
    }

    @Test
    public void shouldInsertRequisitionGroup() throws Exception {
        requisitionGroup.setSupervisoryNode(supervisoryNode);

        requisitionGroupMapper.insert(requisitionGroup);

        RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroup.getId());

        assertThat(resultRequisitionGroup.getCode(), is(REQUISITION_GROUP_CODE));
        assertThat(requisitionGroup.getId(), is(notNullValue()));
        assertThat(resultRequisitionGroup.getModifiedDate(), is(requisitionGroup.getModifiedDate()));
        assertThat(resultRequisitionGroup.getName(), is(REQUISITION_GROUP_NAME));
        assertThat(resultRequisitionGroup.getSupervisoryNode().getId(), is(supervisoryNode.getId()));
    }

    @Test
    public void shouldGetRequisitionGroupsForSupervisoryNodes(){
        requisitionGroup.setSupervisoryNode(supervisoryNode);
        requisitionGroupMapper.insert(requisitionGroup);

        List<RequisitionGroup> requisitionGroups = requisitionGroupMapper.getRequisitionGroupBySupervisoryNodes("{" + supervisoryNode.getId() + "}");

        assertThat(requisitionGroups.size(), is(1));
    }
}
