package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.RegimenCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
public interface RegimenCategoryReportMapper {

    @Select("SELECT id,code,name FROM regimen_categories ORDER BY displayOrder, name")
    List<RegimenCategory> getAll();

    @Select("SELECT * FROM regimen_categories WHERE id = #{id}")
            List<RegimenCategory>getById(Long id);
}
