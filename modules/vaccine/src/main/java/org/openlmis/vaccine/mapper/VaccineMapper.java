package org.openlmis.vaccine.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.VaccineTarget;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineMapper {


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
