package org.openlmis.db.repository;


import lombok.NoArgsConstructor;
import org.openlmis.db.repository.mapper.DbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
@NoArgsConstructor
public class DbRepository {

  @Autowired
  DbMapper dbMapper;

  public Date getCurrentTimeStamp() {
    return dbMapper.getCurrentTimeStamp();
  }

  public int getCount(String table) {
    return dbMapper.getCount(table);
  }
}
