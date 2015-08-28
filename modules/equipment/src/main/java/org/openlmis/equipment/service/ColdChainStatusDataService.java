/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ColdChainEquipmentDesignation;
import org.openlmis.equipment.domain.ColdChainEquipmentPqsStatus;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.repository.ColdChainEquipmentDesignationRepository;
import org.openlmis.equipment.repository.ColdChainEquipmentPqsStatusRepository;
import org.openlmis.equipment.repository.DonorRepository;
import org.openlmis.equipment.repository.EquipmentEnergyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ColdChainStatusDataService {

  @Autowired
  ColdChainEquipmentDesignationRepository designationRepository;

  @Autowired
  ColdChainEquipmentPqsStatusRepository pqsStatusRepository;

  @Autowired
  EquipmentEnergyTypeRepository energyTypeRepository;

  @Autowired
  DonorRepository donorRepository;

  public List<ColdChainEquipmentDesignation> getAllDesignations(){
    return designationRepository.getAll();
  }

  public List<EquipmentEnergyType> getAllEnergyTypes(){
    return energyTypeRepository.getAll();
  }

  public List<ColdChainEquipmentPqsStatus> getAllPqsStatus(){
    return pqsStatusRepository.getAll();
  }
  public List<Donor> getAllDonors(){
    return donorRepository.getAll();
  }
}
