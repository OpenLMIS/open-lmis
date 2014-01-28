/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.util;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NoArgsConstructor
public class SelectedFilterHelper {

  @Autowired
  private ProcessingPeriodRepository periodService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProductRepository productService;



  public String getSelectedFilterString(Map<String, String[]> params){
    String filterSummary = "";

    String product = params.get("product")[0];
    String program = params.get("program")[0];
    String period =  params.get("period")[0];
    // these filters are essential for all reports and these lines should be fairly re-used.

    filterSummary = "Program: " + programService.getById(Long.parseLong(program)).getName();
    filterSummary += "\nPeriod: " + periodService.getById(Long.parseLong(period)).getName();

    if(product.isEmpty()){
      filterSummary += "\nProduct: All Products" ;
    }else if(product.equalsIgnoreCase("0")){
      filterSummary += "\nProduct: Indicator / Tracer Commodities" ;
    }else{
      filterSummary += "Product: " + productService.getById(Long.parseLong(product)).getFullName();
    }

    return filterSummary;
  }

}
