package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrMapper {

  @Insert("INSERT INTO requisition(facilityId, programId, periodId, status, modifiedBy) " +
    "VALUES (#{facilityId}, #{programId}, #{periodId}, #{status}, #{modifiedBy})")
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

  @Select("SELECT * FROM requisition WHERE id = #{rnrId}")
  public Rnr getRequisitionById(Integer rnrId);

  @Select("SELECT * FROM requisition WHERE facilityId = #{facilityId} AND programId= #{programId} AND periodId = #{periodId}")
  public Rnr getRequisition(@Param("facilityId") Integer facilityId,
                            @Param("programId") Integer programId,
                            @Param("periodId") Integer periodId);

  // TODO: Duplicate method
  @Select("SELECT * FROM requisition WHERE id = #{rnrId}")
  Rnr getById(Integer rnrId);


  @Select({"SELECT * FROM requisition WHERE facilityId = ANY (#{commaSeparatedFacilities}::INTEGER[])",
    "AND programId =  ANY (#{commaSeparatedPrograms}::INTEGER[])",
    "AND status = 'SUBMITTED'"})
  List<Rnr> getSubmittedRequisitionsForFacilitiesAndPrograms(@Param("commaSeparatedFacilities") String commaSeparatedFacilities,
                                                             @Param("commaSeparatedPrograms") String commaSeparatedPrograms);
}
