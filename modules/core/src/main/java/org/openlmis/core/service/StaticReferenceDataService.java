/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Exposes the services for getting reference data from properties file.
 */

@Service
@PropertySource({"classpath:/default.properties", "classpath:/app.properties"})
public class StaticReferenceDataService {

  @Autowired
  Environment environment;

  public String getPropertyValue(String propertyName) {
    return environment.getProperty(propertyName);
  }

  public boolean getBoolean(String propertyName) {
    return "true".equals(environment.getProperty(propertyName));
  }
}
