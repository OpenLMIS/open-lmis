package org.openlmis.pod.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.pod.domain.PODLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PODMapper {

  @Insert({"INSERT INTO pod_line_items (orderId, productCode, quantityReceived, createdBy, modifiedBy) values " ,
    "(#{orderId}, #{productCode}, #{quantityReceived}, #{createdBy}, #{modifiedBy} )"})
  void insert(PODLineItem podLineItem);

  @Select("SELECT * FROM pod_line_items where orderId = #{orderId}")
  List<PODLineItem> getPODLineItemsByOrderId(Long orderId);
}
