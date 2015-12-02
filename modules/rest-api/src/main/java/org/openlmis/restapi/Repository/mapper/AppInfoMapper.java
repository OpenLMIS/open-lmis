/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.restapi.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.restapi.domain.AppInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface  AppInfoMapper {

    @Insert("INSERT INTO moz_app_info (facilityId, appVersion) VALUES(#{facilityId}, #{appVersion})")
    @Options(useGeneratedKeys = true)
    int insert(AppInfo appInfo);

    @Update("UPDATE moz_app_info SET appVersion = #{appVersion} WHERE id = #{id}")
    int update(AppInfo appInfo);

    @Select("SELECT * FROM moz_app_info, facilities WHERE facilities.code = #{facilityCode} ")
    AppInfo queryVersionByFacilityCode(String facilityCode);

}