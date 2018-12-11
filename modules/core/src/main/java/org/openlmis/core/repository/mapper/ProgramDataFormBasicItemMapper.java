package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.moz.ProgramDataFormBasicItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramDataFormBasicItemMapper {

    @Insert("INSERT INTO program_data_form_basic_items (formId, productCode, beginningBalance," +
            " quantityReceived, quantityDispensed, totalLossesAndAdjustments, stockInHand, createdBy, modifiedBy, createdDate, modifiedDate, expirationDate) " +
            "VALUES (#{programDataForm.id}, #{productCode}, #{beginningBalance}, #{quantityReceived}, #{quantityDispensed}, #{totalLossesAndAdjustments}," +
            "#{stockInHand},  #{createdBy}, #{modifiedBy}, NOW(), NOW(), #{expirationDate})")
    @Options(useGeneratedKeys = true)
    void insert(ProgramDataFormBasicItem programDataFormBasicItem);

    @Select("SELECT * FROM program_data_form_basic_items WHERE formId = #{formId}")
    List<ProgramDataFormBasicItem> getByFormId(Long formId);
}
