package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.service.EquipmentEnergyTypeService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/equipment/energy-type/")
public class EquipmentEnergyTypeController extends BaseController {

  @Autowired
  EquipmentEnergyTypeService service;

  @RequestMapping(value="list",method= GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')" +
      " or @permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("energy_types",service.getAll());
  }

  @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentEnergyType energyType){
    try {
      service.save(energyType);
    }catch(DuplicateKeyException exp){
      return OpenLmisResponse.error("Duplicate Energy Name Exists in DB.", HttpStatus.BAD_REQUEST);
    }
    return OpenLmisResponse.response("status","success");
  }


  @RequestMapping(method = GET, value = "{id}")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable(value="id") Long id){
    return OpenLmisResponse.response("energyType",service.getById(id));
  }

}
