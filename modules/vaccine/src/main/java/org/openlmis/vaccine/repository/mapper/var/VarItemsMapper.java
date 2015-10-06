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

import org.openlmis.vaccine.domain.var.VarDetails;
import org.openlmis.vaccine.domain.var.VarItems;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Repository
public interface VarItemsMapper {

    @Select("select * from var_items")
    List<VarItems> getAll();

    @Insert("insert into var_items (vardetailsid, shipmentnumber, productid,manufacturedate,expiredate,lotnumber,numberofdoses, derliverystatus,numberreceived,physicaldamage,damagedamount,vvmstatus,problems,createdby,modifiedby, gtinlookupid) values " +
            "(#{vardetailsid}, #{shipmentnumber}, #{productid}, #{manufacturedate},#{expiredate},#{lotnumber}, #{numberofdoses}, #{derliverystatus}, #{numberreceived}, #{physicaldamage}, #{damagedamount}, #{vvmstatus}, #{problems},#{createdby}, #{modifiedby}, #{gtinlookupid})")
    @Options(flushCache = true, useGeneratedKeys = true)
    Integer insert(VarItems var_items);

    @Update("update var_items " +
            "set " +
            " vardetailsid = #{vardetailsid}, " +
            " shipmentnumber = #{shipmentnumber}, " +
            " productid = #{productid}, " +
            " manufacturedate = #{manufacturedate}, " +
            " expiredate = #{expiredate}," +
            " lotnumber = #{lotnumber}, " +
            " numberofdoses = #{numberofdoses}, " +
            " derliverystatus = #{derliverystatus}, " +
            " numberreceived = #{numberreceived}, " +
            " physicaldamage = #{physicaldamage}, " +
            " damagedamount = #{damagedamount}, " +
            " vvmstatus = #{vvmstatus}, " +
            " problems = #{problems}, " +
            " modifiedby = #{modifiedby}, " +
            " gtinlookupid = #{gtinlookupid} " +
            "where id = #{id}")
    void update(VarItems var_items);


    @Select("select * from var_items where id = #{id}")
    VarItems getById(@Param("id") Long id);

    @Select("select * from var_items where shipmentnumber = #{shipmentnumber}")
    List<VarItems> getItemsByPackage(@Param("shipmentnumber") String shipmentnumber);

    @Select("select * from var_items where shipmentnumber = #{shipmentnumber} and lotnumber = #{lotnumber}")
    List<VarItems> getItemsByLot(@Param("shipmentnumber") String shipmentnumber,@Param("lotnumber") String lotnumber);

    @Delete("delete from var_items where id = #{id}")
    void deleteById(@Param("id") Long id);
}
