package org.openlmis.vaccine.repository.smt;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.openlmis.vaccine.domain.Countries;
import org.openlmis.vaccine.repository.mapper.CountriesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class CountriesRepository {
    @Autowired
    private CountriesMapper countriesMapper;
    public List<Countries> loadCountriesList(){
        return this.countriesMapper.loadAllList();
    }
    public void addCountries(Countries countries){
        this.countriesMapper.insert(countries);
    }
    public Countries loadCountriesDetail(long id){
        return  this.countriesMapper.getById(id);
    }
    public void updateCountries(Countries countries){
        this.countriesMapper.update(countries);
    }
    public void removeCountries(Countries countries){
        this.countriesMapper.delete(countries);
    }

    public List<Countries> searchForCountries(String param) {
        return this.countriesMapper.searchForCountriesList(param);
    }
}
