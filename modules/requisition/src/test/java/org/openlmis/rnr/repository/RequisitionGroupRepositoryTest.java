package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.mapper.RequisitionGroupMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupRepositoryTest {

    RequisitionGroupRepository requisitionGroupRepository;
    RequisitionGroup requisitionGroup;
    String headFacilityCode = "testCode";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    RequisitionGroupMapper requisitionGroupMapper;

    @Mock
    FacilityMapper facilityMapper;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupRepository = new RequisitionGroupRepository(requisitionGroupMapper,facilityMapper);
        requisitionGroup = new RequisitionGroup();
        requisitionGroup.setHeadFacilityCode(headFacilityCode);
    }

    @Test
    public void shouldSaveRequisitionGroup() {
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(new Integer(1));
        requisitionGroupRepository.save(requisitionGroup);

        verify(requisitionGroupMapper).save(requisitionGroup);
    }

    @Test
    public void shouldGiveDuplicateCodeError() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Requisition Group Code found");
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(new Integer(1));
        doThrow(new DuplicateKeyException("")).when(requisitionGroupMapper).save(requisitionGroup);

        requisitionGroupRepository.save(requisitionGroup);

        verify(requisitionGroupMapper).save(requisitionGroup);
    }

    @Test
    public void shouldGiveHeadFacilityNotFoundErrorIfHeadFacilityDoesNotExist() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Head Facility Not Found");

        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(null);

        requisitionGroupRepository.save(requisitionGroup);

        verify(facilityMapper).getIdForCode(headFacilityCode);
    }

    @Test
    public void shouldGiveParentRequisitionGroupNotFoundErrorWhenParentGroupDoesNotExist() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Parent RG code not found");
        doThrow(new DataIntegrityViolationException("")).when(requisitionGroupMapper).save(requisitionGroup);
        when(facilityMapper.getIdForCode(headFacilityCode)).thenReturn(new Integer(1));
        requisitionGroupRepository.save(requisitionGroup);

        verify(requisitionGroupMapper).save(requisitionGroup);
    }
}
