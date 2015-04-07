package org.openlmis.vaccine.repository.smt;

import org.openlmis.vaccine.domain.smt.VaccineStorage;
import org.openlmis.vaccine.repository.mapper.smt.VaccineStorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Deprecated
public class VaccineStorageRepository {
    @Autowired
    private VaccineStorageMapper vaccineStorageMapper;
    public void addVaccineStorage(VaccineStorage vaccineStorage){
        this.vaccineStorageMapper.insert(vaccineStorage);
    }
    public VaccineStorage loadVaccineStorageDetail(long vaccineStorageId){
      return   this.vaccineStorageMapper.getById(vaccineStorageId);
    }
    public List<VaccineStorage> loadVaccineStorageList(){
        return this.vaccineStorageMapper.loadAllList();
    }
    public void deleteVccineStorage(VaccineStorage vaccineStorage){
        this.vaccineStorageMapper.delete(vaccineStorage);
    }

    public void updateVaccineStorage(VaccineStorage vaccineStorage) {
        this.vaccineStorageMapper.update(vaccineStorage);
    }

    public List<VaccineStorage> getByFacilityId(Long facilityId){
        return vaccineStorageMapper.getByFacilityId(facilityId);
    }
}
