/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.matchers;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;

public class Matchers {

  public static Matcher<Program> programMatcher(final Long id) {
    return new ArgumentMatcher<Program>() {
      @Override
      public boolean matches(Object argument) {
        Program program = (Program) argument;
        return program.getId().equals(id);
      }
    };
  }

  public static Matcher<ProcessingPeriod> periodMatcher(final Long id) {
    return new ArgumentMatcher<ProcessingPeriod>() {
      @Override
      public boolean matches(Object argument) {
        ProcessingPeriod period = (ProcessingPeriod) argument;
        return period.getId().equals(id);
      }
    };
  }

  public static Matcher<Facility> facilityMatcher(final Long id) {
    return new ArgumentMatcher<Facility>() {
      @Override
      public boolean matches(Object argument) {
        Facility facility = (Facility) argument;
        return facility.getId().equals(id);
      }
    };
  }
}
