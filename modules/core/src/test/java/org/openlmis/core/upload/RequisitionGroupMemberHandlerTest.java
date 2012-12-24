package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.RequisitionGroupMemberService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupMemberHandlerTest {

    public static final String USER = "user";
    private RequisitionGroupMemberHandler requisitionGroupMemberHandler;

    @Mock
    RequisitionGroupMemberService requisitionGroupMemberService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRGMembersTaggedWithModifiedBy() throws Exception {
        RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();

        new RequisitionGroupMemberHandler(requisitionGroupMemberService).save(requisitionGroupMember, USER);

        assertThat(requisitionGroupMember.getModifiedBy(), is(USER));
        assertThat(requisitionGroupMember.getModifiedDate(), is(notNullValue()));

        verify(requisitionGroupMemberService).save(requisitionGroupMember);
    }
}
