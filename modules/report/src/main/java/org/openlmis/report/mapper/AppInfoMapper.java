/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.report.model.dto.AppInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppInfoMapper {

    @Insert("INSERT INTO moz_app_info (facilityId, userName, appVersion) VALUES(#{facilityId}, #{userName}, #{appVersion})")
    @Options(useGeneratedKeys = true)
    int insert(AppInfo appInfo);

    @Update("UPDATE moz_app_info SET appVersion = #{appVersion} WHERE id = #{id}")
    int update(AppInfo appInfo);

    @Select("SELECT * FROM moz_app_info, facilities WHERE facilities.code = #{facilityCode} AND facilities.id = moz_app_info.facilityId")
    AppInfo queryByFacilityCode(String facilityCode);

    @Select("SELECT moz_app_info.*, facilities.name facilityName FROM moz_app_info, facilities WHERE moz_app_info.facilityId = facilities.id")
    List<AppInfo> queryAll();
}