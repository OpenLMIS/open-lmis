/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.*;
import org.openlmis.distribution.repository.VaccinationCoverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling VaccinationFullCoverage, VaccinationChildCoverage and VaccinationAdultCoverage
 * entity.
 */

@Service
public class VaccinationCoverageService {

  @Autowired
  private VaccinationCoverageRepository repository;

  public void save(FacilityDistribution facilityDistribution) {
    repository.saveFullCoverage(facilityDistribution.getFullCoverage());
    repository.saveChildCoverage(facilityDistribution.getChildCoverage());
    repository.saveAdultCoverage(facilityDistribution.getAdultCoverage());
  }

  public VaccinationFullCoverage getFullCoverageBy(Long facilityVisitId) {
    return repository.getFullCoverageBy(facilityVisitId);
  }

  public List<TargetGroupProduct> getVaccinationProducts() {
    return repository.getVaccinationProducts();
  }

  public void saveChildCoverage(VaccinationChildCoverage childCoverage) {
    repository.saveChildCoverage(childCoverage);
  }

  public VaccinationChildCoverage getChildCoverageBy(Long facilityVisitId) {
    return repository.getChildCoverageBy(facilityVisitId);
  }

  public List<ProductVial> getProductVials() {
    return repository.getProductVials();
  }

  public void saveAdultCoverage(VaccinationAdultCoverage adultCoverage) {
    repository.saveAdultCoverage(adultCoverage);
  }

  public VaccinationAdultCoverage getAdultCoverageBy(Long facilityVisitId) {
    return repository.getAdultCoverageBy(facilityVisitId);
  }
}
