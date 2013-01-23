package org.openlmis.core.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.FacilityRepository;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class FacilityServiceTest {
    @Mock
    FacilityRepository facilityRepository;

    FacilityService facilityService;

    @Mock
    private SupervisoryNodeService supervisoryNodeService;
    @Mock
    private RequisitionGroupService requisitionGroupService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        facilityService = new FacilityService(facilityRepository, supervisoryNodeService, requisitionGroupService);
    }

    @Test
    public void shouldStoreFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityService.save(facility);
        verify(facilityRepository).save(facility);
    }

    @Test
        public void shouldReturnEmptyListIfUserIsNotAssignedAFacility() {
        when(facilityRepository.getHomeFacility(1)).thenReturn(null);
        assertTrue(facilityService.getAllForUser(1).isEmpty());
    }

    @Test
    public void shouldGetFacilityById() throws Exception {
        Integer id = 1;
        when(facilityRepository.getById(id)).thenReturn(new Facility());
        Facility facility = facilityService.getById(id);
        assertThat(facility, is(new Facility()));
    }

    @Test
    public void shouldUpdateDataReportableAndActiveFor(){
        Facility facility = make(a(defaultFacility));
        facilityService.updateDataReportableAndActiveFor(facility);
        verify(facilityRepository).updateDataReportableAndActiveFor(facility);


    }

    @Test
    public void shouldReturnUserSupervisedFacilitiesForAProgram(){
        Integer userId = 1;
        Integer programId = 1;
        List<Facility> facilities = new ArrayList<>();
        List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
        List<RequisitionGroup> requisitionGroups = new ArrayList<>();
        when(facilityRepository.getFacilitiesBy(programId, requisitionGroups)).thenReturn(facilities);
        when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION)).thenReturn(supervisoryNodes);
        when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);

        List<Facility> result = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION);

        verify(facilityRepository).getFacilitiesBy(programId, requisitionGroups);
        verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
        verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
        assertThat(result, is(facilities));
    }
}
