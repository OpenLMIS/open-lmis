/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.smt;

import org.openlmis.vaccine.domain.Status;
import org.openlmis.vaccine.repository.mapper.smt.StatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class StatusRepository {
    @Autowired
    private StatusMapper statusMapper;

    public List<Status> getAll(){
        return statusMapper.getAll();
    }

    public void delete(Long id){

        statusMapper.delete(id);
    }

    public void insert(Status receivedStatus){
        statusMapper.insert(receivedStatus);
    }

    public void update(Status receivedStatus){
        statusMapper.update(receivedStatus);
    }

    public List<Status> getList(){
        return statusMapper.getList();
    }


    public Status get(Long id) {
        return statusMapper.get(id);
    }

    public List<Status> search(String param) {
        return statusMapper.search(param);
    }
}
