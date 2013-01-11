package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class UserRepository {

  private UserMapper userMapper;

  public static final String SUPERVISOR_USER_NOT_FOUND = "supervisor.user.not.found";
  public static final String DUPLICATE_EMPLOYEE_ID_FOUND = "duplicate.employee.id.found";
  public static final String DUPLICATE_EMAIL_FOUND = "duplicate.email.found";
  public static final String DUPLICATE_USER_NAME_FOUND = "duplicate.user.name.found";


  @Autowired
  public UserRepository(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public List<User> getUsersWithRightInNodeForProgram(Integer programId, Integer nodeId, Right right) {
    return userMapper.getUsersWithRightInNodeForProgram(programId, nodeId, right);
  }

  public void insert(User user) {
    validateAndSetSupervisor(user);
    try {
      userMapper.insert(user);
    }catch (DuplicateKeyException e){
      final String message = e.getMessage();
      if(message.contains("employeeId"))
      throw  new DataException(new OpenLmisMessage(DUPLICATE_EMPLOYEE_ID_FOUND));
      if(message.contains("email"))
        throw  new DataException(new OpenLmisMessage(DUPLICATE_EMAIL_FOUND));
      if(message.contains("userName"))
        throw  new DataException(new OpenLmisMessage(DUPLICATE_USER_NAME_FOUND));
    }
  }

  private void validateAndSetSupervisor(User user) {
    User supervisor = null;

    if (user.getSupervisor() != null && user.getSupervisor().getUserName() != null
      && !user.getSupervisor().getUserName().isEmpty()) {

      supervisor = userMapper.get(user.getSupervisor().getUserName());
      if (supervisor == null) throw new DataException(new OpenLmisMessage(SUPERVISOR_USER_NOT_FOUND));
    }

    user.setSupervisor(supervisor);
  }
}
