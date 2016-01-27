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
import org.apache.ibatis.mapping.StatementType;
import org.openlmis.core.domain.ISA;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.springframework.stereotype.Repository;

/**
 * ProgramProductIsaMapper maps the ProgramProductISA entity to corresponding representation in database.
 */
@Repository
public interface ProgramProductIsaMapper {

    @Insert(value = "SELECT fn_insert_isa" +
            "(" +
                "#{isa.whoRatio}::numeric, " +
                "#{isa.dosesPerYear}::int," +
                "#{isa.wastageFactor}::numeric," +
                "#{isa.bufferPercentage}::numeric," +
                "#{isa.minimumValue}::int," +
                "#{isa.maximumValue}::int," +
                "#{isa.adjustmentValue}::int," +
                "#{isa.createdBy}::int," +
                "COALESCE(#{isa.createdDate}, NOW())::timestamp ," +
                "#{isa.modifiedBy}::int," +
                "COALESCE(#{isa.modifiedDate}, NOW())::timestamp ," +
                "#{isa.populationSource}::int," +
                "#{programProductId}::int " +
            ")"
    )
    @Options(statementType = StatementType.CALLABLE)
    @SelectKey(
            statement = "SELECT id FROM isa_coefficients ORDER BY id DESC LIMIT 1",
            resultType = Long.class,
            before = false,
            keyColumn = "id",
            keyProperty = "isa.id"
    )
    void insert(ProgramProductISA ppi);


  @Insert(value = "SELECT fn_update_program_product_isa" +
          "(" +
          "#{programProductId}::int, " +
          "#{isa.id}::int, " +
          "#{isa.whoRatio}::numeric, " +
          "#{isa.dosesPerYear}::int," +
          "#{isa.wastageFactor}::numeric," +
          "#{isa.bufferPercentage}::numeric," +
          "#{isa.minimumValue}::int," +
          "#{isa.maximumValue}::int," +
          "#{isa.adjustmentValue}::int," +
          "#{isa.createdBy}::int," +
          "COALESCE(#{createdDate}, NOW())::timestamp ," +
          "#{isa.modifiedBy}::int," +
          "COALESCE(#{modifiedDate}, NOW())::timestamp," +
          "#{isa.populationSource}::int" +
          ")"
  )
  @Options(statementType = StatementType.CALLABLE)
  void update(ProgramProductISA ppi);

  @Select("SELECT pp.id AS \"programProductId\", ic.* \n" +
          "FROM isa_coefficients ic JOIN program_products pp\n" +
          "ON pp.isaCoefficientsId = ic.id\n" +
          "WHERE pp.id = #{programProductId}")
  ProgramProductISA getIsaByProgramProductId(Long programProductId);
}
