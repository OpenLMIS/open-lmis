package org.openlmis.web.controller.vaccine.demographic;

import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.service.demographics.DemographicEstimateCategoryService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Arrays.asList;

@Controller
@RequestMapping(value = "/vaccine/demographic/estimate/")
public class DemographicEstimateCategoryController extends BaseController{

  @Autowired
  DemographicEstimateCategoryService service;

  @RequestMapping("categories")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("estimate_categories", service.getAll());
  }

  @RequestMapping("category/{id}")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable Long id){
    return OpenLmisResponse.response("estimate_category", service.getById(id));
  }

  @RequestMapping("category/save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody DemographicEstimateCategory category){
    service.save(asList(category));
    return OpenLmisResponse.response("estimate_category", category);
  }

}
