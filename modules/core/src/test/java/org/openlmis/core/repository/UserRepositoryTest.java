package org.openlmis.core.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest{

  @Mock
  UserMapper userMapper;

  @Test
  public void shouldGetUsersWithRightInNodeForProgram() throws Exception {
    List<User> users = new ArrayList<>();
    when(userMapper.getUsersWithRightInNodeForProgram(1, 1, APPROVE_REQUISITION)).thenReturn(users);

    List<User> result = new UserRepository(userMapper).getUsersWithRightInNodeForProgram(1, 1, APPROVE_REQUISITION);

    verify(userMapper).getUsersWithRightInNodeForProgram(1,1, APPROVE_REQUISITION);
    assertThat(result, is(users));
  }


}
