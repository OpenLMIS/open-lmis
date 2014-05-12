package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
public interface RegimenReportMapper{

     @Select("SELECT * FROM regimens R INNER JOIN regimen_categories RC ON R.categoryId = RC.id\n" +
             "         ORDER BY RC.displayOrder,R.displayOrder")


    List<Regimen> getByProgram();

    @Select("SELECT * FROM regimens ORDER BY displayOrder, name")
    List<Regimen> getAll();


   @Select("SELECT * FROM regimens R INNER JOIN regimen_categories RC ON R.categoryId = RC.id\n" +
           "  where categoryid = #{categoryid} ORDER BY RC.displayOrder,R.displayOrder")
    List<Regimen>getRegimenByCategory(Long id);


}
