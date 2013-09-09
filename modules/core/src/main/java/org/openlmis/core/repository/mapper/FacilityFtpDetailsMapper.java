/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityFtpDetailsMapper {

  @Insert({"INSERT INTO facility_ftp_details (facilityId, serverHost, serverPort, userName, password, localFolderPath, createdBy, modifiedBy, modifiedDate) ",
    "VALUES (#{facility.id}, #{serverHost}, #{serverPort}, #{userName}, #{password}, #{localFolderPath}, #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))"})
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
