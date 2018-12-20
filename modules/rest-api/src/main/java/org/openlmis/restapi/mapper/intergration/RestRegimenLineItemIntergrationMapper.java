package org.openlmis.restapi.mapper.intergration;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.restapi.domain.integration.RegimenLineItemIntergration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestRegimenLineItemIntergrationMapper {

    @Select({"SELECT facilities.code AS facilityCode,\n" +
            "  rli.rnrid AS requisitionId,\n" +
            "  programs.code AS programCode,\n" +
            "  regimens.code AS regimeCode,\n" +
            "  rli.patientsontreatment AS patientsOnTreatment\n" +
            "FROM requisitions\n" +
            "JOIN regimen_line_items rli ON requisitions.id = rli.rnrid\n" +
            "JOIN regimens ON rli.code = regimens.code\n" +
            "JOIN programs ON programs.id = regimens.programid\n" +
            "JOIN facilities ON requisitions.facilityid = facilities.id\n" +
            "WHERE requisitions.id = #{requisitionId} AND regimens.skipped = FALSE"})
    List<RegimenLineItemIntergration> getRegimenLineItems(@Param(value = "requisitionId") Integer requisitionId);
}