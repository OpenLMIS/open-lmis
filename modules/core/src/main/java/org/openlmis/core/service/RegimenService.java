/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RegimenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling Regimen entity.
 */

@Service
public class RegimenService {

  @Autowired
  RegimenRepository repository;

  @Autowired
  ProgramService programService;

  public void save(List<Regimen> regimens, Long userId) {
    repository.save(regimens, userId);
  }

  public List<Regimen> getByProgram(Long programId) {
    return repository.getByProgram(programId);
  }

  public List<RegimenCategory> getAllRegimenCategories() {
    return repository.getAllRegimenCategories();
  }

  public Regimen getById(Long id){return repository.getById(id);}

  public List<DosageFrequency> getAllDosageFrequencies(){
      return repository.getAllDosageFrequencies();
  }

  public List<RegimenProductCombination> getAllRegimenProductCombinations(){
      return repository.getAllRegimenProductCombinations();
  }

  public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents(){
        return repository.getAllRegimenCombinationConstituents();
  }

  public List<RegimenConstituentDosage> getAllRegimenConstituentDosages(){
        return repository.getAllRegimenConstituentsDosages();
  }
}
