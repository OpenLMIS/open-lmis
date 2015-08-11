/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormSurveyType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ODKXFormMapper {

    @Select("SELECT * FROM odk_xform")
    public List<ODKXForm> getAvailableXFormDefinitions();

    @Select("SELECT * FROM odk_xform WHERE formId = #{formId}")
    public ODKXForm getXFormByFormId(String formId);

    @Select("SELECT * FROM odk_xform_survey_type WHERE id = #{id}")
    public ODKXFormSurveyType getXFormSurveyTypeById(Long id);

    @Insert("INSERT INTO odk_xform(" +
            "formId, " +
            "name, " +
            "version," +
            "hash," +
            "descriptionText," +
            "downloadUrl," +
            "XMLString," +
            "active," +
            "ODKXFormSurveyTypeId) " +
            "VALUES " +
            "(#{formId}, " +
            " #{name}," +
            " #{version}," +
            " #{hash}," +
            " #{descriptionText}, " +
            " #{downloadUrl}, " +
            " #{XMLString}, " +
            " #{active}, " +
            " #{ODKXFormSurveyTypeId})")
    @Options(useGeneratedKeys = true)
    void insert(ODKXForm odkxForm);

    @Select("SELECT * FROM odk_xform WHERE id = #{id}")
    public ODKXForm getXFormById(Long id);

}
