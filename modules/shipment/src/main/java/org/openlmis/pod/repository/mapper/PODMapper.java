package org.openlmis.pod.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PODMapper {

  @Insert({"INSERT INTO pod_line_items (podId, productCode, quantityReceived, createdBy, modifiedBy) values ",
    "(#{podId}, #{productCode}, #{quantityReceived}, #{createdBy}, #{modifiedBy} )"})
  @Options(useGeneratedKeys = true)
  void insertPODLineItem(PODLineItem podLineItem);

  @Select("SELECT * FROM pod_line_items where podId = #{podId}")
  List<PODLineItem> getPODLineItemsByPODId(Long podId);

  @Insert("INSERT INTO pod (orderId, receivedDate) values (#{orderId}, DEFAULT)")
  @Options(useGeneratedKeys = true)
  void insertPOD(POD pod);

  @Select("SELECT * FROM pod WHERE orderId = #{orderId}")
  POD getPODByOrderId(Long orderId);
}
