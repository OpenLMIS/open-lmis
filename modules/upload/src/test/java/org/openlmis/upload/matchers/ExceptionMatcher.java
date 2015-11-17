/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.matchers;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.openlmis.upload.exception.UploadException;

public class ExceptionMatcher {

  public static Matcher<UploadException> uploadExceptionMatcher(final String code, final String... params) {
    return new ArgumentMatcher<UploadException>() {
      @Override
      public boolean matches(Object argument) {
        UploadException uploadException = (UploadException) argument;
        if (!uploadException.getCode().equals(code)) return false;
        for (int index = 0; index < params.length; index++) {
          if (!uploadException.getParams()[index].equals(params[index])) return false;
        }
        return true;
      }
    };
  }

}
