package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
public class SyncUpHashMapperIT {
    @Autowired
    SyncUpHashMapper syncUpHashMapper;

    @Test(expected = DuplicateKeyException.class)
    public void shouldNotSaveTheSameHashTwice() throws Exception {
        String hash = "this is a fake hash string";

        syncUpHashMapper.insert(hash);
        syncUpHashMapper.insert(hash);
    }
}