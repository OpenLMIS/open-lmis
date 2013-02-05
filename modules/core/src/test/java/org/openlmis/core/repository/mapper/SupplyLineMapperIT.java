package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SupplyLineMapperIT {

    @Autowired
    SupplyLineMapper supplyLineMapper;

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    FacilityMapper facilityMapper;


    @Test
    public void shouldInsertSupplyLine() {

        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);
        Program program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
        supervisoryNode.setFacility(facility);
        supervisoryNodeMapper.insert(supervisoryNode);

        SupplyLine supplyLine = new SupplyLine();
        supplyLine.setSupplyingFacility(facility);
        supplyLine.setProgram(program);
        supplyLine.setSupervisoryNode(supervisoryNode);

        Integer id = supplyLineMapper.insert(supplyLine);
        assertNotNull(id);
    }

    @Test
    public void shouldReturnSupplyLineForASupervisoryNodeAndProgram(){
      Facility facility = make(a(FacilityBuilder.defaultFacility));
      facilityMapper.insert(facility);
      Program program = make(a(ProgramBuilder.defaultProgram));
      programMapper.insert(program);
      SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
      supervisoryNode.setFacility(facility);
      supervisoryNodeMapper.insert(supervisoryNode);

      SupplyLine supplyLine = new SupplyLine();
      supplyLine.setSupplyingFacility(facility);
      supplyLine.setProgram(program);
      supplyLine.setSupervisoryNode(supervisoryNode);

      supplyLineMapper.insert(supplyLine);

      SupplyLine returnedSupplyLine = supplyLineMapper.getSupplyLineBy(supervisoryNode, program);

      assertThat(returnedSupplyLine.getId(), is(supplyLine.getId()));

    }

}
