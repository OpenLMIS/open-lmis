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

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.SMS;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SMSMapper {

    @Insert("INSERT INTO sms(message,direction, phoneNumber, dateSaved, sent) VALUES (#{message},#{direction}, #{phoneNumber}, #{dateSaved}, #{sent})")
    @Options(useGeneratedKeys = true)
    void insert(SMS sms);

    @Select("SELECT * FROM sms")
    public List<SMS> getAllSMSMessage();

    @Select("select * from sms where phoneNumber = #{mobile}")
    public java.util.List<SMS> getForMobile(@Param("mobile") String mobile);


}
