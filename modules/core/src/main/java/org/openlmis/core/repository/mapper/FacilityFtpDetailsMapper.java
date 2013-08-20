/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityFtpDetailsMapper {

  @Insert({"INSERT INTO facility_ftp_details (facilityCode, serverHost, serverPort, userName, password, localFolderPath, createdBy, modifiedBy) ",
    "VALUES (#{facilityCode}, #{serverHost}, #{serverPort}, #{userName}, #{password}, #{localFolderPath}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(FacilityFtpDetails facilityFtpDetails);

  @Select({"SELECT * FROM facility_ftp_details WHERE facilityCode = #{facilityCode}"})
  FacilityFtpDetails getByFacilityCode(String facilityCode);

  @Update({"UPDATE facility_ftp_details SET serverHost = #{serverHost}, serverPort = #{serverPort}, userName = #{userName}, ",
    "password = #{password}, localFolderPath = #{localFolderPath}, ",
    "modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id = #{id}"})
  void update(FacilityFtpDetails facilityFtpDetails);

}
