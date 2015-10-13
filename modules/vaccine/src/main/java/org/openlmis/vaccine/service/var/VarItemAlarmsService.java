/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.service.var;


import org.openlmis.vaccine.domain.var.VarItemAlarms;
import org.openlmis.vaccine.repository.var.VarItemAlarmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VarItemAlarmsService {

    @Autowired
    private VarItemAlarmsRepository repository;

    public List<VarItemAlarms> getAll(){
        return repository.getAll();
    }

    public void save(VarItemAlarms var_item_alarms){
        if(var_item_alarms.getId() == null){
            repository.insert(var_item_alarms);
        }else {
            repository.update(var_item_alarms);
        }
    }

    public VarItemAlarms getById(Long id){
        return repository.getById(id);
    }

    public void delete(Long id){
        repository.delete(id);
    }
}
