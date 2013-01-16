package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

@Repository
public interface RnrMapper {

  @Insert("insert into requisition(facilityId, programId, status, modifiedBy) " +
      "values (#{facilityId}, #{programId}, #{status}, #{modifiedBy})")
  @Options(useGeneratedKeys = true)
  public void insert(Rnr requisition);

  @Update({"UPDATE requisition SET",
      "modifiedBy = #{modifiedBy},",
      "status = #{status},",
      "modifiedDate = DEFAULT,",
      "fullSupplyItemsSubmittedCost = #{fullSupplyItemsSubmittedCost},",
      "nonFullSupplyItemsSubmittedCost = #{nonFullSupplyItemsSubmittedCost}",
      "WHERE id = #{id}"})
  public void update(Rnr requisition);

  @Select("Select * from requisition where id = #{rnrId}")
  public Rnr getRequisitionById(Integer rnrId);

  @Select("Select * from requisition where facilityId = #{facilityId} and programId= #{programId}")
  public Rnr getRequisitionByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                @Param("programId") Integer programId);

  @Select("SELECT * FROM requisition WHERE id = #{rnrId}")
  Rnr getById(Integer rnrId);
}
