package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.GeneralObservation;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralObservationMapper {

  @Insert({"INSERT INTO general_observations (distributionId, facilityId, confirmedByName, confirmedByTitle, verifiedByName, verifiedByTitle, observation)",
    "VALUES (#{distributionId}, #{facilityId}, #{confirmedBy.name}, #{confirmedBy.title}, #{verifiedBy.name}, #{verifiedBy.title}, #{observation})"})
  @Options(useGeneratedKeys = true)
  public void insert(GeneralObservation generalObservation);

  @Select("SELECT * FROM general_observations WHERE distributionId = #{distributionId} AND facilityId = #{facilityId}")
  @Results({
    @Result(property = "verifiedBy.name", column = "verifiedByName"),
    @Result(property = "verifiedBy.title", column = "verifiedByTitle"),
    @Result(property = "confirmedBy.name", column = "confirmedByName"),
    @Result(property = "confirmedBy.title", column = "confirmedByTitle")
  })
  GeneralObservation getByDistributionAndFacility(@Param(value = "distributionId") Long distributionId,
                                                  @Param(value = "facilityId") Long facilityId);
}
