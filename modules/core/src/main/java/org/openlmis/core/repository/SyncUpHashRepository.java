
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

}
