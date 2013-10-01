/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FormulaOption {

  private List<Formula> formulas;

  public FormulaOption(String options){
    if(options != "DEFAULT"){
      ObjectMapper mapper = new ObjectMapper();
      try{
        formulas = mapper.readValue(options, new TypeReference<List<Formula>>(){}  );
      } catch(Exception exp){

      }
    }
  }

}
