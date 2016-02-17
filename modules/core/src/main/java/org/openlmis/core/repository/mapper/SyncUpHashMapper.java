package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncUpHashMapper {
    @Insert({"INSERT INTO sync_up_hashes",
            "(hash)",
            "VALUES",
            "(#{hash})"})
    Integer insert(String hash);

    @Delete("DELETE from sync_up_hashes")
    void deleteAll();

    @Select({"SELECT hash from sync_up_hashes where hash=#{hash}"})
    List<String> find(String hash);
}
