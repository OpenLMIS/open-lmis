/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


package org.openlmis.core.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramFeedDTOTest {

  @Test
  public void shouldGetSerializedContents() throws Exception {
    Program program = make(a(defaultProgram));

    String serializedFeed = new ProgramFeedDTO(program).getSerializedContents();

    assertThat(serializedFeed, is("{\"programCode\":\"YELL_FVR\",\"programName\":\"Yellow Fever\"}"));
  }
}
