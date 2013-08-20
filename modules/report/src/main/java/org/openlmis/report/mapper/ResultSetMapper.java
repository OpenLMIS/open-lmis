/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 */
//@Component
//@Data
public class ResultSetMapper {

    private QueryExecutor  queryExecutor;

    @Autowired
    public ResultSetMapper(QueryExecutor queryExecutor){
        this.queryExecutor = queryExecutor;
    }

    public Map<String,Object> executeQuery(String query){
        return queryExecutor.execute(query);
    }

    @Repository
    private interface QueryExecutor{

        String  q="#{statement}";
        @Select(q)
        public Map<String , Object> execute(@Param(value = "statement") String statement);
    }


}
