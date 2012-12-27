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

    @Delete("delete from requisition")
    public void deleteAll();

    @Update("update requisition set modifiedBy = #{modifiedBy}, status = #{status}, modifiedDate= DEFAULT, " +
			"fullSupplyItemsSubmittedCost = #{fullSupplyItemsSubmittedCost}, " +
			"nonFullSupplyItemsSubmittedCost = #{nonFullSupplyItemsSubmittedCost}, " +
			"totalSubmittedCost = #{totalSubmittedCost} " +
			"where id = #{id}")
    public void update(Rnr requisition);

    @Select("Select * from requisition where id = #{rnrId}")
      public Rnr getRequisitionById(Integer rnrId);

    @Select("Select * from requisition where facilityId = #{facilityId} and programId= #{programId}")
    public Rnr getRequisitionByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                  @Param("programId") Integer programId);
}
