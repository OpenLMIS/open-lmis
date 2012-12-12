package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.rnr.builder.RequisitionGroupBuilder;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.SupervisoryNodeMapper;
import org.springframework.dao.DataIntegrityViolationException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupRepositoryTest {

    RequisitionGroupRepository requisitionGroupRepository;
    RequisitionGroup requisitionGroup;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    RequisitionGroupMapper requisitionGroupMapper;

    @Mock
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupRepository = new RequisitionGroupRepository(requisitionGroupMapper, supervisoryNodeMapper);
        requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
        requisitionGroup.setSupervisoryNode(new SupervisoryNode());
    }

    @Test
    public void shouldGiveDuplicateRGCodeErrorIfDuplicateRGCodeFound() throws Exception {
        doThrow(new DataIntegrityViolationException("")).when(requisitionGroupMapper).insert(requisitionGroup);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Requisition Group Code found");

        requisitionGroupRepository.insert(requisitionGroup);
        verify(requisitionGroupMapper).insert(requisitionGroup);
    }


    @Test
    public void shouldGiveSupervisoryNodeNotFoundErrorIfTheSupervisoryNodeDoesNotExist() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(requisitionGroup.getSupervisoryNode().getCode())).thenReturn(null);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Supervisory Node Not Found");
        requisitionGroupRepository.insert(requisitionGroup);

        verify(supervisoryNodeMapper).getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
        verify(requisitionGroupMapper, never()).insert(requisitionGroup);
    }

    @Test
    public void shouldSaveRequisitionGroup() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(requisitionGroup.getSupervisoryNode().getCode())).thenReturn(1L);

        requisitionGroupRepository.insert(requisitionGroup);
        verify(requisitionGroupMapper).insert(requisitionGroup);
        verify(supervisoryNodeMapper).getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
    }
}
