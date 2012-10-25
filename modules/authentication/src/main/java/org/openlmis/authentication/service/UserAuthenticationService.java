package org.openlmis.authentication.service;

import org.openlmis.authentication.UserToken;
import org.openlmis.authentication.dao.UserMapper;
import org.openlmis.authentication.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService{

    private UserMapper userMapper;

    @Autowired
    public UserAuthenticationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserToken authorizeUser(String userName, String password) {

        User fetchedUser = userMapper.selectUserByUserNameAndPassword(userName, password);
        User userForToken = getUserForUserToken(userName, fetchedUser);

        return new UserToken(userForToken, getTokenFlag(fetchedUser));
    }

    private User getUserForUserToken(String userName, User user) {
        return user==null?new User(userName, null):user;
    }

    private boolean getTokenFlag(User user) {
        return user!=null;
    }
}
