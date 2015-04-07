/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.smt.VaccineTarget;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineTargetMapper {


    @Update("UPDATE vaccine_targets\n" +
            "   SET geographiczoneid=#{geographicZoneId}, targetyear=#{targetYear}, population=#{population}, expectedbirths=#{expectedBirths}, \n" +
            "       expectedpregnancies=#{expectedPregnancies}, servinginfants=#{servingInfants}, survivinginfants=#{survivingInfants}, \n" +
            "       children1yr=#{children1Yr}, children2yr=#{children2Yr}, girls9_13yr=#{girls9_13Yr}, \n" +
            "       modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) \n" +
            " WHERE id = #{id}")
    public void updateVaccineTraget(VaccineTarget vaccineTarget);

    @Select("select geographic_zones.name As zoneName, vaccine_targets.* from vaccine_targets\n" +
            "join geographic_zones ON vaccine_targets.geographiczoneid = geographic_zones.id")
    List<VaccineTarget> getVaccineTargets();

    @Select("select * from vaccine_targets where id = #{id}")
    VaccineTarget getVaccineTarget(Long id);

    @Insert("INSERT INTO vaccine_targets(  " +
            "            geographiczoneid, targetyear, population, expectedbirths,   " +
            "            expectedpregnancies, servinginfants, survivinginfants, children1yr,   " +
            "            children2yr, girls9_13yr, createdby, createddate, modifiedby,   " +
            "            modifieddate)  " +
            "VALUES (  " +
            "  #{geographicZoneId},  " +
            "  #{targetYear},  " +
            "  #{population},  " +
            "  #{expectedBirths},  " +
            "  #{expectedPregnancies},  " +
            "  #{servingInfants},  " +
            "  #{survivingInfants},  " +
            "  #{children1Yr},  " +
            "  #{children2Yr},  " +
            "  #{girls9_13Yr},  " +
            "  #{createdBy},   " +
            "  COALESCE(#{createdDate}, NOW()),   " +
            "  #{modifiedBy},   " +
            "  COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)  " +
            ")  ")
    @Options(useGeneratedKeys = true)
    void insertVaccineTraget(VaccineTarget vaccineTarget);

    @Delete("delete from vaccine_targets where id = #{id}")
    void deleteVaccineTarget(Long id);
}


