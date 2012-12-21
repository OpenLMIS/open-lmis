package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.service.SupervisoryNodeService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class SupervisoryNodeServiceTest {

    @Mock
    SupervisoryNodeRepository supervisoryNodeRepository;
    SupervisoryNodeService supervisoryNodeService;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        supervisoryNodeService = new SupervisoryNodeService(supervisoryNodeRepository);
    }

    @Test
    public void shouldSaveSupervisoryNode() throws Exception {
        SupervisoryNode supervisoryNode = new SupervisoryNode();
        supervisoryNodeService.save(supervisoryNode);

        verify(supervisoryNodeRepository).save(supervisoryNode);
    }

    @Test
    public void shouldGetUserSupervisedNodesInHierarchyByUserAndProgram(){
        Integer userId = 1;
        Integer programId = 1;
        supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
        verify(supervisoryNodeRepository).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    }
}
