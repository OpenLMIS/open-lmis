/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.smt;

import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.Status;
import org.openlmis.vaccine.repository.smt.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    public List<Status> getAll(){
        return statusRepository.getAll();
    }

    public void deleteStatus(Long id){

        try{

            statusRepository.delete(id);
        }
        catch(DataIntegrityViolationException exp){
            throw new DataException("Can not delete status. Data already in use.");
        }
    }

    public void saveStatus(Status transactionType) {

        try {
            if (transactionType.getId() == null)
                statusRepository.insert(transactionType);
            else
                statusRepository.update(transactionType);

        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("Status already exists");
        }
    }

    public List<Status> getStatusList(){
        return statusRepository.getList();
    }

    public Status getStatus(Long id){
        return statusRepository.get(id);
    }

    public List<Status> search(String param) {

        return statusRepository.search(param);
    }
}
