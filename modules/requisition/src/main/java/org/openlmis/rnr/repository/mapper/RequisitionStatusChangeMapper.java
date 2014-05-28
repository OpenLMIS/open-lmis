/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * It maps the RequisitionStatusChange entity to corresponding representation in database.
 */

@Repository
public interface RequisitionStatusChangeMapper {

  @Insert({"INSERT INTO requisition_status_changes",
    "(rnrId, status, userName, createdBy, modifiedBy) VALUES ",
    "(#{rnrId}, #{status}, #{userName}, #{createdBy.id}, #{createdBy.id})"})
  @Options(useGeneratedKeys = true)
  void insert(RequisitionStatusChange statusChange);

  @Select("SELECT max(createdDate) FROM requisition_status_changes WHERE rnrId = #{rnrId} AND status = #{status}")
  Date getOperationDateFor(@Param("rnrId") Long rnrId, @Param("status") String status);

  @Select({"SELECT rsc.*, u.firstName, u.lastName, u.id as userId from requisition_status_changes rsc",
    "INNER JOIN users u ON rsc.createdBy = u.id WHERE rnrId = #{rnrId}"})
  @Results({
    @Result(column = "firstName", property = "createdBy.firstName"),
    @Result(column = "lastName", property = "createdBy.lastName"),
    @Result(column = "userId", property = "createdBy.id")
  })
  List<RequisitionStatusChange> getByRnrId(Long rnrId);
}
