package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionColumns;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineOrderRequisitionColumnMapper {

    @Select("Select * from vaccine_order_requisition_master_columns")
    List<VaccineOrderRequisitionColumns>getColumns();

}
