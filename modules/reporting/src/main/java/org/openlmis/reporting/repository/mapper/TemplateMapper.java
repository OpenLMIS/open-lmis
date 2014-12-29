/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the Template entity and Template Parameter entity to their corresponding representations in database.
 */

@Repository
public interface TemplateMapper {

  @Insert({"INSERT INTO templates (name, data, type, description, createdBy)",
    "VALUES (#{name}, #{data}, #{type}, #{description}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Template template);

  @Select("SELECT * FROM templates WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "parameters", javaType = List.class, column = "id",
      many = @Many(select = "getParametersByTemplateId"))
  })
  Template getById(Long id);

  @Select("SELECT id, name FROM templates WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "parameters", javaType = List.class, column = "id",
      many = @Many(select = "getParametersByTemplateId"))
  })
  Template getLWById(Long id);

  @Select("SELECT * FROM templates WHERE LOWER(name) = LOWER(#{name})")
  Template getByName(String name);

  @Select("SELECT id, name FROM templates WHERE type = 'Consistency Report' ORDER BY createdDate")
  List<Template> getAllConsistencyReportTemplates();

  @Select({"SELECT DISTINCT t.name, t.id, t.description FROM templates t",
    "INNER JOIN report_rights rt ON rt.templateId = t.id",
    "INNER JOIN role_rights rr ON rr.rightName = rt.rightName",
    "INNER JOIN role_assignments ra ON ra.roleId = rr.roleId WHERE ra.userId = #{userId}"})
  List<Template> getAllTemplatesForUser(@Param("userId") Long userId);

  @Insert({"INSERT INTO template_parameters(templateId, name, displayName, defaultValue, dataType, description, createdBy)",
    "VALUES (#{templateId}, #{name}, #{displayName}, #{defaultValue}, #{dataType}, #{description}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insertParameter(TemplateParameter parameter);

  @Select("SELECT * FROM template_parameters WHERE templateId = #{templateId}")
  List<TemplateParameter> getParametersByTemplateId(Long templateId);
}