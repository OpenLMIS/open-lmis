package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.VaccineStorage;
import org.openlmis.vaccine.repository.VaccineStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by seifu on 11/12/2014.
 */
@Component
public class VaccineStorageService {
    @Autowired
    private VaccineStorageRepository storageRepository;
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
}
