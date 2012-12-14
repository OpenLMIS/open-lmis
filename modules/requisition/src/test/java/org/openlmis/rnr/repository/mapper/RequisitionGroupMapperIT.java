package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.rnr.builder.RequisitionGroupBuilder.*;
import static org.openlmis.rnr.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RequisitionGroupMapperIT {

    SupervisoryNode supervisoryNode;
    Integer supervisoryNodeId;


    @Autowired
    RequisitionGroupMapper requisitionGroupMapper;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        Facility facility = make(a(defaultFacility));
        facility.setId(facilityMapper.insert(facility));
        supervisoryNode = make(a(defaultSupervisoryNode));
        supervisoryNode.setFacility(facility);

        supervisoryNodeId = supervisoryNodeMapper.insert(supervisoryNode);
        supervisoryNode.setId(supervisoryNodeId);
    }

    @Test
    public void shouldInsertRequisitionGroup() throws Exception {
        RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
        requisitionGroup.setSupervisoryNode(supervisoryNode);

        Integer requisitionGroupID = requisitionGroupMapper.insert(requisitionGroup);

        RequisitionGroup resultRequisitionGroup = requisitionGroupMapper.getRequisitionGroupById(requisitionGroupID);

        assertThat(resultRequisitionGroup.getCode(), is(REQUISITION_GROUP_CODE));
        assertThat(requisitionGroupID, is(notNullValue()));
        assertThat(resultRequisitionGroup.getName(), is(REQUISITION_GROUP_NAME));
        assertThat(resultRequisitionGroup.getSupervisoryNode().getId(), is(supervisoryNodeId));
    }
}
