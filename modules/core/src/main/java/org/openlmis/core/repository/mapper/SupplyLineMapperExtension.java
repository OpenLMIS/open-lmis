/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyLineMapperExtension extends SupplyLineMapper{

    @Select("SELECT " +
            "sl.id, sl.description, p.name AS programName,f.name AS facilityName, n.name As supervisorynodeName " +
            "FROM supply_lines AS sl " +
            "JOIN programs AS p ON p.id = sl.programid " +
            "JOIN facilities AS f ON f.id = sl.supplyingfacilityid " +
            "INNER JOIN supervisory_nodes AS n ON n.id = sl.supervisorynodeid")
    @Results(value={
            @Result(property = "supervisoryNode.name", column = "supervisorynodeName"),
            @Result(property = "supplyingFacility.name", column = "facilityName"),
            @Result(property = "program.name", column = "programName"),
    })
    List<SupplyLine> getAllSupplyLine();

    @Select("SELECT * FROM supply_lines WHERE id = #{id}")
    @Results(value={
            @Result(property = "supervisoryNode.id", column = "supervisorynodeid"),
            @Result(property = "supplyingFacility.id", column = "supplyingfacilityid"),
            @Result(property = "program.id", column = "programid"),
    })
    SupplyLine getSupplylineById(Long id);

    @Select("SELECT " +
            "sl.id, sl.description, p.name AS programName,f.name AS facilityName, n.name As supervisorynodeName " +
            "FROM supply_lines AS sl " +
            "JOIN programs AS p ON p.id = sl.programid " +
            "JOIN facilities AS f ON f.id = sl.supplyingfacilityid " +
            "INNER JOIN supervisory_nodes AS n ON n.id = sl.supervisorynodeid " +
            "WHERE sl.id = #{id}")
    @Results(value={
            @Result(property = "supervisoryNode.name", column = "supervisorynodeName"),
            @Result(property = "supplyingFacility.name", column = "facilityName"),
            @Result(property = "program.name", column = "programName"),

    })
    SupplyLine getSupplylineDetailById(Long id);


    @Delete("DELETE FROM supply_lines where id = #{supplylineId}")
  int deleteById(Long supplylineId);

}
