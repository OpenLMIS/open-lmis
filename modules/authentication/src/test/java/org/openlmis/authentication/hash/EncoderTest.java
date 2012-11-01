package org.openlmis.authentication.hash;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openlmis.utils.hash.Encoder.hash;

public class EncoderTest {

    @Test
    public void shouldHashPasswordBySha512AndUtf8() {
        String hashedPassword =  "TQskzK3iLfbRVHeM1muvBCiKribfl6lh8+o91hb74G3OvsybvkzpPI4S3KIeWTXAiwlUU0iSxWi4wSuS8mokSA==";
        String passwordPlainText = "Admin123";

        String encodedPassword = hash(passwordPlainText);
        assertThat(encodedPassword, is(equalTo(hashedPassword)));
    }
}
