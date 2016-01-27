package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionStatusChange;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineStatusRequisitionChangeMapper {

    @Insert("INSERT into vaccine_order_requisition_status_changes (orderId, status, createdBy, createdDate, modifiedBy, modifiedDate) " +
            " values " +
            " (#{orderId}, #{status}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
    @Options(useGeneratedKeys = true)
    Integer insert(VaccineOrderRequisitionStatusChange change);

    @Select("SELECT sc.status, sc.orderId, sc.createdDate as date,  u.username, u.firstName, u.lastName  from vaccine_order_requisition_status_changes sc join users u on u.id = sc.createdBy where orderId = #{orderId}")
    List<VaccineOrderRequisitionStatusChange> getChangeLogByReportId(@Param("orderId") Long orderId);

    @Select("SELECT sc.status, sc.reportId, sc.createdDate as date, u.username, u.firstName, u.lastName " +
            "from vaccine_order_requisition_status_changes sc join users u on u.id = sc.createdBy " +
            "where orderId = #{orderId} and status = #{operation} " +
            "order by sc.createdDate desc limit 1")
    VaccineOrderRequisitionStatusChange getOperationLog(@Param("reportId") Long orderId, @Param("operation") VaccineOrderStatus status);

}
