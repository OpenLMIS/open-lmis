/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Data
public class OpenLmisMessage {

  private String code;
  private String[] params = new String[0];

  public OpenLmisMessage(String code) {
    this.code = code;
  }

  public OpenLmisMessage(String code, String... params) {
    this.code = code;
    this.params = params;
  }

  @Override
  public String toString(){
    if(params.length == 0) return code;

    StringBuilder messageBuilder = new StringBuilder("code: "+code+ ", params: { ");
    for(String param : params){
      messageBuilder.append("; ").append(param);
    }
    messageBuilder.append(" }");
    return messageBuilder.toString().replaceFirst("; ","");
  }
}
