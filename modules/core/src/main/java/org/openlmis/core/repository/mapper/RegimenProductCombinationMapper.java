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

import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.DosageFrequency;
import org.openlmis.core.domain.RegimenProductCombination;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DosageFrequencyMapper maps the DosageUnit entity to corresponding representation in database.
 */
@Repository
public interface RegimenProductCombinationMapper {

    @Select("SELECT * FROM regimen_product_combinations WHERE id = #{id}")
    @Results(value = {
            @Result(property = "regimen", column = "regimenid", javaType = Long.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.RegimenMapper.getById"))})
    RegimenProductCombination getById(Long id);

    @Select("SELECT * FROM regimen_product_combinations")
    @Results(value = {
            @Result(property = "regimen", column = "regimenid", javaType = Long.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.RegimenMapper.getById"))})
    List<RegimenProductCombination> getAll();
}
