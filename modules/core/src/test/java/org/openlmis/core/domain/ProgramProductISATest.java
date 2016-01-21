/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ISABuilder;
import org.openlmis.db.categories.UnitTests;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramProductISATest
{

  @Test
  public void shouldCalculateISA() throws Exception
  {
    ISA isa = new ISA(3.33, 44, 1.4, 33.9, 23, 345, 11, null);
    ProgramProductISA programProductISA = new ProgramProductISA(3L, isa);

    Integer isaAmount = programProductISA.calculate(420L);
    assertThat(isaAmount, is(108));
  }

  @Test public void builderShouldBeAbleToSetAllFields() throws Exception
  {
    ISA isa = ISABuilder.build();
    VerifyAllFieldsAreSet(isa);
  }


  /*
    Verify that all properties defined within the specified field (as opposed to within any of its ancestors)
    have been set to non-default values.

    For the sake of reuse, this method should probably be moved elsewhere. Because it’s intended to access
    the package-private members of our domain-objects, however, it currently must reside within the same
    package as the class-under-test.
  */
  public static void VerifyAllFieldsAreSet(Object object)
  {
    try
    {
      Class fieldType;
      String fieldName;
      String className = object.getClass().getSimpleName();
      final String err = "Found default value for %s.%s, but expected that all fields would have non-default values set.";
      for (Field field : object.getClass().getDeclaredFields())
      {
        fieldType = field.getType();
        fieldName = field.getName();

        if (fieldType == boolean.class && field.getBoolean(object) == false)
          fail(String.format(err, className, fieldName));
        else if (fieldType.isPrimitive() && field.getDouble(object) == 0)
          fail(String.format(err, className, fieldName));
        else if (!fieldType.isPrimitive() && field.get(object) == null)
          fail(String.format(err, className, fieldName));
      }
    }
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
