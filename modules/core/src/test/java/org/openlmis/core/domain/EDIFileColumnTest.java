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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

@Category(UnitTests.class)
public class EDIFileColumnTest {


  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfPositionIsZero() throws Exception {
    EDIFileColumn column = new EDIFileColumn("name", "Label", true, true, 0, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("file.invalid.position");

    column.validate();
  }

  @Test
  public void shouldThrowErrorIfPositionIsNull() throws Exception {
    EDIFileColumn column = new EDIFileColumn("name", "Label", true, true, null, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("file.invalid.position");

    column.validate();
  }
}
