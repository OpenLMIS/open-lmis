/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.VaccinationChildCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.domain.VaccinationProduct;
import org.openlmis.distribution.repository.mapper.VaccinationCoverageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VaccinationCoverageRepository {

  @Autowired
  private VaccinationCoverageMapper mapper;

  public void saveFullCoverage(VaccinationFullCoverage vaccinationFullCoverage) {
    mapper.insertFullVaccinationCoverage(vaccinationFullCoverage);
  }

  public VaccinationFullCoverage getFullCoverageBy(Long facilityVisitId) {
    return mapper.getFullCoverageBy(facilityVisitId);
  }

  public List<VaccinationProduct> getVaccinationProducts(Boolean isChildCoverage) {
    return mapper.getVaccinationProducts(isChildCoverage);
  }

  public void saveChildCoverage(VaccinationChildCoverage childCoverage) {
    for (ChildCoverageLineItem childCoverageLineItem : childCoverage.getChildCoverageLineItems()) {
      mapper.insertChildVaccinationCoverageLineItem(childCoverageLineItem);
    }
  }

  public VaccinationChildCoverage getChildCoverageBy(Long facilityVisitId) {
    return new VaccinationChildCoverage(mapper.getChildCoverageLineItemsBy(facilityVisitId));
  }
}
