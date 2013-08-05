package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Refrigerator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefrigeratorMapper {

  @Insert({"INSERT INTO refrigerators",
    "(brand, model, serialNumber, facilityId, createdBy, modifiedBy)",
    "VALUES",
    "(#{brand}, #{model}, #{serialNumber}, #{facilityId} ,#{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Refrigerator refrigerator);

  @Select({"SELECT * FROM refrigerators rf JOIN facilities f ON f.id = rf.facilityid WHERE f.id=#{id}"})
  List<Refrigerator> getRefrigerators(Long id);


}
