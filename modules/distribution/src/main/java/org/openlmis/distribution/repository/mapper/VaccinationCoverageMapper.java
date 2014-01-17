/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.domain.VaccinationProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationCoverageMapper {

  @Insert({"INSERT INTO full_coverages (facilityVisitId, femaleHealthCenter, femaleOutreach, maleHealthCenter, maleOutreach,",
    "createdBy, modifiedBy)",
    "VALUES (#{facilityVisitId}, #{femaleHealthCenter}, #{femaleOutreach}, #{maleHealthCenter}, #{maleOutreach},",
    "#{createdBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insertFullVaccinationCoverage(VaccinationFullCoverage vaccinationFullCoverage);

  @Select({"SELECT * FROM full_coverages WHERE facilityVisitId = #{facilityVisitId}"})
  VaccinationFullCoverage getFullCoverageBy(Long facilityVisitId);

  @Select({"SELECT * FROM coverage_vaccination_products WHERE childCoverage = #{isChildCoverage}"})
  List<VaccinationProduct> getVaccinationProducts(Boolean isChildCoverage);

  @Insert({"INSERT INTO vaccination_child_coverage_line_items (facilityVisitId, vaccination, targetGroup)",
    "VALUES (#{facilityVisitId}, #{vaccination}, #{targetGroup})"})
  @Options(useGeneratedKeys = true)
  void insertChildVaccinationCoverageLineItem(ChildCoverageLineItem childCoverageLineItem);

  @Select({"SELECT * FROM vaccination_child_coverage_line_items WHERE facilityVisitId = #{facilityVisitId}"})
  List<ChildCoverageLineItem> getChildCoverageLineItemsBy(Long facilityVisitId);
}
