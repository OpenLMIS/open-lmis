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

import org.openlmis.vaccine.domain.smt.Manufacturer;
import org.openlmis.vaccine.domain.smt.ManufacturerProduct;
import org.openlmis.vaccine.repository.mapper.ManufacturerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManufacturerRepository {

    @Autowired
    private ManufacturerMapper manufacturerMapper;

    public List<Manufacturer> getAll(){
        return manufacturerMapper.getAll();
    }

    public Manufacturer get(Long manufacturerId){
         return manufacturerMapper.get(manufacturerId);
    }

    public void update(Manufacturer manufacturer){
        manufacturerMapper.update(manufacturer);
    }

    public void insert(Manufacturer manufacturer){
        manufacturerMapper.insert(manufacturer);
    }

    public void delete(Long manufacturerId) {
        manufacturerMapper.delete(manufacturerId);
    }

    public List<ManufacturerProduct> getProductMapping(Long manufacturerId) {
        return manufacturerMapper.getProductMapping(manufacturerId);
    }

    public ManufacturerProduct getProductMappingByMappingId(Long productMappingId){
        return manufacturerMapper.getProductMappingByMappingId(productMappingId);
    }

    public void deleteProductMapping(Long productMappingId){
        manufacturerMapper.deleteProductMapping(productMappingId);
    }

    public void insertProductMapping(ManufacturerProduct manufacturerProduct){
        manufacturerMapper.insertProductMapping(manufacturerProduct);
    }

    public void updateProductMapping(ManufacturerProduct manufacturerProduct){
        manufacturerMapper.updateProductMapping(manufacturerProduct);
    }

}
