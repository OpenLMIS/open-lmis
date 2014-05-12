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


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

  @Getter
  @Setter
  public Integer page;

  @Getter
  @Setter
  public Integer pageSize;

  @Getter
  public Integer numberOfPages;

  @Setter
  public Integer offset;

  @Getter
  public Integer totalRecords;

  public Pagination(Integer page, Integer pageSize) {
    this.page = page;
    this.pageSize = pageSize;
  }

  public Integer getOffset() {
    return (page - 1) * pageSize;
  }

  private void setNumberOfPages() {
    numberOfPages = totalRecords / pageSize;
    if (totalRecords % pageSize > 0) {
      numberOfPages++;
    }

    if (numberOfPages == 0) {
      numberOfPages++;
    }
  }

  public void setTotalRecords(Integer totalRecords) {
    this.totalRecords = totalRecords;
    setNumberOfPages();
  }
}
