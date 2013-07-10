/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.reporting.model.ReportTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTemplateMapper {

  @Select("SELECT * from report_templates WHERE name=#{name}")
  ReportTemplate getByName(String name);

  @Select("SELECT * from report_templates WHERE id=#{id}")
  ReportTemplate getById(Integer id);

  @Insert("INSERT INTO report_templates (name, data, commaSeparatedParameters, createdBy, createdDate) " +
    "VALUES (#{name}, #{data}, #{commaSeparatedParameters}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))")
  @Options(useGeneratedKeys = true)
  void insert(ReportTemplate reportTemplate);

  @Select("SELECT id, name from report_templates")
  List<ReportTemplate> getAll();
}
