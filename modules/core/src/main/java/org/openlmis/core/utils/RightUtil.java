/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.utils;

import com.google.common.base.Predicate;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RightType;

import java.util.List;

/**
 * RightUtil is a utility class which provides basic matcher operations on Right entity.
 */

public class RightUtil {

  public static Predicate<Right> with(final String name) {
    return new Predicate<Right>() {
      @Override
      public boolean apply(Right right) {
        return right.getName().equals(name);
      }
    };
  }

  public static Predicate<Right> withType(final RightType rightType) {
    return new Predicate<Right>() {
      @Override
      public boolean apply(Right right) {
        return right.getType().equals(rightType);
      }
    };
  }

  public static Predicate<Right> withDisplayNameKey(final String displayNameKey) {
    return new Predicate<Right>() {
      @Override
      public boolean apply(Right right) {
        return right.getDisplayNameKey().equals(displayNameKey);
      }
    };
  }

  public static Predicate<Right> contains(final List<String> names) {
    return new Predicate<Right>() {
      @Override
      public boolean apply(Right right) {
        return names.contains(right.getName());
      }
    };
  }
}
