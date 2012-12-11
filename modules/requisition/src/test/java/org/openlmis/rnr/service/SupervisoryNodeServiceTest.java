package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.openlmis.rnr.repository.SupervisoryNodeRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeServiceTest {

    @Mock
    SupervisoryNodeRepository supervisoryNodeRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveSupervisoryNode() throws Exception {
        SupervisoryNode supervisoryNode = new SupervisoryNode();
        new SupervisoryNodeService(supervisoryNodeRepository).save(supervisoryNode);

        verify(supervisoryNodeRepository).save(supervisoryNode);
    }
}
