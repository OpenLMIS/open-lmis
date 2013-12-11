/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.service.*;
import org.openlmis.report.mapper.lookup.FacilityTypeReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.String;
import java.text.DateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AverageConsumptionReportFilter {

  @Autowired
  private ProductCategoryService productCategoryService;

  @Autowired
  private ProductService productService;

  @Autowired
  private GeographicZoneService geographicZoneService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private FacilityTypeReportMapper facilityTypeService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private FacilityService facilityService;


  //top filters
  private int userId;

  // period selections
  private String periodType;
  private int yearFrom;
  private int yearTo;
  private int monthFrom;
  private int monthTo;
  private int quarterFrom;
  private int quarterTo;
  private int semiAnnualFrom;
  private int semiAnnualTo;

  private int facilityTypeId;
  private String facilityType;

  private int zoneId;
  private String zone;

  private String productNames;
  private String productId;

  private long productCategoryId;
  private String productCategories;

  private long rgroupId;
  private String requisitionGroup;

  private Long facilityId;
  private String facility;

  private Long programId;
  private String program;

  private int pdformat;

  private Date startDate;
  private Date endDate;

  @Override
  public String toString(){

    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

    if(getFacilityId() == 0){
      setFacilityType("All facility types");
    }else{
      //TODO: write the facility type service
      //facilityTypeService.
    }

    if(programId != 0){
      this.setProgram(programService.getById(programId).getName());
    }

    if(getZoneId() != 0){
      setZone(geographicZoneService.getById( getZoneId()).getName() );
    }  else{
      setZone("All geographical zones");
    }

    if(getRgroupId() != 0){
      //setRequisitionGroup(requisitionGroupService.getById(rgroupId).);
    }else{
      setRequisitionGroup("All requisition groups");
    }

    if(getFacilityId()!= 0){
      setFacility(facilityService.getById(getFacilityId()).getName());
    }else{
      setFacility("All facilities");
    }

    //TODO: copy over the list of products here.

    return "Program: " + this.getProgram() + "\n" +
           "Period : "+  dateFormatter.format(this.getStartDate()) +" - "+ dateFormatter.format(this.getEndDate()) +" \n" +
           "Geographic Zones: " + getZone() + "\n" +
           "Requisition Group: " + getRequisitionGroup() + "\n" +
           "Facility Types: "+ this.getFacilityType() +"\n" +
           "Facility: " + this.getFacility()+ "\n" +
           "Product Category: " + this.getProductCategories() + "\n" +
           "Product: " + this.getProductNames();


    }
}


