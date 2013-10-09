package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.FacilityVisit;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityVisitMapper {

  @Insert({"INSERT INTO facility_visits (distributionId, facilityId, confirmedByName, confirmedByTitle, verifiedByName, verifiedByTitle, observations, createdBy, modifiedBy)",
    "VALUES (#{distributionId}, #{facilityId}, #{confirmedBy.name}, #{confirmedBy.title}, #{verifiedBy.name}, #{verifiedBy.title}, #{observations}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(FacilityVisit facilityVisit);

  @Select("SELECT * FROM facility_visits WHERE distributionId = #{distributionId} AND facilityId = #{facilityId}")
  @Results({
    @Result(property = "verifiedBy.name", column = "verifiedByName"),
    @Result(property = "verifiedBy.title", column = "verifiedByTitle"),
    @Result(property = "confirmedBy.name", column = "confirmedByName"),
    @Result(property = "confirmedBy.title", column = "confirmedByTitle")
  })
  FacilityVisit getByDistributionAndFacility(@Param(value = "distributionId") Long distributionId,
                                                  @Param(value = "facilityId") Long facilityId);
}
