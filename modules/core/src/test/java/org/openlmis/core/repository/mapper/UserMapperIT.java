package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserMapperIT {


    @Autowired
    UserMapper userMapper;

    @Test
    public void shouldGetUserByUserNameAndPassword() throws Exception {
        User someUser = new User("someUserName", "somePassword");
        userMapper.insert(someUser);

        User user = userMapper.selectUserByUserNameAndPassword("someUserName", "somePassword");
        assertThat(user, is(notNullValue()));
        assertThat(user.getUserName(), is("someUserName"));
        assertThat(user.getId(), is(someUser.getId()));
        User user1 = userMapper.selectUserByUserNameAndPassword("someUserName", "wrongPassword");
        assertThat(user1, is(nullValue()));
        User user2 = userMapper.selectUserByUserNameAndPassword("wrongUserName", "somePassword");
        assertThat(user2, is(nullValue()));
    }


}
