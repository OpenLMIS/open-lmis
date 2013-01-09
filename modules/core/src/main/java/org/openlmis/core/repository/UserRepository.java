package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@NoArgsConstructor
public class UserRepository {

  private UserMapper userMapper;

  @Autowired
  public UserRepository(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public List<User> getUsersWithRightInNodeForProgram(Integer programId, Integer nodeId, Right right) {
    return userMapper.getUsersWithRightInNodeForProgram(programId, nodeId, right);
  }
}
