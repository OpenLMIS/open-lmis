/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.report.builder;
import org.openlmis.report.model.params.UserSummaryParams;
import org.openlmis.report.model.report.UserSummaryReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class UserSummaryQueryBuilder {

    public static String getQuery(Map params){
     UserSummaryParams filter = (UserSummaryParams)params.get("filterCriteria");
     Map sortCriteria = (Map)params.get("SortCriteria");
     BEGIN();
     SELECT("firstname,lastname,email,cellphone,officephone,supervisorynodename,programname,rolename");
     FROM("vw_user_role_assignments");
     writepredicates(filter);
     ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, UserSummaryReport.class,"firstname asc,lastname asc,email asc"));
        // copy the sql over to a variable, this makes the debugging much more possible.
      String sql = SQL();
        return sql;

    }

    public static void writepredicates(UserSummaryParams filter){
        if(filter != null){
           if(filter.getProgramId() !=0 && filter.getProgramId()!=-1){
              WHERE("programid= #{filterCriteria.programId}");

           }
            if(filter.getRoleId() !=0 && filter.getRoleId()!=-1){
                WHERE("roleid= #{filterCriteria.roleId}");

            }
            if(filter.getSupervisoryNodeId() !=0 && filter.getSupervisoryNodeId()!=-1){
                WHERE("supervisorynodeid= #{filterCriteria.supervisoryNodeId}");

            }
        }

    }


}
