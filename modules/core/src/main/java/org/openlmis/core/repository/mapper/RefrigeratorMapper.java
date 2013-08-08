package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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

  @Select({"SELECT RF.*",
    "FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
    "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
    "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
    "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
    "INNER JOIN refrigerators RF ON RF.facilityId = F.id",
    "WHERE DZPS.programId = #{programId} AND F.active = true",
    "AND PS.programId = #{programId}  AND DZM.deliveryZoneId = #{deliveryZoneId} order by F.name"})
  List<Refrigerator> getRefrigeratorsForADeliveryZoneAndProgram(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);

}
