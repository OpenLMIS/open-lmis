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

package org.openlmis.vaccine.repository.mapper.var;

import org.openlmis.vaccine.domain.var.VarItemAlarms;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Repository
public interface VarItemAlarmsMapper {
    
    @Select("select * from var_item_alarms")
    List<VarItemAlarms> getAll();

    @Insert("insert into var_item_alarms (vardetailsid, productid, boxnumber,lotnumber,alarmtemprature,coldchainmonitor,gtinlookupid,timeofinspection,createdby) values " +
            "(#{vardetailsid}, #{productid}, #{boxnumber}, #{lotnumber}, #{alarmtemprature}, #{coldchainmonitor}, #{gtinlookupid}, #{timeofinspection}, #{createdBy})")
    @Options(flushCache = true, useGeneratedKeys = true)
    Integer insert(VarItemAlarms var_item_alarms);

    @Update("update var_item_alarms " +
            "set " +
            " vardetailsid = #{vardetailsid}, " +
            " productid = #{productid}, " +
            " boxnumber = #{boxnumber}, " +
            " lotnumber = #{lotnumber}, " +
            " alarmtemprature = #{alarmtemprature}, " +
            " coldchainmonitor = #{coldchainmonitor}, " +
            " gtinlookupid = #{gtinlookupid}, " +
            " timeofinspection = #{timeofinspection} " +
            "where id = #{id}")
    void update(VarItemAlarms var_item_alarms);


    @Select("select * from var_item_alarms where id = #{id}")
    VarItemAlarms getById(@Param("id") Long id);

    @Delete("delete from var_item_alarms where id = #{id}")
    void deleteById(@Param("id") Long id);
}