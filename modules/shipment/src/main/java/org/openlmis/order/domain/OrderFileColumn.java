/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.EDIFileColumn;

/**
 * Represents the attributes for a column in Order file. It extends EDIFileColumn for basic configuration and provides
 * configuration for each column like columnLabel, format.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFileColumn extends EDIFileColumn{

  private Boolean openLmisField;
  private String columnLabel;
  private String format;
  private String keyPath;
  private String nested;
  private Boolean includeInOrderFile;

}
