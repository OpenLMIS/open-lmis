package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionMapper {

  @Insert("INSERT INTO requisition(facilityId, programId, periodId, status, modifiedBy) " +
      "VALUES (#{facilityId}, #{programId}, #{periodId}, #{status}, #{modifiedBy})")
  @Options(useGeneratedKeys = true)
  void insert(Rnr requisition);

  @Update({"UPDATE requisition SET",
      "modifiedBy = #{modifiedBy},",
      "status = #{status},",
      "modifiedDate = DEFAULT,",
      "fullSupplyItemsSubmittedCost = #{fullSupplyItemsSubmittedCost},",
      "submittedDate = #{submittedDate},",
      "nonFullSupplyItemsSubmittedCost = #{nonFullSupplyItemsSubmittedCost},",
      "supervisoryNodeId = #{supervisoryNodeId}",
      "WHERE id = #{id}"})
  void update(Rnr requisition);

  @Select("SELECT * FROM requisition WHERE id = #{rnrId}")
  Rnr getById(Integer rnrId);

  @Select({"SELECT R.id AS id, R.submittedDate AS submittedDate, R.modifiedDate AS modifiedDate, P.name AS programName,",
      "F.code AS facilityCode, F.name AS facilityName, PP.startDate AS periodStartDate, PP.endDate AS periodEndDate",
      "FROM requisition R",
      "INNER JOIN facilities F ON R.facilityId = F.id",
      "INNER JOIN programs P ON R.programId = P.id",
      "INNER JOIN processing_periods PP ON R.periodId = PP.id",
      "AND R.programId =  #{programId}",
      "AND R.supervisoryNodeId =  #{supervisoryNode.id}",
      "AND P.active =  'true'",
      "AND R.status = 'AUTHORIZED'"})
  List<RnrDTO> getAuthorizedRequisitions(RoleAssignment roleAssignment);

  @Select("SELECT * FROM requisition WHERE facilityId = #{facilityId} AND programId= #{programId} AND periodId = #{periodId}")
  Rnr getRequisition(@Param("facilityId") Integer facilityId,
                     @Param("programId") Integer programId,
                     @Param("periodId") Integer periodId);

  @Select("SELECT * FROM requisition " +
      "WHERE facilityId = #{facilityId} " +
      "AND programId = #{programId} " +
      "AND status NOT IN ('INITIATED', 'SUBMITTED') " +
      "ORDER BY submittedDate DESC " +
      "LIMIT 1")
  Rnr getLastRequisitionToEnterThePostSubmitFlow(@Param(value = "facilityId") Integer facilityId,
                                                 @Param(value = "programId") Integer programId);
}
