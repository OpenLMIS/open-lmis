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

import org.openlmis.vaccine.domain.smt.Manufacturer;
import org.openlmis.vaccine.domain.smt.ManufacturerProduct;
import org.openlmis.vaccine.repository.smt.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManufacturerService {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    public List<Manufacturer> getAll(){
        return manufacturerRepository.getAll();
    }

    public void updateManufacturer(Manufacturer vaccineManufacturer) {

        if(vaccineManufacturer.getId() == null){
            manufacturerRepository.insert(vaccineManufacturer);
        }
        else
            manufacturerRepository.update(vaccineManufacturer);
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturerRepository.getAll();
    }

    public Manufacturer getManufacturer(Long id) {
        return manufacturerRepository.get(id);
    }

    public void deleteManufacturer(Long id) {
        manufacturerRepository.delete(id);
    }

    public List<ManufacturerProduct> getProductMapping(Long manufacturerId){
        return manufacturerRepository.getProductMapping(manufacturerId);
    }

    public ManufacturerProduct getProductMappingByMappingId(Long productMappingId){
        return manufacturerRepository.getProductMappingByMappingId(productMappingId);
    }

    public void deleteProductMapping(Long productMappingId){
        manufacturerRepository.deleteProductMapping(productMappingId);
    }

    public void insertProductMapping(ManufacturerProduct manufacturerProduct){
        manufacturerRepository.insertProductMapping(manufacturerProduct);
    }

    public void updateProductMapping(ManufacturerProduct manufacturerProduct){
        manufacturerRepository.updateProductMapping(manufacturerProduct);
    }

    public void addUpdateProductMapping(ManufacturerProduct manufacturerProduct){

        if(manufacturerProduct.getId() == null){
            insertProductMapping(manufacturerProduct);
        }
        else
            updateProductMapping(manufacturerProduct);
    }
}
