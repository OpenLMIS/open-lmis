package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.rnr.domain.Service;
import org.openlmis.rnr.domain.ServiceLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceLineItemMapper {

    @Insert({"INSERT INTO service_line_items(rnrid, serviceid, programdatacolumnid, value, createdby, createddate) values " +
            "(#{rnrId}, #{serviceId}, #{programDataColumnId}, #{value}, #{createdBy}, #{createdDate})"})
    @Options(useGeneratedKeys = true)
    void insert(ServiceLineItem serviceLineItem);

    @Select("SELECT * FROM service_line_items)")
    List<String> getAll();

    @Select("SELECT * from service_line_items where rnrid = #{rnrId}")
    @Results(value = {
            @Result(property = "service", column = "serviceId", javaType = Service.class,
                    one = @One(select = "org.openlmis.rnr.repository.mapper.ServiceMapper.getById")),
            @Result(property = "programDataColumn", column = "programDataColumnId", javaType = ProgramDataColumn.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProgramDataColumnMapper.getColumnById"))
    })
    List<ServiceLineItem> getServiceLineItemsByRnrId(Long rnrId);

}
