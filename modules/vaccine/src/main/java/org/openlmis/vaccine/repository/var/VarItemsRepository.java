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

package org.openlmis.vaccine.repository.var;


import org.openlmis.vaccine.domain.var.VarItems;
import org.openlmis.vaccine.repository.mapper.var.VarItemsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VarItemsRepository {

    @Autowired
    private VarItemsMapper mapper;

    public List<VarItems> getAll(){
        return mapper.getAll();
    }
    public List<VarItems> getItemsByPackage(String shipmentnumber){
        return mapper.getItemsByPackage(shipmentnumber);
    }
    public List<VarItems> getItemsByLot(String shipmentnumber,String lotnumber){
        return mapper.getItemsByLot(shipmentnumber,lotnumber);
    }

    public void update(VarItems var_items){
        mapper.update(var_items);
    }

    public void insert(VarItems var_items){
        mapper.insert(var_items);
    }

    public void delete(Long id){
        mapper.deleteById(id);
    }

    public VarItems getById(Long id) {
        return mapper.getById(id);
    }

}