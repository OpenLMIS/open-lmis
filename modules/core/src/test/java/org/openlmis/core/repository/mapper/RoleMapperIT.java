package org.openlmis.core.repository.mapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RoleMapperIT {

    @Autowired
    RoleMapper roleMapper;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldInsertRole() throws Exception {
        Role role = new Role("role name", "");
        roleMapper.insert(role);

        Role resultRole = roleMapper.get(role.getId());
        assertThat(resultRole, is(role));
    }

    @Test(expected = DuplicateKeyException.class)
    public void shouldThrowDuplicateKeyExceptionIfDuplicateRoleName() throws Exception {
        String duplicateRoleName = "role name";
        Role role = new Role(duplicateRoleName, "");
        Role role2 = new Role(duplicateRoleName, "any other description");
        roleMapper.insert(role);
        roleMapper.insert(role2);
    }
}
