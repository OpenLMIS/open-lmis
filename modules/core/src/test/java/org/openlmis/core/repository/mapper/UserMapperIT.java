package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserMapperIT {


  @Autowired
  UserMapper userMapper;

  @Test
  public void shouldAuthenticateCorrectUsernamePassword() throws Exception {
    User someUser = new User("someUserName","somePassword");
    userMapper.insert(someUser);

    assertTrue(userMapper.authenticate("someUserName", "somePassword"));
    assertFalse(userMapper.authenticate("someUserName", "wrongPassword"));
    assertFalse(userMapper.authenticate("wrongUserName", "somePassword"));
  }


}
