package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
    SupervisoryNodeRepository supervisoryNodeRepository;
    @Mock
    private CommaSeparator commaSeparator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupRepository = new RequisitionGroupRepository(requisitionGroupMapper, supervisoryNodeRepository, commaSeparator);
        requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
        requisitionGroup.setSupervisoryNode(new SupervisoryNode());
    }

    @Test
    public void shouldGiveDuplicateRGCodeErrorIfDuplicateRGCodeFound() throws Exception {
        doThrow(new DuplicateKeyException("")).when(requisitionGroupMapper).insert(requisitionGroup);
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Duplicate Requisition Group Code found");

        requisitionGroupRepository.insert(requisitionGroup);
        verify(requisitionGroupMapper).insert(requisitionGroup);
    }


    @Test
    public void shouldGiveSupervisoryNodeNotFoundErrorIfTheSupervisoryNodeDoesNotExist() throws Exception {
        when(supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode())).thenThrow(new DataException("Invalid Supervisory Node Code"));

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Invalid Supervisory Node Code");
        requisitionGroupRepository.insert(requisitionGroup);

        verify(supervisoryNodeRepository).getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
        verify(requisitionGroupMapper, never()).insert(requisitionGroup);
    }

    @Test
    public void shouldSaveRequisitionGroup() throws Exception {
        when(supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode())).thenReturn(1);

        requisitionGroupRepository.insert(requisitionGroup);
        verify(requisitionGroupMapper).insert(requisitionGroup);
        verify(supervisoryNodeRepository).getIdForCode(requisitionGroup.getSupervisoryNode().getCode());
    }

    @Test
    public void shouldGetRequisitionGroupForSupervisoryNodes() throws Exception {
        when(supervisoryNodeRepository.getIdForCode(requisitionGroup.getSupervisoryNode().getCode())).thenReturn(1);

        List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
        when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{1, 2}");
        List<RequisitionGroup> requisitionGroups = new ArrayList<>();
        when(requisitionGroupMapper.getRequisitionGroupBySupervisoryNodes("{1, 2}")).thenReturn(requisitionGroups);
        List<RequisitionGroup> result = requisitionGroupRepository.getRequisitionGroups(supervisoryNodes);
        verify(requisitionGroupMapper).getRequisitionGroupBySupervisoryNodes("{1, 2}");
        assertThat(result, is(requisitionGroups));
    }
}
