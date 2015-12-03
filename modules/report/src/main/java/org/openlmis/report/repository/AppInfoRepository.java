/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.report.repository;

import org.openlmis.report.mapper.AppInfoMapper;
import org.openlmis.report.model.dto.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AppInfoRepository {

    @Autowired
    AppInfoMapper appInfoMapper;

    public int create(AppInfo appInfo) {
        return appInfoMapper.insert(appInfo);
    }

    public int update(AppInfo appInfo) {
        return appInfoMapper.update(appInfo);
    }

    public AppInfo getAppInfoByFacilityCode(String facilityCode) {
        return appInfoMapper.queryByFacilityCode(facilityCode);
    }
}
