package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrColumnMapper {

    @Insert("INSERT INTO program_rnr_columns " +
            "(programCode,    masterColumnId,          visible,           label,              position,              source,            formulaValidated) " +
            "VALUES " +
            "(#{programCode}, #{rnrColumn.id},  #{rnrColumn.visible}, #{rnrColumn.label}, #{rnrColumn.position}, #{rnrColumn.source.code}, #{rnrColumn.formulaValidated})")
    int insert(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

    @Delete("DELETE FROM program_rnr_columns")
        //TODO get rid of delete, only used in tests
    void deleteAll();

    @Select("select 0<(select count(id) as count from program_rnr_columns where programCode = #{programCode})")
    boolean isRnrTemplateDefined(@Param("programCode") String programCode);

    @Select("select m.id, m.name, m.description, m.formula, m.indicator, m.used, m.mandatory, m.sourceConfigurable, " +
            " p.position, p.label, p.visible, p.source as sourceString, p.formulaValidated as formulaValidated" +
            " FROM program_rnr_columns p INNER JOIN master_rnr_columns m " +
            " ON p.masterColumnId = m.id " +
            " WHERE p.programCode = #{programCode} " +
            " ORDER BY visible DESC, position")
    List<RnrColumn> getAllRnrColumnsForProgram(String programCode);

    @Update("UPDATE program_rnr_columns SET " +
            "visible = #{rnrColumn.visible}, " +
            "label = #{rnrColumn.label}, " +
            "position = #{rnrColumn.position}, " +
            "source = #{rnrColumn.source.code}, " +
            "formulaValidated = #{rnrColumn.formulaValidated} " +
            "WHERE programCode = #{programCode} AND masterColumnId = #{rnrColumn.id}")
    void update(@Param("programCode") String programCode, @Param("rnrColumn") RnrColumn rnrColumn);

    @Select("SELECT m.id, m.name, m.description, m.used, m.mandatory, m.formula, m.indicator, " +
            " p.position, p.label, p.visible , p.source as sourceString, p.formulaValidated as formulaValidated" +
            " FROM program_rnr_columns p INNER JOIN master_rnr_columns m" +
            " ON p.masterColumnId = m.id" +
            " WHERE p.programCode = #{programCode} AND p.visible = 'true'" +
            " ORDER BY visible desc, position")
    List<RnrColumn> getVisibleProgramRnrColumns(String programCode);


    @Select(value = "SELECT * FROM master_rnr_columns")
    @Results(value = {@Result(property = "sourceString", column = "source")})
    List<RnrColumn> fetchAllMasterRnRColumns();


}
