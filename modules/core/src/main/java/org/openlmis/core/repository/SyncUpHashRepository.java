package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.SyncUpHashMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class SyncUpHashRepository {

    private SyncUpHashMapper syncUpHashMapper;

    @Autowired
    public SyncUpHashRepository(SyncUpHashMapper syncUpHashMapper) {
        this.syncUpHashMapper = syncUpHashMapper;
    }

    public void save(String hash) {
        syncUpHashMapper.insert(hash);
    }

    public boolean hashExists(String hash) {
        return syncUpHashMapper.find(hash).size() > 0;
    }
}
