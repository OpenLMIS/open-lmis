package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.builder.RequisitionGroupBuilder;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMemberMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupRepositoryTest {

    public static final String USER = "user";
    RequisitionGroupRepository requisitionGroupRepository;
    RequisitionGroup requisitionGroup;
    String headFacilityCode = "testCode";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    RequisitionGroupMapper requisitionGroupMapper;

    @Mock
    FacilityMapper facilityMapper;

    @Mock
    RequisitionGroupMemberMapper requisitionGroupMemberMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupRepository = new RequisitionGroupRepository(requisitionGroupMapper,facilityMapper, requisitionGroupMemberMapper);
        requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
        requisitionGroup.getHeadFacility().setCode((headFacilityCode));
    }

    @Test
    public void shouldSaveRequisitionGroup() {
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(1L);
        requisitionGroupRepository.insert(requisitionGroup);
        assertThat(requisitionGroup.getHeadFacility(),is(notNullValue()));
        assertThat(requisitionGroup.getHeadFacility().getId(), is(1L));
        assertThat(requisitionGroup.getHeadFacility().getCode(),is(headFacilityCode));

        verify(requisitionGroupMapper).insert(requisitionGroup);
    }

    @Test
    public void shouldInsertHeadFacilityIntoRequisitionGroupMemberIfDoesNotAlreadyExist() throws Exception {
        Long headFacilityId = 1L;
        Long requisitionGroupId = 5L;
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(headFacilityId);
        when(requisitionGroupMapper.insert(requisitionGroup)).thenReturn(requisitionGroupId);

        requisitionGroupRepository.insert(requisitionGroup);
        RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember(requisitionGroup.getId(), requisitionGroup.getHeadFacility().getId());
        requisitionGroupMember.setModifiedBy(USER);
        verify(requisitionGroupMemberMapper).insert(requisitionGroupMember);
        assertThat(requisitionGroup.getHeadFacility(), is(notNullValue()));
        assertThat(requisitionGroup.getHeadFacility().getId(), is(headFacilityId));
        assertThat(requisitionGroup.getHeadFacility().getCode(), is(headFacilityCode));

        verify(requisitionGroupMapper).insert(requisitionGroup);
    }

    @Test
    public void shouldGiveDuplicateCodeError() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Requisition Group Code found");
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(1L);
        doThrow(new DuplicateKeyException("")).when(requisitionGroupMapper).insert(requisitionGroup);

        requisitionGroupRepository.insert(requisitionGroup);

        verify(requisitionGroupMapper).insert(requisitionGroup);
    }

    @Test
    public void shouldGiveHeadFacilityNotFoundErrorIfTheFacilityDoesNotExist() throws Exception {

        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(null);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Head Facility Not Found");
        requisitionGroupRepository.insert(requisitionGroup);

        verify(facilityMapper).getIdForCode(headFacilityCode);
    }


    @Test
    public void shouldGiveParentRequisitionGroupNotFoundErrorWhenParentGroupDoesNotExist() throws Exception {
        doThrow(new DataIntegrityViolationException("")).when(requisitionGroupMapper).insert(requisitionGroup);
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(1L);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Parent RG code not found");
        requisitionGroupRepository.insert(requisitionGroup);

        verify(requisitionGroupMapper).insert(requisitionGroup);
    }


}
