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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class PaginationTest {

  @Test
  public void shouldSetValues() {
    Pagination pagination = new Pagination(2, 10);

    assertThat(pagination.getOffset(), is(10));
    assertThat(pagination.getLimit(), is(10));
    assertThat(pagination.getPage(), is(2));
  }

  @Test
  public void shouldSetTotalRecordsAndNumberOfPagesWhenDistributionIsEven() {
    Pagination pagination = new Pagination(2, 10);

    pagination.setTotalRecords(50);

    assertThat(pagination.getTotalRecords(), is(50));
    assertThat(pagination.getNumberOfPages(), is(5));
  }

 @Test
  public void shouldSetTotalRecordsAndNumberOfPagesWhenDistributionIsOdd() {
    Pagination pagination = new Pagination(2, 10);

    pagination.setTotalRecords(51);

    assertThat(pagination.getTotalRecords(), is(51));
    assertThat(pagination.getNumberOfPages(), is(6));
  }

  @Test
  public void shouldSetTotalRecordsAndNumberOfPagesWhenNoRecordsFound() {
    Pagination pagination = new Pagination(2, 10);

    pagination.setTotalRecords(0);

    assertThat(pagination.getTotalRecords(), is(0));
    assertThat(pagination.getNumberOfPages(), is(1));
  }

  @Test
  public void shouldSetTotalRecordsAndNumberOfPagesWhenLimitZero() {
    Pagination pagination = new Pagination(2, 0);

    pagination.setTotalRecords(50);

    assertThat(pagination.getTotalRecords(), is(50));
    assertThat(pagination.getNumberOfPages(), is(1));
  }
}
