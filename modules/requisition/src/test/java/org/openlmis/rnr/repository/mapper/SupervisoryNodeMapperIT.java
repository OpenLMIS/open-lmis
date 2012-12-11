package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SupervisoryNodeMapperIT {

    SupervisoryNode supervisoryNode;
    String code = "TSN";
    String name = "Test Name";
    Facility facility;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        supervisoryNode = new SupervisoryNode();
        supervisoryNode.setCode(code);
        supervisoryNode.setApprovalPoint(true);
        supervisoryNode.setName(name);
        facility = make(a(FacilityBuilder.defaultFacility));
        facility.setId(facilityMapper.insert(facility));
        supervisoryNode.setFacility(facility);
    }


    @Test
    public void shouldInsertSupervisoryNode() throws Exception {


        Long nodeId = supervisoryNodeMapper.insert(supervisoryNode);

        SupervisoryNode resultSupervisoryNode = supervisoryNodeMapper.getSupervisoryNode(nodeId);

        assertThat(resultSupervisoryNode, is(notNullValue()));
        assertThat(resultSupervisoryNode.getCode(), is(code));
        assertThat(resultSupervisoryNode.getName(), is(name));
        assertThat(resultSupervisoryNode.getApprovalPoint(), is(true));
        assertThat(resultSupervisoryNode.getFacility().getId(), is(facility.getId()));
    }

    @Test
    public void shouldGetSupervisoryNodeIdByCode() throws Exception {
        Long insertedId = supervisoryNodeMapper.insert(supervisoryNode);

        Long fetchedId = supervisoryNodeMapper.getIdForCode(supervisoryNode.getCode());

        assertThat(fetchedId, is(insertedId));
    }
}
