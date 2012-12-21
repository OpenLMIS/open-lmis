package org.openlmis.core.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class RequisitionGroupHandlerTest {

    public static final String USER = "USER";
    RequisitionGroupHandler requisitionGroupHandler;

    @Mock
    RequisitionGroupService requisitionGroupService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupHandler = new RequisitionGroupHandler(requisitionGroupService);
    }

    @Test
    public void shouldSaveRequisitionGroupWithModifiedByAndModifiedDateSet() throws Exception {
        RequisitionGroup requisitionGroup = new RequisitionGroup();
        requisitionGroup.setModifiedBy(USER);

        requisitionGroupHandler.save(requisitionGroup, "USER");

        assertThat(requisitionGroup.getModifiedBy(), is(USER));
        assertThat(requisitionGroup.getModifiedDate(), is(notNullValue()));
        verify(requisitionGroupService).save(requisitionGroup);
    }
}
