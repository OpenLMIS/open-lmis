package org.openlmis.core.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupervisoryNode;
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
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SupervisoryNodeMapperIT {

    SupervisoryNode supervisoryNode;
    Facility facility;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

        facility = make(a(FacilityBuilder.defaultFacility));
        facility.setId(facilityMapper.insert(facility));
        supervisoryNode.setFacility(facility);
    }

    @Test
    public void shouldInsertSupervisoryNode() throws Exception {
        supervisoryNodeMapper.insert(supervisoryNode);

        SupervisoryNode resultSupervisoryNode = supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId());

        assertThat(resultSupervisoryNode, is(notNullValue()));
        assertThat(resultSupervisoryNode.getCode(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_CODE));
        assertThat(resultSupervisoryNode.getName(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_NAME));
        assertThat(resultSupervisoryNode.getApprovalPoint(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_APPROVAL_POINT));
        assertThat(resultSupervisoryNode.getModifiedDate(), CoreMatchers.is(SupervisoryNodeBuilder.SUPERVISORY_NODE_DATE));
        assertThat(resultSupervisoryNode.getFacility().getId(), is(facility.getId()));
    }

    @Test
    public void shouldGetSupervisoryNodeIdByCode() throws Exception {
        supervisoryNodeMapper.insert(supervisoryNode);

        Integer fetchedId = supervisoryNodeMapper.getIdForCode(supervisoryNode.getCode());

        assertThat(fetchedId, is(supervisoryNode.getId()));
    }
}
