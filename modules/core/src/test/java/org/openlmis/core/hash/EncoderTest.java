package org.openlmis.core.hash;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openlmis.core.hash.Encoder.hash;

public class EncoderTest {

  @Test
  public void shouldHashPasswordBySha512AndUtf8() {
    String hashedPassword = "TQskzK3iLfbRVHeM1muvBCiKribfl6lh8+o91hb74G3OvsybvkzpPI4S3KIeWTXAiwlUU0iSxWi4wSuS8mokSA==";
    String passwordPlainText = "Admin123";

    String encodedPassword = hash(passwordPlainText);
    assertThat(encodedPassword, is(equalTo(hashedPassword)));
    shouldHashNullPasswordToNull();

  }

  @Test
  public void shouldHashNullPasswordToNull() {
    assertThat(hash(null), is(nullValue()));
  }
}
