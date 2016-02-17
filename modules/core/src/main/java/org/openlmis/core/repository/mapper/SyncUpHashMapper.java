package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncUpHashMapper {
    @Insert({"INSERT INTO sync_up_hashes",
            "(hash)",
            "VALUES",
            "(#{hash})"})
    Integer insert(String hash);
}
