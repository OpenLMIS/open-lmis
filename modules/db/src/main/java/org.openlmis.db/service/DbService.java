package org.openlmis.db.service;

import lombok.NoArgsConstructor;
import org.openlmis.db.repository.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@NoArgsConstructor
public class DbService {

  @Autowired
  private DbRepository dbRepository;

  public int getCount(String tableName) {
    return dbRepository.getCount(tableName);
  }

  public Date getCurrentTimestamp() {
    return dbRepository.getCurrentTimeStamp();
  }
}
