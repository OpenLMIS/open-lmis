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
import org.apache.ibatis.annotations.Update;
import org.openlmis.distribution.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the VaccinationFullCoverage, ChildCoverageLineItem, AdultCoverageLineItem, OpenedVialLineItem,
 * TargetGroupProduct and ProductVial entity to corresponding representation in database.
 */

@Repository
public interface VaccinationCoverageMapper {

  @Insert({"INSERT INTO full_coverages (facilityVisitId, femaleHealthCenter, femaleOutreach, maleHealthCenter, maleOutreach,",
    "createdBy, modifiedBy)",
    "VALUES (#{facilityVisitId}, #{femaleHealthCenter}, #{femaleOutreach}, #{maleHealthCenter}, #{maleOutreach},",
    "#{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertFullVaccinationCoverage(VaccinationFullCoverage vaccinationFullCoverage);

  @Insert({"INSERT INTO vaccination_child_coverage_line_items (facilityVisitId, vaccination, targetGroup, createdBy, modifiedBy)",
    "VALUES (#{facilityVisitId}, #{vaccination}, #{targetGroup}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertChildCoverageLineItem(ChildCoverageLineItem childCoverageLineItem);

  @Insert({"INSERT INTO child_coverage_opened_vial_line_items (facilityVisitId, productVialName, packSize, createdBy, modifiedBy)",
    "VALUES (#{facilityVisitId}, #{productVialName}, #{packSize}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertChildCoverageOpenedVialLineItem(OpenedVialLineItem openedVialLineItem);

  @Insert({"INSERT INTO adult_coverage_opened_vial_line_items (facilityVisitId, productVialName, packSize, createdBy, modifiedBy)",
    "VALUES (#{facilityVisitId}, #{productVialName}, #{packSize}, #{createdBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insertAdultCoverageOpenedVialLineItem(OpenedVialLineItem openedVialLineItem);

  @Select({"SELECT * FROM full_coverages WHERE facilityVisitId = #{facilityVisitId}"})
  VaccinationFullCoverage getFullCoverageBy(Long facilityVisitId);

  @Select({"SELECT * FROM coverage_target_group_products"})
  List<TargetGroupProduct> getVaccinationProducts();

  @Select({"SELECT * FROM vaccination_child_coverage_line_items WHERE facilityVisitId = #{facilityVisitId}"})
  List<ChildCoverageLineItem> getChildCoverageLineItemsBy(Long facilityVisitId);

  @Select({"SELECT * FROM coverage_product_vials"})
  List<ProductVial> getProductVials();

  @Update({"UPDATE vaccination_child_coverage_line_items SET healthCenter11Months = #{healthCenter11Months}, outreach11Months = #{outreach11Months},",
    "healthCenter23Months = #{healthCenter23Months}, outreach23Months = #{outreach23Months}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void updateChildCoverageLineItem(ChildCoverageLineItem childCoverageLineItem);

  @Update({"UPDATE child_coverage_opened_vial_line_items SET openedVials = #{openedVials}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void updateChildCoverageOpenedVialLineItem(OpenedVialLineItem openedVialLineItem);

  @Update({"UPDATE adult_coverage_opened_vial_line_items SET openedVials = #{openedVials}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void updateAdultCoverageOpenedVialLineItem(OpenedVialLineItem openedVialLineItem);

  @Select({"SELECT * FROM child_coverage_opened_vial_line_items WHERE facilityVisitId = #{facilityVisitId}"})
  List<OpenedVialLineItem> getChildCoverageOpenedVialLineItemsBy(Long facilityVisitId);

  @Select({"SELECT * FROM adult_coverage_opened_vial_line_items WHERE facilityVisitId = #{facilityVisitId}"})
  List<OpenedVialLineItem> getAdultCoverageOpenedVialLineItemsBy(Long facilityVisitId);

  @Insert({"INSERT INTO vaccination_adult_coverage_line_items (facilityVisitId, targetGroup, demographicGroup, createdBy, modifiedBy) " +
    "VALUES (#{facilityVisitId}, #{targetGroup}, #{demographicGroup}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertAdultCoverageLineItem(AdultCoverageLineItem lineItem);

  @Select({"SELECT * FROM vaccination_adult_coverage_line_items WHERE facilityVisitId = #{facilityVisitId}"})
  List<AdultCoverageLineItem> getAdultCoverageLineItemsBy(Long facilityVisitId);

  @Update({"UPDATE vaccination_adult_coverage_line_items SET healthCenterTetanus1=#{healthCenterTetanus1},",
    "outreachTetanus1=#{outreachTetanus1}, healthCenterTetanus2To5=#{healthCenterTetanus2To5},",
    "outreachTetanus2To5=#{outreachTetanus2To5}, modifiedDate=DEFAULT, modifiedBy=#{modifiedBy}",
    "WHERE id=#{id}"})
  void updateAdultCoverageLineItem(AdultCoverageLineItem adultCoverageLineItem);
}
