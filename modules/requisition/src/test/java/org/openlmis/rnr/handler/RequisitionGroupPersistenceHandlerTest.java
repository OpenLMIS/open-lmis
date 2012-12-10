package org.openlmis.rnr.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.openlmis.rnr.repository.RequisitionGroupRepository;
import org.openlmis.rnr.service.RequisitionGroupService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class RequisitionGroupPersistenceHandlerTest {

    public static final String USER = "USER";
    RequisitionGroupPersistenceHandler requisitionGroupPersistenceHandler;

    @Mock
    RequisitionGroupRepository requisitionGroupRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupPersistenceHandler = new RequisitionGroupPersistenceHandler(requisitionGroupRepository);
    }

    @Test
    public void shouldSaveRequisitionGroup() throws Exception {
        RequisitionGroup requisitionGroup = new RequisitionGroup();
        requisitionGroup.setModifiedBy(USER);

        requisitionGroupPersistenceHandler.save(requisitionGroup, "USER");

        assertThat(requisitionGroup.getModifiedBy(), is(USER));
        verify(requisitionGroupRepository).insert(requisitionGroup);
    }
}
