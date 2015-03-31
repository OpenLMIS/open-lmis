package org.openlmis.vaccine.repository.mapper;


import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.DiscardingReason;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscardingReasonMapper {

  @Select("select * from vaccine_discarding_reasons order by displayOrder")
  List<DiscardingReason> getAll();
}
