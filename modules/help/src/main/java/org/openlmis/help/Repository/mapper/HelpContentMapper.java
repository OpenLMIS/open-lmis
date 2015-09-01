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

package org.openlmis.help.Repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by seifu on 10/20/2014.
 */

@Repository
public interface HelpContentMapper {
    @Insert({"INSERT INTO elmis_help",
            "(helpTopicId, name, htmlcontent, imagelink, createddate, createdby, modifiedby, modifieddate) ",
            "VALUES(#{helpTopic.id}, #{name}, #{htmlContent}, #{imageLink}, #{createdDate}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(HelpContent helpContent);

    /*

     */
    @Select("SELECT eh.* FROM elmis_help eh INNER JOIN elmis_help_topic et on eh.helpTopicId=et.id  ")
    @Results({
            @Result(column = "helpTopicId", property = "helpTopic.id")

    })
    List<HelpContent> getHelpContentList();

    /*

     */
    @Select("SELECT * FROM elmis_help where helptopicid = #{id} ")
    List<HelpContent> getHelpTopcicContentList(@Param(value = "id") Long id);

    /*

    */
    @Select("SELECT * FROM elmis_help where id = #{id} ")
    HelpContent get(@Param(value = "id") Long id);

    @Update("UPDATE elmis_help" +
            "   SET name= #{name}," +
            " modifiedby=#{modifiedBy}, " +
            "htmlcontent=#{htmlContent}, " +
            "imagelink=#{imageLink}, " +
            "modifieddate=#{modifiedDate}," +
            " helptopicid=#{helpTopic.id}" +
            " WHERE id=#{id};")
    void update(HelpContent helpContent);
}
