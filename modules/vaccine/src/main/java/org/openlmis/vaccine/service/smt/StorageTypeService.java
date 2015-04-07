package org.openlmis.vaccine.service.smt;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.openlmis.vaccine.domain.smt.StorageType;
import org.openlmis.vaccine.repository.smt.StorageTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class StorageTypeService {
@Autowired
private StorageTypeRepository storageTypeRepository;
    public List<StorageType> loadStorageTypeList(){
        return this.storageTypeRepository.loadStorageTypeList();
    }
    public void addStorageType(StorageType storageType){
        this.storageTypeRepository.addStorageType(storageType);
    }
    public StorageType loadStorageTypeDetail(long id){
        return  this.storageTypeRepository.loadStorageTypeDetail(id);
    }
    public void updateStorageType(StorageType storageType){
        this.storageTypeRepository.updateStorageType(storageType);
    }
    public void removeStorageType(StorageType storageType){
        this.storageTypeRepository.removeStorageType(storageType);
    }

    public  List<StorageType> searchForStorageTypeList(String param) {

       return this.storageTypeRepository.searchForStorageTypes(param);
    }
}
