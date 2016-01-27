/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *   Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.repository;


import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceDataSet;
import org.openlmis.core.domain.ELMISInterfaceFacilityMapping;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ELMISInterfaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ELMISInterfaceRepository {

    @Autowired
    private ELMISInterfaceMapper mapper;

    public ELMISInterface get(long interfaceId) {
        return mapper.get(interfaceId);
    }

    public void insert(ELMISInterface elmisInterface) {
         mapper.insert(elmisInterface);
    }

    public void update(ELMISInterface elmisInterface) {
         mapper.update(elmisInterface);
    }

    public void updateELMISInterfaceDataSets(ELMISInterface elmisInterface) {

        List<ELMISInterfaceDataSet> previousDataSets = mapper.getInterfaceDatasetById(elmisInterface.getId());
        List<ELMISInterfaceDataSet> editedDatasets = elmisInterface.getDataSets();

        deleteRemovedDatasetElements(previousDataSets, editedDatasets);
        UpdateDatasetElements(elmisInterface);
    }

    private void UpdateDatasetElements(ELMISInterface elmisInterface) {
        for(ELMISInterfaceDataSet dataset : elmisInterface.getDataSets()){

                if(dataset.getId() == null){
                    dataset.setCreatedBy(elmisInterface.getModifiedBy());
                    dataset.setModifiedBy(elmisInterface.getModifiedBy());
                    dataset.setInterfaceId(elmisInterface.getId());
                    mapper.insertDataSet(dataset);
                }
                else {
                    dataset.setModifiedBy(elmisInterface.getModifiedBy());
                    mapper.updateDataSet(dataset);
                }
    }
    }

    private void deleteRemovedDatasetElements(List<ELMISInterfaceDataSet> previousDataSets, List<ELMISInterfaceDataSet> updateLists) {
        Boolean deletedFlag;

        for(ELMISInterfaceDataSet previous: previousDataSets){

            deletedFlag = true;

            for(ELMISInterfaceDataSet current : updateLists){
                if(current.getId() != null && current.getId().equals(previous.getId()))
                {
                    deletedFlag = false;
                    break;
                }
            }

            if(deletedFlag)
                mapper.deleteDataset(previous);
        }
    }

    public List<ELMISInterface> getAllInterfaces(){
        return mapper.getAllInterfaces();
    }

    public List<ELMISInterfaceFacilityMapping> getInterfaceFacilityMappings(){
        return mapper.getInterfaceFacilityMappings();
    }

    public List<ELMISInterfaceFacilityMapping> getFacilityInterfaceMappingById(Long facilityId){
       return mapper.getFacilityInterfaceMappingById(facilityId);
    }

    public List<ELMISInterface> getAllActiveInterfaces() {
        return mapper.getAllActiveInterfaces();
    }

    public void updateFacilityInterfaceMapping(Facility facility) {

        List<ELMISInterfaceFacilityMapping> previousMapping = mapper.getFacilityInterfaceMappingById(facility.getId());
        List<ELMISInterfaceFacilityMapping> editedMappings = facility.getInterfaceMappings();

        try {
            deleteRemovedMappings(previousMapping, editedMappings);
            UpdateFacilityMappings(facility);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("error.facility.interface.mapping.exists");
        } catch (DataIntegrityViolationException integrityViolationException) {
            throw new DataException("error.reference.data.invalid.interface.id");
        }
    }

    private void deleteRemovedMappings(List<ELMISInterfaceFacilityMapping> previousMapping, List<ELMISInterfaceFacilityMapping> editedMappings) {
        Boolean deletedFlag;

        for(ELMISInterfaceFacilityMapping previous: previousMapping){

            deletedFlag = true;

            for(ELMISInterfaceFacilityMapping current : editedMappings){
                if(current.getId() != null && current.getId().equals(previous.getId()))
                {
                    deletedFlag = false;
                    break;
                }
            }

            if(deletedFlag)
                mapper.deleteFacilityMapping(previous);
        }
    }

    private void UpdateFacilityMappings(Facility facility) {
        for(ELMISInterfaceFacilityMapping mapping : facility.getInterfaceMappings()){

            if(mapping.getId() == null){
                mapping.setCreatedBy(facility.getModifiedBy());
                mapping.setModifiedBy(facility.getModifiedBy());
                mapping.setFacilityId(facility.getId());
                mapper.insertFacilityMapping(mapping);
            }
            else {
                mapping.setModifiedBy(facility.getModifiedBy());
                mapper.updateFacilityMapping(mapping);
            }
        }
    }
}
