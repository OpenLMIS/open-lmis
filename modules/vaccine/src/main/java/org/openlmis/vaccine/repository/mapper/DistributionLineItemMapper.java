/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.DistributionBatch;
import org.openlmis.vaccine.domain.DistributionLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionLineItemMapper {

    @Insert("INSERT INTO vaccine_distribution_line_items(distributionbatchid, quantityreceived, vvmstage, confirmed,comments, createdby, createddate, modifiedby, modifieddate)\n" +
            "VALUES(#{distributionBatch.id},#{quantityReceived},#{vvmStage},#{confirmed},#{comments}, #{createdBy}, COALESCE(#{createdDate}, NOW()),#{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) ) ")

    @Options(useGeneratedKeys = true)
    int insert(DistributionLineItem distributionLineItem);

    @Update("UPDATE vaccine_distribution_line_items\n" +
            "   SET distributionbatchid= #{distributionBatch.id}, quantityreceived= #{quantityReceived}, vvmstage= #{vvmStage}, \n" +
            "       confirmed= #{confirmed}, comments= #{comments}, createdby=#{createdBy}, createddate= COALESCE(#{createdDate}, NOW()), modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id = #{id}\n")
    void update(DistributionLineItem distributionLineItem);

    @Select("SELECT * from vaccine_distribution_line_items")
    @Results({
            @Result(property = "distributionBatch", javaType = DistributionBatch.class, column = "distributionBatchId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.VaccineDistributionBatchMapper.getById"))
    })
    List<DistributionLineItem> getAll();
}
