package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class FacilityMapperIT {

  public static final String OPERATED_BY_MOH = "MoH";
  @Autowired
  private UserMapper userMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Test
  public void shouldFetchAllFacilitiesAvailable() throws Exception {
    Facility trz001 = make(a(defaultFacility,
        with(code, "TRZ001"),
        with(name, "Ngorongoro Hospital"),
        with(type, "warehouse"),
        with(geographicZoneId, 1L)));
    Facility trz002 = make(a(defaultFacility,
        with(code, "TRZ002"),
        with(name, "Rural Clinic"),
        with(type, "lvl3_hospital"),
        with(geographicZoneId, 2L)));

    facilityMapper.insert(trz001);
    facilityMapper.insert(trz002);

    List<Facility> facilities = facilityMapper.getAll();

    assertEquals(facilities.get(0).getCode(), trz001.getCode());
    assertEquals(facilities.get(1).getCode(), trz002.getCode());
  }

  @Test
  public void shouldFetchFacilityAndFacilityTypeDataForRequisitionHeader() {
    Facility facility1 = make(a(defaultFacility,
        with(code, "TRZ001"),
        with(name, "Ngorongoro Hospital"),
        with(type, "lvl3_hospital")));

    Facility facility2 = make(a(defaultFacility));

    int facilityId = facilityMapper.insert(facility1);
    facilityMapper.insert(facility2);

    RequisitionHeader requisitionHeader = facilityMapper.getRequisitionHeaderData(facilityId);

    assertEquals("TRZ001", requisitionHeader.getFacilityCode());
    assertEquals("Ngorongoro Hospital", requisitionHeader.getFacilityName());
    assertEquals("Lvl3 Hospital", requisitionHeader.getFacilityType());
    assertEquals(.5, requisitionHeader.getEmergencyOrderPoint(), 0.0);
    assertEquals(3, requisitionHeader.getMaximumStockLevel());
    assertEquals("MoH", requisitionHeader.getFacilityOperatedBy());

    assertEquals("Dodoma", requisitionHeader.getZone().getValue());
    assertEquals("Arusha", requisitionHeader.getParentZone().getValue());

    assertEquals("city", requisitionHeader.getZone().getLabel());
    assertEquals("state", requisitionHeader.getParentZone().getLabel());
  }

  @Test
  public void shouldGetAllFacilityTypes() throws Exception {
    List<FacilityType> facilityTypes = facilityMapper.getAllTypes();

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
    List<FacilityOperator> allOperators = facilityMapper.getAllOperators();
    assertThat(allOperators.size(), is(4));
    FacilityOperator facilityOperator = allOperators.get(0);
    assertThat(facilityOperator.getCode(), is("MoH"));
    assertThat(facilityOperator.getText(), is("MoH"));
    assertThat(facilityOperator.getDisplayOrder(), is(1));
  }

  @Test
  public void shouldGetAllGeographicZones() throws Exception {
    List<GeographicZone> allGeographicZones = facilityMapper.getAllGeographicZones();
    assertThat(allGeographicZones.size(), is(3));
    GeographicZone geographicZone = allGeographicZones.get(0);

    assertThat(geographicZone.getId(), is(1L));
    assertThat(geographicZone.getValue(), is("Arusha"));
    assertThat(geographicZone.getLabel(), is("state"));
  }

  @Test
  public void shouldReturnFacilityForAUser() throws Exception {
    facilityMapper.insert(make(a(defaultFacility)));
    Facility facility = facilityMapper.getAll().get(0);

    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);

    Facility userFacility = facilityMapper.getHomeFacility(user.getUserName());

    assertEquals(facility.getCode(), userFacility.getCode());
    assertEquals(facility.getName(), userFacility.getName());
    assertEquals(facility.getId(), userFacility.getId());
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(facilityMapper.insert(facility));
    Facility resultFacility = facilityMapper.get(facility.getId());
    assertThat(resultFacility.getCode(), is("F10010"));
    assertThat(resultFacility.getId(), is(facility.getId()));
    assertThat(resultFacility.getName(), is("Apollo Hospital"));
  }

  @Test
  public void shouldUpdateFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(facilityMapper.insert(facility));
    facility.setCode("NewTestCode");

    facilityMapper.update(facility);

    Facility updatedFacility = facilityMapper.get(facility.getId());
    assertThat(updatedFacility.getCode(), is(facility.getCode()));
  }

  @Test
  public void shouldReturnFacilityOperatorIdForCode() {
    Long id = facilityMapper.getOperatedByIdForCode(OPERATED_BY_MOH);
    assertThat(id, is(1L));

    id = facilityMapper.getOperatedByIdForCode("InValid");
    assertThat(id, is(nullValue()));
  }

  @Test
  public void shouldReturnFacilityTypeIdForCode() {
    Long id = facilityMapper.getFacilityTypeIdForCode(FACILITY_TYPE_CODE);
    assertThat(id, is(1L));

    id = facilityMapper.getFacilityTypeIdForCode("InValid");
    assertThat(id, is(nullValue()));
  }
}
