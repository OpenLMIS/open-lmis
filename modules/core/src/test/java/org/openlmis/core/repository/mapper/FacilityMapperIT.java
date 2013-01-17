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

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class FacilityMapperIT {

    public static final String OPERATED_BY_MOH = "MoH";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    FacilityMapper facilityMapper;

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
        facilityMapper.insert(trz001);
        facilityMapper.insert(trz002);

        List<Facility> facilities = facilityMapper.getAll();

        assertEquals(facilities.get(0).getCode(), trz001.getCode());
        assertEquals(facilities.get(1).getCode(), trz002.getCode());
    }

    @Test
    public void shouldFetchFacilityAndFacilityTypeDataForRequisitionHeader() {
        Facility facility = make(a(defaultFacility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(typeId, 2),
                with(type, "lvl3_hospital")));

        facilityMapper.insert(facility);

        RequisitionHeader requisitionHeader = facilityMapper.getRequisitionHeaderData(facility.getId());

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

        assertThat(geographicZone.getId(), is(1));
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
        facilityMapper.insert(facility);
        Facility resultFacility = facilityMapper.get(facility.getId());
        assertThat(resultFacility.getCode(), is("F10010"));
        assertThat(resultFacility.getId(), is(facility.getId()));
        assertThat(resultFacility.getName(), is("Apollo Hospital"));
    }

    @Test
    public void shouldUpdateFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        facility.setCode("NewTestCode");

        facilityMapper.update(facility);

        Facility updatedFacility = facilityMapper.get(facility.getId());
        assertThat(updatedFacility.getCode(), is(facility.getCode()));
    }

    @Test
    public void shouldReturnFacilityOperatorIdForCode() {
        Integer id = facilityMapper.getOperatedByIdForCode(OPERATED_BY_MOH);
        assertThat(id, is(1));

        id = facilityMapper.getOperatedByIdForCode("InValid");
        assertThat(id, is(nullValue()));
    }

    @Test
    public void shouldReturnFacilityTypeIdForCode() {
        Integer id = facilityMapper.getFacilityTypeIdForCode(FACILITY_TYPE_CODE);
        assertThat(id, is(1));

        id = facilityMapper.getFacilityTypeIdForCode("InValid");
        assertThat(id, is(nullValue()));
    }

    @Test
    public void shouldReturnFacilityTypeById() {
        Integer id = facilityMapper.getFacilityTypeIdForCode(FACILITY_TYPE_CODE);

        FacilityType facilityType = facilityMapper.getFacilityTypeById(id);
        assertThat(facilityType, is(notNullValue()));
        assertThat(facilityType.getId(), is(id));
        assertThat(facilityType.getCode(), is(FACILITY_TYPE_CODE));
    }

    @Test
    public void shouldReturnFacilityOperatorById() throws Exception {
        Integer id = facilityMapper.getOperatedByIdForCode(OPERATED_BY_MOH);

        FacilityOperator operator = facilityMapper.getFacilityOperatorById(id);
        assertThat(operator.getId(), is(id));
        assertThat(operator.getCode(), is(OPERATED_BY_MOH));
    }

    @Test
    public void shouldUpdateDataReportableAndActiveForAFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        facility.setDataReportable(false);
        facility.setActive(false);
        facility.setModifiedBy("user1");
        facilityMapper.updateDataReportableAndActiveFor(facility);

        Facility updatedFacility = facilityMapper.get(facility.getId());

        assertThat(updatedFacility.getDataReportable(), is(false));
        assertThat(updatedFacility.getActive(), is(false));
        assertThat(updatedFacility.getModifiedBy(), is("user1"));
    }

    @Test
    public void shouldGetIdByCode() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        assertThat(facilityMapper.getIdForCode(facility.getCode()), is(facility.getId()));
    }

    @Test
    public void shouldTellIfGeographicZoneIdExists() throws Exception {
        assertThat(facilityMapper.isGeographicZonePresent(1), is(true));
        assertThat(facilityMapper.isGeographicZonePresent(9999), is(false));
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
        facilityMapper.insert(facilitySupportingProgramInRG1);
        facilityMapper.insert(facilityNotSupportingProgramInRG2);
        facilityMapper.insert(facilitySupportingProgramNotInAnyRG);


        requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg1, facilitySupportingProgramInRG1));
        requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilitySupportingProgramInRG1));
        requisitionGroupMemberMapper.insert(new RequisitionGroupMember(rg2, facilityNotSupportingProgramInRG2));

        programMapper.insert(make(a(defaultProgram, with(programCode, "Random"))));


        programSupportedMapper.addSupportedProgram(make(a(defaultProgramSupported,
                with(supportedFacilityId, facilitySupportingProgramInRG1.getId()),
                with(supportedProgramId, make(a(defaultProgram, with(programCode, "Random"))).getId()))));

        programSupportedMapper.addSupportedProgram(make(a(defaultProgramSupported,
                with(supportedFacilityId, facilitySupportingProgramNotInAnyRG.getId()),
                with(supportedProgramId, make(a(defaultProgram, with(programCode, "Random"))).getId()))));

        List<Facility> facilities = facilityMapper.getFacilitiesBy(make(a(defaultProgram, with(programCode, "Random"))).getId(), "{" + rg1.getId() + "," + rg2.getId() + " }");

        assertThat(facilities.size(), is(1));
        assertThat(facilities.get(0).getCode(), is("F1"));

    }

}
