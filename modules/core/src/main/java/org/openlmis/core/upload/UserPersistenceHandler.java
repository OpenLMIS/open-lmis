package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@NoArgsConstructor
public class UserPersistenceHandler extends AbstractModelPersistenceHandler {

  private UserService userService;

  @Autowired
  public UserPersistenceHandler(UserService userService) {

    this.userService = userService;
  }

  @Override
  protected void save(Importable modelClass, String modifiedBy) {
    final User user = (User) modelClass;
    user.setModifiedBy(modifiedBy);
    userService.save(user, new HashMap());
  }
}
