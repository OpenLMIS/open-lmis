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

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.ByteArrayTypeHandler;
import org.openlmis.odkapi.domain.ODKSubmissionData;
import org.springframework.stereotype.Repository;

@Repository
public interface ODKSubmissionDataMapper {

    @Insert("INSERT INTO odk_submission_data(" +
            "ODKSubmissionId, " +
            "facilityId, " +
            "GPSLatitude,GPSLongitude, GPSAltitude, GPSAccuracy, firstPicture, secondPicture, thirdPicture, fourthPicture, fifthPicture, comment) " +
            "VALUES (#{ODKSubmissionId}, " +
            "#{facilityId}, #{GPSLatitude}, #{GPSLongitude}, #{GPSAltitude}, #{GPSAccuracy}, " +
            "#{firstPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{secondPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{thirdPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{fourthPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}," +
            "#{fifthPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{comment})")
    @Options(useGeneratedKeys = true)
    void insert(ODKSubmissionData odkSubmissionData);

}
