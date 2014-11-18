/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.DistributionBatch;
import org.openlmis.vaccine.repository.VaccineDistributionBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineDistributionBatchService {

    @Autowired
    private VaccineDistributionBatchRepository distributionBatchRepository;

    public List<DistributionBatch> getByDispatchId(String dispatchId){
        return distributionBatchRepository.getByDispatchId(dispatchId);
    }

    public DistributionBatch getById(Long id){
        return distributionBatchRepository.getById(id);
    }

    public List<DistributionBatch> getAll(){
        return distributionBatchRepository.getAll();
    }
    public List<DistributionBatch> searchDistributionBatches(String query){
        return distributionBatchRepository.searchDistributionBatches(query);
    }

    public void update(DistributionBatch distributionBatch) {
        distributionBatchRepository.update(distributionBatch);
    }

}
