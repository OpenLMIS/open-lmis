package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Elias Muluneh
 */
@Repository
public interface ProductFormReportMapper {

    @Select("SELECT * " +
            "   FROM " +
            "       product_forms order by displayorder")

    List<ProductForm> getAll();
}
