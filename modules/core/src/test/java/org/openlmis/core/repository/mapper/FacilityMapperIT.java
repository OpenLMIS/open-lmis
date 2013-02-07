package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;

@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class FacilityMapperIT {

  public static final String OPERATED_BY_MOH = "MoH";
  @Autowired
  private UserMapper userMapper;

  @Autowired
  FacilityMapper mapper;

  @Autowired
  RequisitionGroupMapper requisitionGroupMapper;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  ProgramMapper programMapper;


  @Autowired
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Test
  public void shouldFetchAllFacilitiesAvailable() throws Exception {
    Facility trz001 = make(a(defaultFacility,
      with(code, "TRZ001"),
      with(name, "Ngorongoro Hospital"),
      with(type, "warehouse"),
      with(geographicZoneId, 1)));
    Facility trz002 = make(a(defaultFacility,
      with(code, "TRZ002"),
      with(name, "Rural Clinic"),
      with(type, "lvl3_hospital"),
      with(geographicZoneId, 2)));
    mapper.insert(trz001);
    mapper.insert(trz002);

    List<Facility> facilities = mapper.getAll();

    assertEquals(facilities.get(0).getCode(), trz001.getCode());
    assertEquals(facilities.get(1).getCode(), trz002.getCode());
  }

  @Test
  public void shouldGetAllFacilityTypes() throws Exception {
    List<FacilityType> facilityTypes = mapper.getAllTypes();

    assertThat(facilityTypes.size(), is(11));
    FacilityType facilityType = facilityTypes.get(0);
    assertThat(facilityType.getCode(), is("lvl3_hospital"));
    assertThat(facilityType.getName(), is("Lvl3 Hospital"));
    assertThat(facilityType.getDescription(), is("State Hospital"));
    assertThat(facilityType.getLevelId(), is(nullValue()));
    assertThat(facilityType.getNominalMaxMonth(), is(3));
    assertThat(facilityType.getNominalEop(), is(0.5));
    assertThat(facilityType.getDisplayOrder(), is(1));
    assertThat(facilityType.isActive(), is(true));
  }

  @Test
  public void shouldGetAllOperators() throws Exception {
    List<FacilityOperator> allOperators = mapper.getAllOperators();
    assertThat(allOperators.size(), is(4));
    FacilityOperator facilityOperator = allOperators.get(0);
    assertThat(facilityOperator.getCode(), is("MoH"));
    assertThat(facilityOperator.getText(), is("MoH"));
    assertThat(facilityOperator.getDisplayOrder(), is(1));
  }

  @Test
  public void shouldGetAllGeographicZones() throws Exception {
    List<GeographicZone> allGeographicZones = mapper.getAllGeographicZones();
    assertThat(allGeographicZones.size(), is(3));
    GeographicZone geographicZone = allGeographicZones.get(0);

    assertThat(geographicZone.getId(), is(1));
    assertThat(geographicZone.getName(), is("Arusha"));
    assertThat(geographicZone.getLevel().getName(), is("state"));
  }

  @Test
  public void shouldReturnFacilityForAUser() throws Exception {
    mapper.insert(make(a(defaultFacility)));
    Facility facility = mapper.getAll().get(0);

    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    Facility userFacility = mapper.getHomeFacility(user.getId());

    assertEquals(facility.getCode(), userFacility.getCode());
    assertEquals(facility.getName(), userFacility.getName());
    assertEquals(facility.getId(), userFacility.getId());
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    Facility resultFacility = mapper.getById(facility.getId());
    assertThat(resultFacility.getCode(), is("F10010"));
    assertThat(resultFacility.getId(), is(facility.getId()));
    assertThat(resultFacility.getName(), is("Apollo Hospital"));
    assertThat(resultFacility.getGeographicZone().getName(), is("Dodoma"));
    assertThat(resultFacility.getGeographicZone().getParent().getName(), is("Arusha"));
  }

  @Test
  public void shouldUpdateFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    facility.setCode("NewTestCode");

    mapper.update(facility);

    Facility updatedFacility = mapper.getById(facility.getId());
    assertThat(updatedFacility.getCode(), is(facility.getCode()));
  }

  @Test
  public void shouldReturnFacilityOperatorIdForCode() {
    Integer id = mapper.getOperatedByIdForCode(OPERATED_BY_MOH);
    assertThat(id, is(1));

    id = mapper.getOperatedByIdForCode("InValid");
    assertThat(id, is(nullValue()));
  }

  @Test
  public void shouldReturnFacilityTypeIdForCode() {
    Integer id = mapper.getFacilityTypeIdForCode(FACILITY_TYPE_CODE);
    assertThat(id, is(1));

    id = mapper.getFacilityTypeIdForCode("InValid");
    assertThat(id, is(nullValue()));
  }

  @Test
  public void shouldReturnFacilityTypeById() {
    Integer id = mapper.getFacilityTypeIdForCode(FACILITY_TYPE_CODE);

    FacilityType facilityType = mapper.getFacilityTypeById(id);
    assertThat(facilityType, is(notNullValue()));
    assertThat(facilityType.getId(), is(id));
    assertThat(facilityType.getCode(), is(FACILITY_TYPE_CODE));
  }

  @Test
  public void shouldReturnFacilityOperatorById() throws Exception {
    Integer id = mapper.getOperatedByIdForCode(OPERATED_BY_MOH);

    FacilityOperator operator = mapper.getFacilityOperatorById(id);
    assertThat(operator.getId(), is(id));
    assertThat(operator.getCode(), is(OPERATED_BY_MOH));
  }

  @Test
  public void shouldUpdateDataReportableAndActiveForAFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    facility.setDataReportable(false);
    facility.setActive(false);
    facility.setModifiedBy("user1");
    mapper.updateDataReportableAndActiveFor(facility);

    Facility updatedFacility = mapper.getById(facility.getId());

    assertThat(updatedFacility.getDataReportable(), is(false));
    assertThat(updatedFacility.getActive(), is(false));
    assertThat(updatedFacility.getModifiedBy(), is("user1"));
  }

  @Test
  public void shouldGetIdByCode() throws Exception {
    Facility facility = make(a(defaultFacility));
    mapper.insert(facility);
    assertThat(mapper.getIdForCode(facility.getCode()), is(facility.getId()));
  }

  @Test
  public void shouldTellIfGeographicZoneIdExists() throws Exception {
    assertThat(mapper.isGeographicZonePresent(1), is(true));
    assertThat(mapper.isGeographicZonePresent(9999), is(false));
  }

  @Test
  public void shouldGetAllFacilitiesForRequisitionGroupsWhichSupportGivenProgram() {
    RequisitionGroup rg1 = make(a(RequisitionGroupBuilder.defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG1")));
    RequisitionGroup rg2 = make(a(RequisitionGroupBuilder.defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "RG2")));
    requisitionGroupMapper.insert(rg1);
    requisitionGroupMapper.insert(rg2);

    Facility facilitySupportingProgramInRG1 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F1")));
    Facility facilityNotSupportingProgramInRG2 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F2")));
    Facility facilitySupportingProgramNotInAnyRG = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.code, "F3")));
    mapper.insert(facilitySupportingProgramInRG1);
    mapper.insert(facilityNotSupportingProgramInRG2);
    mapper.insert(facilitySupportingProgramNotInAnyRG);


    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg1, facilitySupportingProgramInRG1));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilitySupportingProgramInRG1));
    requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilityNotSupportingProgramInRG2));

    programMapper.insert(make(a(defaultProgram, with(programCode, "Random"))));


    programSupportedMapper.addSupportedProgram(make(a(defaultProgramSupported,
      with(supportedFacilityId, facilitySupportingProgramInRG1.getId()),
      with(supportedProgram,make(a(defaultProgram, with(programCode, "Random")))),
      with(supportedProgramId,1))));

    programSupportedMapper.addSupportedProgram(make(a(defaultProgramSupported,
      with(supportedFacilityId, facilitySupportingProgramNotInAnyRG.getId()),
      with(supportedProgram, make(a(defaultProgram, with(programCode, "Random")))),
      with(supportedProgramId,1))));

    List<Facility> facilities = mapper.getFacilitiesBy(make(a(defaultProgram, with(programCode, "Random"))).getId(), "{" + rg1.getId() + "," + rg2.getId() + " }");

    assertThat(facilities.size(), is(1));
    assertThat(facilities.get(0).getCode(), is("F1"));
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    Facility facility1 = make(a(FacilityBuilder.defaultFacility, with(code, "FF110"), with(name, "D1100")));
    Facility facility2 = make(a(FacilityBuilder.defaultFacility, with(code, "D00"), with(name, "F110")));
    Facility facility3 = make(a(FacilityBuilder.defaultFacility, with(code, "FF1100"), with(name, "F1100")));
    Facility facility4 = make(a(FacilityBuilder.defaultFacility, with(code, "FF130")));

    mapper.insert(facility1);
    mapper.insert(facility2);
    mapper.insert(facility3);
    mapper.insert(facility4);

    List<Facility> returnedFacilityList = mapper.searchFacilitiesByCodeOrName("f11");

    assertThat(returnedFacilityList.size(), is(3));

    for (Facility facility : returnedFacilityList) {
      assertThat(facility.getCode().equals(facility1.getCode()) || facility.getCode().equals(facility2.getCode()) || facility.getCode().equals(facility3.getCode()), is(true));
    }
  }

  @Test
  public void shouldGetGeographicZoneWithParent() throws Exception {
    GeographicZone parent = new GeographicZone(null, "Dodoma", new GeopoliticalLevel(null, "city"), null);
    GeographicZone expectedZone = new GeographicZone(3, "Ngorongoro", new GeopoliticalLevel(null, "district"), parent);

    GeographicZone zone = mapper.getGeographicZoneById(3);

    assertThat(zone, is(expectedZone));
  }
}
