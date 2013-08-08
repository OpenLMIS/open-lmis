package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.core.domain.Refrigerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.*;

@Category(IntegrationTests.class)
@Transactional
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RefrigeratorMapperIT {

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RefrigeratorMapper mapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;

  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  DeliveryZoneMemberMapper deliveryZoneMemberMapper;


  @Autowired
  DeliveryZoneProgramScheduleMapper deliveryZoneProgramScheduleMapper;

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;


  @Test
  public void shouldReturnListOfRefrigeratosForAFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    Refrigerator refrigerator = new Refrigerator("SAM","AUO","SAM1",facility.getId());
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    List<Refrigerator> refrigerators = mapper.getRefrigerators(facility.getId());

    assertThat(refrigerators.size(),is(1));
    assertThat(refrigerators.get(0),is(refrigerator));
  }

  @Test
  public void shouldGetAllInDeliveryZoneAndOrderByGeographicZoneParentAndFacilityName() {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    DeliveryZone deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramScheduleMapper.insert(new DeliveryZoneProgramSchedule(deliveryZone.getId(),
      program.getId(), processingSchedule.getId()));

    Facility facility1 = insertMemberFacility(deliveryZone, program, "F10A", "facility1", 10l, true);


    Refrigerator refrigerator = new Refrigerator("SAM","AUO","SAM1",facility1.getId());
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    Program unsupportedProgram = new Program();
    unsupportedProgram.setId(2l);

    List<Refrigerator> refrigerators = mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZone.getId(), program.getId());

    assertThat(refrigerators.get(0).getSerialNumber(),is(refrigerator.getSerialNumber()));
  }

  private Facility insertMemberFacility(DeliveryZone zone, Program program, String facilityCode, String facilityName,
                                        Long geoZoneId, Boolean facilityActive) {
    Facility facility = make(a(FacilityBuilder.defaultFacility, with(code, facilityCode), with(name, facilityName),
      with(geographicZoneId, geoZoneId), with(active, facilityActive)));
    facilityMapper.insert(facility);
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityId(facility.getId());
    programSupported.setProgram(program);
    programSupportedMapper.insert(programSupported);
    DeliveryZoneMember member1 = new DeliveryZoneMember(zone, facility);
    deliveryZoneMemberMapper.insert(member1);
    return facility;
  }


}
