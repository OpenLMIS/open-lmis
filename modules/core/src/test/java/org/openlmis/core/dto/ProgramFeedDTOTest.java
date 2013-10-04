/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
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
