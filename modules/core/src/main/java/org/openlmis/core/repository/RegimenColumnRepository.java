package org.openlmis.core.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.repository.mapper.RegimenColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegimenColumnRepository {

  @Autowired
  RegimenColumnMapper mapper;

  public void insert(RegimenColumn regimenColumn) {
    mapper.insert(regimenColumn);
  }

  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId) {
    return mapper.getAllRegimenColumnsByProgramId(programId);
  }

  public void update(RegimenColumn regimenColumn) {
    mapper.update(regimenColumn);
  }

}
