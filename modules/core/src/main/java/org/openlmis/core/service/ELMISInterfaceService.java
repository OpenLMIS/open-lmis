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

package org.openlmis.core.service;

import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceDataSet;
import org.openlmis.core.domain.ELMISInterfaceFacilityMapping;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.ELMISInterfaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ELMISInterfaceService {

    @Autowired
    private ELMISInterfaceRepository repository;

    public ELMISInterface get(long interfaceId) {
        return repository.get(interfaceId);
    }

    public void save(ELMISInterface elmisInterface) {

        if(elmisInterface.getId() != null)
            repository.update(elmisInterface);
        else
            repository.insert(elmisInterface);

        repository.updateELMISInterfaceDataSets(elmisInterface);
    }

    public List<ELMISInterface> getAllInterfaces(){
        return repository.getAllInterfaces();
    }

    public List<ELMISInterfaceFacilityMapping> getInterfaceFacilityMappings(){
        return repository.getInterfaceFacilityMappings();
    }

    public List<ELMISInterfaceFacilityMapping> getFacilityInterfaceMappingById(Long facilityId){
        return repository.getFacilityInterfaceMappingById(facilityId);
    }

    public List<ELMISInterface> getAllActiveInterfaces() {
        return repository.getAllActiveInterfaces();
    }

    public void updateFacilityInterfaceMapping(Facility facility){
        repository.updateFacilityInterfaceMapping(facility);
    }
}
