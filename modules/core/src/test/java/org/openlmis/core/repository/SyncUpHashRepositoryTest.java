package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.mapper.SyncUpHashMapper;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyncUpHashRepositoryTest {

    private SyncUpHashRepository syncUpHashRepository;

    @Mock
    SyncUpHashMapper syncUpHashMapper;

    @Before
    public void setUp() throws Exception {
        syncUpHashRepository = new SyncUpHashRepository(syncUpHashMapper);
    }

    @Test
    public void shouldSaveHash() throws Exception {
        syncUpHashRepository.save("whatever hash");
        verify(syncUpHashMapper).insert("whatever hash");
    }

    @Test
    public void shouldTellIfHashExists() {
        when(syncUpHashMapper.find("some hash")).thenReturn(Arrays.asList("some hash"));

        boolean hashExists = syncUpHashRepository.hashExists("some hash");
        assertTrue(hashExists);
    }

    @Test
    public void shouldTellIfHashDoesNotExist() {
        when(syncUpHashMapper.find("some hash")).thenReturn(new ArrayList<String>());

        boolean hashExists = syncUpHashRepository.hashExists("some hash");
        assertFalse(hashExists);
    }
}