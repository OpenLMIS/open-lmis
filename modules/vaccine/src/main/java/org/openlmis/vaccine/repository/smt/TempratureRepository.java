package org.openlmis.vaccine.repository.smt;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.openlmis.vaccine.domain.smt.Temperature;
import org.openlmis.vaccine.repository.mapper.smt.TempratureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Deprecated
public class TempratureRepository {
    @Autowired
    private TempratureMapper tempratureMapper;
    public List<Temperature> loadTempratureList(){
        return this.tempratureMapper.loadAllList();
    }
    public void addTemprature(Temperature temperature){
        this.tempratureMapper.insert(temperature);
    }
    public Temperature loadTempratureDetail(long id){
        return  this.tempratureMapper.getById(id);
    }
    public void updateTemprature(Temperature temperature){
        this.tempratureMapper.update(temperature);
    }
    public void removeTemprature(Temperature temperature){
        this.tempratureMapper.delete(temperature);
    }

    public List<Temperature> searchForTempratureList(String param) {
        return this.tempratureMapper.searchTempratureList(param);
    }
}
