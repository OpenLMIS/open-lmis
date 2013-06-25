package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.SupplyLineList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Muhammad Ahmed
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface SupplyLineListMapper {

    @Select("SELECT " +
            "sl.id, sl.description, p.name AS program,f.name AS facility, n.name As supervisorynode " +
            "FROM public.supply_lines AS sl " +
            "JOIN public.programs AS p ON p.id = sl.programid " +
            "JOIN public.facilities AS f ON f.id = sl.supplyingfacilityid " +
            "INNER JOIN public.supervisory_nodes AS n ON n.id = sl.supervisorynodeid")
    List<SupplyLineList> getAll();
}
