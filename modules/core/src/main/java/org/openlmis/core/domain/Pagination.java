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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.session.RowBounds;

/**
 * Pagination represents an entity that keeps track of the current page number of any record, size of a page,
 * number of pages and total records.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Pagination extends RowBounds {

  @Getter
  @Setter
  private int page;

  @Getter
  private int numberOfPages;

  @Getter
  private int totalRecords;

  public Pagination(Integer page, int limit) {
    super((page - 1) * limit, limit);
    this.page = page;
  }

  public void setTotalRecords(Integer totalRecords) {
    this.totalRecords = totalRecords;
    setNumberOfPages();
  }

  private void setNumberOfPages() {
    if (getLimit() > 0) {
      if (totalRecords % getLimit() == 0) {
        numberOfPages = totalRecords / getLimit();
      } else {
        numberOfPages = (totalRecords / getLimit()) + 1;
      }
    }
    if (numberOfPages == 0) {
      numberOfPages++;
    }
  }
}