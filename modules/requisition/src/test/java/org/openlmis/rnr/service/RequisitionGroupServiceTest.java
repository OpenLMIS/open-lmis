package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.repository.RequisitionGroupRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class RequisitionGroupServiceTest {

    RequisitionGroupService requisitionGroupService;

    @Mock
    RequisitionGroupRepository requisitionGroupRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupService = new RequisitionGroupService(requisitionGroupRepository);
    }

    @Test
    public void shouldSaveARequisitionGroup() {
        RequisitionGroup requisitionGroup = new RequisitionGroup();

        requisitionGroupService.save(requisitionGroup);

        verify(requisitionGroupRepository).save(requisitionGroup);

    }
}
