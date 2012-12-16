package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSupportedMapper {
    //TODO : Change the OR query.
    @Insert("INSERT INTO PROGRAMS_SUPPORTED" +
            "(facility_id, program_id, active, modified_by, modified_date) VALUES " +
            "((SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{facilityCode}) OR id = #{facilityId}), " +
            "(SELECT id FROM program WHERE LOWER(code) = LOWER(#{programCode}) OR id = #{programId}), " +
            "#{active}, #{modifiedBy}, #{modifiedDate})")
    void addSupportedProgram(ProgramSupported programSupported);

    @Select("SELECT DISTINCT p.* " +
            "FROM program p, facilities f, programs_supported ps, user u, program  WHERE " +
            "ps.program_id = ANY(#{programIds}::INTEGER[]) AND " +
            "ps.facility_id = #{facilityId} AND " +
            "ps.program_id = p.id AND " +
            "p.active = true AND " +
            "ps.active = true")
    List<Program> filterActiveProgramsAndFacility(@Param(value = "programIds") String programCodesCommaSeparated,
                                                  @Param(value = "facilityId") Integer facilityId);

    @Select("SELECT " +
            "facility_id AS facilityId, " +
            "program_id AS programId, " +
            "active AS active, " +
            "modified_by AS modifiedBy, " +
            "modified_date as modifiedDate FROM " +
            "programs_supported " +
            "WHERE facility_id = #{facilityId} AND program_id = #{programId}")
    List<ProgramSupported> getBy(@Param("facilityId") Integer facilityId, @Param("programId") Integer programId);


    @Delete("DELETE FROM programs_supported WHERE facility_Id=#{facilityId} AND program_id=#{programId}")
    void deleteObsoletePrograms(@Param(value = "facilityId") Integer facilityId, @Param(value = "programId") Integer programId);
}
