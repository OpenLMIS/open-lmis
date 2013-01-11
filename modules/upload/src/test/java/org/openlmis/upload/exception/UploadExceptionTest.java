package org.openlmis.upload.exception;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UploadExceptionTest {

  @Test
  public void shouldGetMessageWithCodeAndParams() {
    UploadException uploadException = new UploadException("code", "1", "2", "3");
    assertThat(uploadException.getMessage(), is("code: code, params: { 1; 2; 3 }"));
  }
}
