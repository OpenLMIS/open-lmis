package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
public class SyncUpHashMapperIT {
    @Autowired
    SyncUpHashMapper syncUpHashMapper;

    @After
    public void tearDown() throws Exception {
        syncUpHashMapper.deleteAll();
    }

    @Test
    public void shouldSaveHashThatDoesNotExistYet() throws Exception {
        String hash = "this is a new hash that has never been saved before";
        syncUpHashMapper.insert(hash);
        List<String> matchedHashes = syncUpHashMapper.find(hash);

        assertThat(matchedHashes.size(), is(1));
    }

    @Test(expected = DuplicateKeyException.class)
    public void shouldNotSaveTheSameHashTwice() throws Exception {
        String hash = "hello hash";

        syncUpHashMapper.insert(hash);
        syncUpHashMapper.insert(hash);
    }

    @Test
    public void shouldFindExistingHash() throws Exception {
        String hash = "abc hash";
        List<String> matchedHashes = syncUpHashMapper.find(hash);
        assertThat(matchedHashes.size(), is(0));

        syncUpHashMapper.insert(hash);
        matchedHashes = syncUpHashMapper.find(hash);
        assertThat(matchedHashes.size(), is(1));
    }
}