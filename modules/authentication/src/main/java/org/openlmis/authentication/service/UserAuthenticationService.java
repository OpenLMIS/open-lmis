package org.openlmis.authentication.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.openlmis.authentication.hash.Encoder.hash;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserMapper userMapper;

    @Autowired
    public UserAuthenticationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean authorizeUser(String userName, String password) {
      return userMapper.authenticate(userName, hash(password));
    }
}
