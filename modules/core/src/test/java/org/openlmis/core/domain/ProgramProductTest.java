/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;

@Category(UnitTests.class)
public class ProgramProductTest {
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfPricePerDosageIsNegativeOnValidation() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProduct.setCurrentPrice(new Money("-0.01"));
    expectException.expect(DataException.class);
    expectException.expectMessage("programProduct.invalid.current.price");
    programProduct.validate();
  }
}
