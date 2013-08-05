package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorReading;
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

  @Select({"SELECT RF.brand,RF.model,RF.serialNumber,RF.facilityId,RF.createdBy,RF.createdDate,RF.modifiedBy,RF.modifiedDate",
    "FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
    "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
    "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
    "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
    "LEFT OUTER JOIN refrigerators RF ON RF.facilityId = F.id",
    "WHERE DZPS.programId = #{programId} AND F.active = true",
    "AND PS.programId = #{programId}  AND DZM.deliveryZoneId = #{deliveryZoneId} order by F.name"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "refrigerators", column = "id", javaType = List.class,
      many = @Many(select = "org.openlmis.distribution.repository.mapper.RefrigeratorMapper.getRefrigerators")),
  })
  List<Refrigerator> getRefrigeratorInfoForADeliveryZoneProgram(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);


 @Select({"SELECT * from distribution_refrigerator_reading where refrigeratorid=#{refrigeratorid} AND distributionId=#{distributionId}"})
 RefrigeratorReading getReadingByDistribution(Long distributionId,Long refrigeratorId);

}
