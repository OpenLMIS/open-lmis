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

import lombok.NoArgsConstructor;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.repository.EpiUseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Exposes the services for handling EpiUse entity.
 */

@Service
@NoArgsConstructor
public class EpiUseService {

  @Autowired
  private EpiUseRepository repository;

  public void save(EpiUse epiUse) {
    for (EpiUseLineItem lineItem : epiUse.getLineItems()) {
      repository.saveLineItem(lineItem);
    }
  }

  public EpiUse getBy(Long facilityVisitId) {
    return repository.getBy(facilityVisitId);
  }
}
