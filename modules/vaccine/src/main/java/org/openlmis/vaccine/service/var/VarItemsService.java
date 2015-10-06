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


import org.openlmis.vaccine.domain.var.VarItems;
import org.openlmis.vaccine.repository.var.VarItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VarItemsService {

    @Autowired
    private VarItemsRepository repository;

    public List<VarItems> getAll(){
        return repository.getAll();
    }
    public List<VarItems> getItemsByPackage(String shipmentnumber){
        return repository.getItemsByPackage(shipmentnumber);
    }
    public List<VarItems> getItemsByLot(String shipmentnumber,String lotnumber){
        return repository.getItemsByLot(shipmentnumber,lotnumber);
    }

    public void save(VarItems var_items){
        if(var_items.getId() == null){
            repository.insert(var_items);
        }else {
            repository.update(var_items);
        }
    }

    public VarItems getById(Long id){
        return repository.getById(id);
    }

    public void delete(Long id){
        repository.delete(id);
    }
}
