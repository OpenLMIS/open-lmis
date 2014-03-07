/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.springframework.stereotype.Repository;

/**
 * FacilityFtpDetailsMapper maps the FacilityFtpDetails entity to corresponding representation in database.
 */
@Repository
public interface FacilityFtpDetailsMapper {

  @Insert({"INSERT INTO facility_ftp_details (facilityId, serverHost, serverPort, userName, password, localFolderPath, createdBy, modifiedBy, modifiedDate) ",
    "VALUES (#{facility.id}, #{serverHost}, #{serverPort}, #{userName}, #{password}, #{localFolderPath}, #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))"})
  @Options(useGeneratedKeys = true)
  public void insert(FacilityFtpDetails facilityFtpDetails);

  @Select({"SELECT * FROM facility_ftp_details WHERE facilityId = #{id}"})
  @Results(value = {
    @Result(property = "facility.id", column = "facilityId")
  })
  FacilityFtpDetails getByFacilityId(Facility facility);

  @Update({"UPDATE facility_ftp_details SET serverHost = #{serverHost}, serverPort = #{serverPort}, userName = #{userName}, ",
    "password = #{password}, localFolderPath = #{localFolderPath}, ",
    "modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id = #{id}"})
  void update(FacilityFtpDetails facilityFtpDetails);

}
