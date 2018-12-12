package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.ServiceItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemMapper {
    @Insert({"INSERT INTO service_items(serviceid, requisitionlineitemid, patientsontreatment, createdby, createdDate) values " +
            "(#{serviceId}, #{rnrLineItem.id}, #{serviceItem.patientsOnTreatment}, #{rnrLineItem.createdBy}, #{rnrLineItem.createdDate})"})
    void insert(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "serviceItem") ServiceItem serviceItem, @Param(value = "serviceId") Long serviceId);

    @Delete("DELETE FROM service_items WHERE requisitionlineitemid = #{rnrLineItemId}")
    void deleteByLineItemId(Long rnrLineItemId);

    @Select("SELECT services.code, services.name, service_items.patientsontreatment FROM service_items\n" +
            "JOIN services on service_items.serviceid = services.id\n" +
            " WHERE service_items.requisitionlineitemid = #{rnrLineItemid}")
    List<ServiceItem> getByRnrLineItem(Long rnrLineItemid);
}