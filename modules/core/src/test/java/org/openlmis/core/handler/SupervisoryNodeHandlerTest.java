package org.openlmis.core.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.handler.SupervisoryNodeHandler;
import org.openlmis.core.service.SupervisoryNodeService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeHandlerTest {

    public static final String USER = "USER";
    @Mock
    SupervisoryNodeService supervisoryNodeService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveSupervisoryNode() throws Exception {
        SupervisoryNode supervisoryNode = new SupervisoryNode();

        new SupervisoryNodeHandler(supervisoryNodeService).save(supervisoryNode, USER);
        assertThat(supervisoryNode.getModifiedBy(), is(USER));
        assertThat(supervisoryNode.getModifiedDate(), is(notNullValue()));

        verify(supervisoryNodeService).save(supervisoryNode);
    }


}
