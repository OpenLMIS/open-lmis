package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryZoneMemberMapper {

  @Insert({"INSERT INTO delivery_zone_members(deliveryZoneId, facilityId, createdBy, modifiedBy, modifiedDate)",
      "VALUES(#{deliveryZone.id}, #{facility.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZoneMember member);

  @Update("UPDATE delivery_zone_members SET modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id = #{id}")
  void update(DeliveryZoneMember member);

  @Select({"SELECT DZM.* FROM delivery_zone_members DZM INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
  "INNER JOIN facilities F ON DZM.facilityId = F.id WHERE F.code = #{facilityCode} AND DZ.code = #{deliveryZoneCode}"})
      @Results({
          @Result(column = "deliveryZoneId", property = "deliveryZone.id"),
          @Result(column = "facilityId", property = "facility.id")
      })
  DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(@Param("deliveryZoneCode") String deliveryZoneCode,
                                                          @Param("facilityCode") String facilityCode);

  @Select({"SELECT DZPS.programId from delivery_zone_program_schedules DZPS",
      "INNER JOIN delivery_zone_members DZM ON DZM.deliveryZoneId = DZPS.deliveryZoneId AND DZM.facilityId = #{facilityId}"})
  List<Long> getDeliveryZoneProgramIdsForFacility(Long facilityId);
}
