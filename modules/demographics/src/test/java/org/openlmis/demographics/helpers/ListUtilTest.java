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

package org.openlmis.demographics.helpers;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.openlmis.demographics.helpers.ListUtil.emptyIfNull;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ListUtilTest {

  @Test
  public void shouldReturnEmptyCollectionWhenParamIsNull() throws Exception{

    List<String> value = null;

    List<String> response = emptyIfNull(value);

    assertThat(response.size(), is(0));
  }

  @Test
  public void shouldReturnListWhenNotNull()throws Exception{
    List<String> strings = asList("string");

    List<String> response  = emptyIfNull(strings);

    assertThat(response, is(strings));
  }

}