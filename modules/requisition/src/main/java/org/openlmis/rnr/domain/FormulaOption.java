/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.rnr.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FormulaOption {

  private static final Logger LOGGER = LoggerFactory.getLogger(FormulaOption.class);

  private List<Formula> formulas;

  public FormulaOption(String options){
    if(!options.equals("DEFAULT")){
      ObjectMapper mapper = new ObjectMapper();
      try{
        formulas = mapper.readValue(options, new TypeReference<List<Formula>>(){}  );
      } catch(Exception exp){
        LOGGER.error("Rnr formula option could not be parsed with the following exception", exp);
      }
    }
  }

}
