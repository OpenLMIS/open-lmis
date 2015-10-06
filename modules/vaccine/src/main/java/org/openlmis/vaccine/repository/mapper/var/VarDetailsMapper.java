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
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Repository
public interface VarDetailsMapper {

    @Select("select * from var_details")
    List<VarDetails> getAll();

    @Insert("insert into var_details (awbnumber, flightnumber,destnationairport, estimatetimeofarrival,actualtimeofarrival,numberofitemsinspected,coolanttype,tempraturemonitor, purchaseordernumber,clearingagent,labels,comments,invoice,packinglist,releasecerificate,airwaybill,deliverystatus, createdby) values " +
            "(#{awbnumber}, #{flightnumber},#{destnationairport}, #{estimatetimeofarrival}, #{actualtimeofarrival}, #{numberofitemsinspected},#{coolanttype},#{tempraturemonitor}, #{purchaseordernumber}, #{clearingagent}, #{labels}, #{comments}, #{invoice}, #{packinglist}, #{releasecerificate},#{airwaybill},#{deliverystatus}, #{createdBy})")
    @Options(flushCache = true, useGeneratedKeys = true)
    Integer insert(VarDetails var_details);

    @Update("update var_details " +
            "set " +
            " awbnumber = #{awbnumber}, " +
            " flightnumber = #{flightnumber}, " +
            " destnationairport = #{destnationairport}, " +
            " actualtimeofarrival = #{actualtimeofarrival}, " +
            " estimatetimeofarrival = #{estimatetimeofarrival}, " +
            " numberofitemsinspected = #{numberofitemsinspected}," +
            " coolanttype = #{coolanttype}, " +
            " tempraturemonitor = #{tempraturemonitor}, " +
            " purchaseordernumber = #{purchaseordernumber}, " +
            " clearingagent = #{clearingagent}, " +
            " labels = #{labels}, " +
            " comments = #{comments}, " +
            " invoice = #{invoice}, " +
            " packinglist = #{packinglist}, " +
            " releasecerificate = #{releasecerificate}, " +
            " deliverystatus = #{deliverystatus}, " +
            " airwaybill = #{airwaybill} " +
            "where id = #{id}")
    void update(VarDetails var_details);


    @Select("select * from var_details where id = #{id}")
    VarDetails getById(@Param("id") Long id);


    @Select("select * from var_details where airwaybill = #{airwaybill}")
    List<VarDetails> getByPackageNumber(@Param("airwaybill") String airwaybill);

    @Delete("delete from var_details where id = #{id}")
    void deleteById(@Param("id") Long id);
}
