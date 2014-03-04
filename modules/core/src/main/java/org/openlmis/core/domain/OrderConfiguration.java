/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OrderConfiguration represents the configuration for Order file to be downloaded. Extends EDIConfiguration for basic attributes like
 * headerIncluded. Defines filePrefix for order file to be generated.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderConfiguration extends EDIConfiguration {

  private String filePrefix;

}
