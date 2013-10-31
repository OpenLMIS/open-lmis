/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */
package org.openlmis.core.event;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramChangeEventTest {

  @Test
  public void shouldCreateAProgramChangeEvent() throws Exception {
    ProgramChangeEvent programChangeEvent = new ProgramChangeEvent(make(a(ProgramBuilder.defaultProgram)));

    assertThat(programChangeEvent.getCategory(), is("program-catalog-changes"));
    assertThat(programChangeEvent.getTitle(), is("Program Catalog Changes"));
  }
}
