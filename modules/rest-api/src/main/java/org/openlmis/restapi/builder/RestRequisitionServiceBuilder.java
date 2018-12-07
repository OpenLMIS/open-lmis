package org.openlmis.restapi.builder;


import java.util.Date;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.JOIN;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class RestRequisitionServiceBuilder {
    public static String getQuery(Map params) {
        Date afterUpdatedTime = (Date)params.get("afterUpdatedTime");
        String programCode = (String)params.get("programCode");

        BEGIN();
        SELECT("s.code, s.name, s.active, p.code as programcode");
        FROM("services s");
        JOIN("programs p on s.programid = p.id");
        if(null != afterUpdatedTime) {
            WHERE("s.modifieddate >= #{afterUpdatedTime}");
        }
        if(null != programCode) {
            WHERE("p.code = #{programCode}");
        }

        return SQL();
    }
}