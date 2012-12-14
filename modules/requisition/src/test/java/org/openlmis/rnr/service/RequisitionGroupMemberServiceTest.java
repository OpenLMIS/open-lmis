package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.rnr.domain.RequisitionGroupMember;
import org.openlmis.rnr.repository.RequisitionGroupMemberRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupMemberServiceTest {

    @Mock
    RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRGMember() throws Exception {

        RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
        new RequisitionGroupMemberService(requisitionGroupMemberRepository).save(requisitionGroupMember);

        verify(requisitionGroupMemberRepository).insert(requisitionGroupMember);
    }
}
