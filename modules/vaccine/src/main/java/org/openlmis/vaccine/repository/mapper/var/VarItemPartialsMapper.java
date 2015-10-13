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

import org.openlmis.vaccine.domain.var.VarItemPartials;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Repository
public interface VarItemPartialsMapper {

    @Select("select * from var_item_partials")
    List<VarItemPartials> getAll();

    @Insert("insert into var_item_partials (vardetailsid, productid, boxnumber,lotnumber,expectednumber,availablenumber,gtinlookupid,createdby) values " +
            "(#{vardetailsid}, #{productid}, #{boxnumber}, #{lotnumber}, #{expectednumber}, #{availablenumber}, #{gtinlookupid},  #{createdBy})")
    @Options(flushCache = true, useGeneratedKeys = true)
    Integer insert(VarItemPartials var_item_partials);

    @Update("update var_item_partials " +
            "set " +
            " vardetailsid = #{vardetailsid}, " +
            " productid = #{productid}, " +
            " boxnumber = #{boxnumber}, " +
            " lotnumber = #{lotnumber}, " +
            " expectednumber = #{expectednumber}, " +
            " availablenumber = #{availablenumber}, " +
            " gtinlookupid = #{gtinlookupid} " +
            "where id = #{id}")
    void update(VarItemPartials var_item_partials);


    @Select("select * from var_item_partials where id = #{id}")
    VarItemPartials getById(@Param("id") Long id);

    @Delete("delete from var_item_partials where id = #{id}")
    void deleteById(@Param("id") Long id);

}
