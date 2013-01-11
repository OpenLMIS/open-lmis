package org.openlmis.authentication.service;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.stereotype.Service;

import static org.openlmis.authentication.hash.Encoder.hash;

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

    public UserToken authorizeUser(String userName, String password) {
        String passwordHash = hash(password);
        User fetchedUser = userMapper.selectUserByUserNameAndPassword(userName, passwordHash);
        if (fetchedUser == null) return new UserToken(userName, null, AUTHORIZATION_FAILED);

        return new UserToken(fetchedUser.getUserName(), fetchedUser.getId(), AUTHORIZATION_SUCCESSFUL);
    }
}
