package org.openlmis.vaccine.service.smt;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.vaccine.domain.smt.VaccineStorage;
import org.openlmis.vaccine.repository.smt.VaccineStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
@Component
public class VaccineStorageService {
    @Autowired
    private VaccineStorageRepository storageRepository;
    @Autowired
    private FacilityService facilityService;
    public void addVaccineStorage(VaccineStorage vaccineStorage){
        this.storageRepository.addVaccineStorage(vaccineStorage);
    }
    public VaccineStorage loadVaccineStorageDetail(long vaccineStorageId){
        return   this.storageRepository.loadVaccineStorageDetail(vaccineStorageId);
    }
    public List<VaccineStorage> loadVaccineStorageList(){
        return this.storageRepository.loadVaccineStorageList();
    }
    public void updateVaccineStorage(VaccineStorage vaccineStorage){
        this.storageRepository.updateVaccineStorage(vaccineStorage);
    }
    public void deleteVccineStorage(VaccineStorage vaccineStorage){
        this.storageRepository.deleteVccineStorage(vaccineStorage);
    }
    public List<Facility> loadFacillityList(){
        return null;//this.facilityService.getAll();
    }

    public List<VaccineStorage> getByFacilityId(Long facilityId){
        return storageRepository.getByFacilityId(facilityId);
    }
}
