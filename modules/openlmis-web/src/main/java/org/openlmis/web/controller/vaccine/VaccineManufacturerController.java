/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller.vaccine;

import org.openlmis.vaccine.domain.Manufacturer;
import org.openlmis.vaccine.domain.ManufacturerProduct;
import org.openlmis.vaccine.service.ManufacturerService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/vaccine/manufacturer")
public class VaccineManufacturerController extends BaseController{


    @Autowired
    ManufacturerService manufacturerService;

    @RequestMapping(value = "/create", method = POST, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_MANUFACTURER')")
    public ResponseEntity insert(@RequestBody Manufacturer manufacturer, HttpServletRequest request) {

        manufacturer.setCreatedBy(loggedInUserId(request));
        manufacturer.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            manufacturerService.updateManufacturer(manufacturer);
        } catch(DuplicateKeyException exp){
            return OpenLmisResponse.error("There is record with the same vaccine quantification year already.", HttpStatus.BAD_REQUEST);
        }

        response = success("Vaccine manufacuted saved successfully");
        return response;
    }

    @RequestMapping(value = "/delete/{id}", method = DELETE, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_MANUFACTURER')")
    public ResponseEntity deleteVaccineManufacturer(@PathVariable(value="id") Long id){

        try{

            manufacturerService.deleteManufacturer(id);
        }
        catch(DataIntegrityViolationException exp){
            return OpenLmisResponse.error("There is a data dependent on the selected manufacturer", HttpStatus.BAD_REQUEST);
        }


        ResponseEntity<OpenLmisResponse> response;
        response = success("Vaccine manufacutrer deleted successfully");
        return response;
    }

    @RequestMapping(value = "/list", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineManufacturers() {

        return OpenLmisResponse.response("vaccineManufacturers", manufacturerService.getAll());
    }

    @RequestMapping(value = "/get/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineManufacturer(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("vaccineManufacturer", manufacturerService.getManufacturer(id));
    }

    /*** Manufacturer product Mapping *****/

    @RequestMapping(value = "/getManufacturerProducts/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineManufacturerProductMapping(@PathVariable(value="id") Long id){

        Manufacturer manufacturer = manufacturerService.getManufacturer(id);
        List<ManufacturerProduct> product = manufacturerService.getProductMapping(id);

        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("vaccineManufacturer", manufacturer);
        response.getBody().addData("productMapping", product);
        return response;
    }

    @RequestMapping(value = "/getProduct/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getProductMappingByMappingId(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("manufacturerProduct", manufacturerService.getProductMappingByMappingId(id));
    }

    @RequestMapping(value = "/deleteProduct/{id}", method = DELETE, headers = ACCEPT_JSON)
    public ResponseEntity deleteProductMapping(@PathVariable(value="id") Long id){

        manufacturerService.deleteProductMapping(id);

        ResponseEntity<OpenLmisResponse> response;
        response = success("Product mapping deleted successfully");
        return response;
    }

    @RequestMapping(value = "/createProduct", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity createUpdate(@RequestBody ManufacturerProduct manufacturerProduct, HttpServletRequest request){

        manufacturerProduct.setCreatedBy(loggedInUserId(request));
        manufacturerProduct.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            manufacturerService.addUpdateProductMapping(manufacturerProduct);
        } catch(DuplicateKeyException exp) {
            return OpenLmisResponse.error("Duplicate key not allowed.", HttpStatus.BAD_REQUEST);
        }
        catch(DataIntegrityViolationException exp){
            return OpenLmisResponse.error("Invalid product code", HttpStatus.BAD_REQUEST);
        }

        response = success("Product mapping saved successfully");
        return response;
    }

}
