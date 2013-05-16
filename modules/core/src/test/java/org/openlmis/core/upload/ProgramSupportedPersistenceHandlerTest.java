/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
@Category(UnitTests.class)
public class ProgramSupportedPersistenceHandlerTest {

  @Mock
  FacilityService facilityService;

  private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programSupportedPersistenceHandler = new ProgramSupportedPersistenceHandler(facilityService);
  }

  @Test
  public void shouldSaveProgramSupported() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupportedPersistenceHandler.save(programSupported);
    verify(facilityService).uploadSupportedProgram(programSupported);
  }

 }
