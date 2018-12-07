package org.openlmis.restapi.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.restapi.builder.RestRequisitionServiceBuilder;
import org.openlmis.restapi.domain.RequisitionServiceResponse;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RestRequisitionServiceMapper {

    @SelectProvider(type = RestRequisitionServiceBuilder.class, method = "getQuery")
    List<RequisitionServiceResponse> getRequisitonServices(@Param("afterUpdatedTime") Date afterUpdatedTimeInDate,
            @Param("programCode") String programCode
    );
}