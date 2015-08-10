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

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.odkapi.domain.ODKStorageSurveySubmission;
import org.springframework.stereotype.Repository;

@Repository
public interface ODKStorageSurveySubmissionMapper {

    @Insert("INSERT INTO odk_znz_survey_storage_submission(" +
            "ODKSubmissionId," +
            "facilityId," +
            "adequateStorageSpace," +
            "adequateShelves," +
            "storeRoomClean," +
            "productsArrangedAppropriately," +
            "productsStoredIssued," +
            "medicinesStoredSeparately," +
            "coldChainFollowed," +
            "productsFreeFromDusts," +
            "productsFreeFromMoisture," +
            "productsFreeFromSunlight," +
            "storeRoomPreventedFromInfestation," +
            "adequateSecurity," +
            "fireExtinguisherAvailable," +
            "storeRoomConditionConductive," +
            "controlForUnauthorizedPersonnel," +
            "totalPercentage," +
            "GPSLatitude," +
            "GPSLongitude," +
            "GPSAltitude," +
            "GPSAccuracy," +
            "firstPicture," +
            "secondPicture," +
            "thirdPicture) " +
            "VALUES " +
            "( " +
            " #{ODKSubmissionId}," +
            " #{facilityId}," +
            " #{adequateStorageSpace}," +
            " #{adequateShelves}," +
            " #{storeRoomClean}," +
            " #{productsArrangedAppropriately}," +
            " #{productsStoredIssued}," +
            " #{medicinesStoredSeparately}," +
            " #{coldChainFollowed}," +
            " #{productsFreeFromDusts}," +
            " #{productsFreeFromMoisture}," +
            " #{productsFreeFromSunlight}," +
            " #{storeRoomPreventedFromInfestation}," +
            " #{adequateSecurity}," +
            " #{fireExtinguisherAvailable}," +
            " #{storeRoomConditionConductive}," +
            " #{controlForUnauthorizedPersonnel}," +
            " #{totalPercentage}," +
            " #{GPSLatitude}," +
            " #{GPSLongitude}," +
            " #{GPSAltitude}," +
            " #{GPSAccuracy}," +
            "#{firstPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{secondPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}, " +
            "#{thirdPicture, typeHandler=org.apache.ibatis.type.ByteArrayTypeHandler}" +
            ")"
    )
    @Options(useGeneratedKeys = true)
    void insert(ODKStorageSurveySubmission odkStorageSurveySubmission);

    // TODO find another method to do this
    @Select("SELECT MAX(id) FROM odk_znz_survey_storage_submission")
    public Long getLastId();
}
