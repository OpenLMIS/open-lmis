package org.openlmis.authentication.service;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private static final boolean AUTHORIZATION_SUCCESSFUL = true;
    private static final boolean AUTHORIZATION_FAILED = false;

    private UserMapper userMapper;

    @Autowired
    public UserAuthenticationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserToken authorizeUser(User user) {
        User fetchedUser = userMapper.selectUserByUserNameAndPassword(user.getUserName(), user.getPassword());
        if (fetchedUser == null) return new UserToken(user.getUserName(), null, AUTHORIZATION_FAILED);

        return new UserToken(fetchedUser.getUserName(), fetchedUser.getId(), AUTHORIZATION_SUCCESSFUL);
    }
}
