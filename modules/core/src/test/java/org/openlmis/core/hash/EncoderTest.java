/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.hash;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openlmis.core.hash.Encoder.hash;

@Category(UnitTests.class)
public class EncoderTest {

  @Test
  public void shouldHashPasswordBySha512AndUtf8EncodedByBase62() {
    String hashedPassword = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    String passwordPlainText = "Admin123";

    String encodedPassword = hash(passwordPlainText);
    assertThat(encodedPassword, is(equalTo(hashedPassword)));
    shouldHashNullPasswordToNull();

  }

  @Test
  public void shouldHashNullPasswordToNull() {
    assertThat(hash(null), is(nullValue()));
  }

  @Test
  public void shouldNotContainCharactersNotPermittedByBase62WhenReturningHashPasswordBySha512AndUtf8() throws Exception {
    String passwordPlainText = "Admin123";

    String encodedPassword = hash(passwordPlainText);
    assertThat(encodedPassword.indexOf("/"), is(-1));
    assertThat(encodedPassword.indexOf("="), is(-1));
    assertThat(encodedPassword.indexOf("+"), is(-1));
    shouldHashNullPasswordToNull();

  }
}
