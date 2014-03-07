/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents exception occurred while processing upload.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UploadException extends RuntimeException {

  private String code;
  private String[] params = new String[0];

  public UploadException(String code) {
    super(code);
    this.code = code;
  }

  public UploadException(String code,String... params){
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
