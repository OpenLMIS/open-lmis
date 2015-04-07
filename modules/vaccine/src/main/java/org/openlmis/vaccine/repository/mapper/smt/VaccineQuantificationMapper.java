/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.AdministrationMode;
import org.openlmis.vaccine.domain.smt.Dilution;
import org.openlmis.vaccine.domain.smt.VaccinationType;
import org.openlmis.vaccine.domain.smt.VaccineQuantification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface VaccineQuantificationMapper {


    @Update("UPDATE vaccine_quantifications \n" +
            "   SET programid=#{programId}, quantificationyear=#{quantificationYear}, vaccinetypeid=#{vaccineTypeId}, productcode=#{productCode}, \n" +
            "       targetpopulation=#{targetPopulation}, targetpopulationpercent=#{targetPopulationPercent}, dosespertarget=#{dosesPerTarget}, \n" +
            "       presentation=#{presentation}, expectedcoverage=#{expectedCoverage}, wastagerate=#{wastageRate}, administrationmodeid=#{administrationModeId}, \n" +
            "       dilutionid=#{dilutionId}, supplyinterval=#{supplyInterval}, safetystock=#{safetyStock}, leadtime=#{leadTime},  modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id = #{id}")
    public void updateVaccineQuantification(VaccineQuantification vaccineQuantification);

    @Select("select programs.name AS program, products.fullname AS product, vaccine_quantifications.* from vaccine_quantifications \n" +
            "join products on products.code = vaccine_quantifications.productcode\n" +
            "join programs on programs.id = vaccine_quantifications.programid order by program, product\n")
    List<VaccineQuantification> getVaccineQuantifications();

    @Select("select * from vaccine_dilution")
    List<Dilution> getVaccineDilutions();

    @Select("select * from vaccination_types")
    List<VaccinationType> getVaccinationTypes();

    @Select("select * from vaccine_administration_mode")
    List<AdministrationMode> getVaccineAdministrationMode();

    @Select("select * from vaccine_quantifications where id = #{id}")
    VaccineQuantification getVaccineQuantification(Long id);

    @Insert("INSERT INTO vaccine_quantifications( " +
            "            programid, quantificationyear, vaccinetypeid, productcode,  " +
            "            targetpopulation, targetpopulationpercent, dosespertarget, presentation,  " +
            "            expectedcoverage, wastagerate, administrationmodeid, dilutionid,  " +
            "            supplyinterval, safetystock, leadtime, createdby, createddate) " +
            "    VALUES ( " +
            " #{programId}, " +
            " #{quantificationYear}, " +
            " #{vaccineTypeId}, " +
            " #{productCode}, " +
            " #{targetPopulation}, " +
            " #{targetPopulationPercent}, " +
            " #{dosesPerTarget}, " +
            " #{presentation}, " +
            " #{expectedCoverage}, " +
            " #{wastageRate}, " +
            " #{administrationModeId}, " +
            " #{dilutionId}, " +
            " #{supplyInterval}, " +
            " #{safetyStock}, " +
            " #{leadTime}, " +
            " #{createdBy},     " +
            " COALESCE(#{createdDate}, NOW()) " +
            " )")
    @Options(useGeneratedKeys = true)
    void insertVaccineQuantification(VaccineQuantification vaccineQuantification);

    @Delete("delete from vaccine_quantifications where id = #{id}")
    void deleteVaccineQuantification(Long id);
}


