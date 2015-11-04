package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.inventory.EquipmentAlert;
import org.openlmis.vaccine.repository.mapper.inventory.builder.VaccineInventoryDashboardQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineInventoryDashboardMapper {

    @SelectProvider(type = VaccineInventoryDashboardQueryBuilder.class, method = "getNonFunctionalAlerts")
    @Results(value = {
            @Result(property = "changeBy", column = "modifiedby")})
    List<EquipmentAlert> getNonFunctionalAlerts(@Param("facilities") String facilities);
}
