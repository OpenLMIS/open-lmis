package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

public class EDIFileColumnTest {


  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfPositionIsZero() throws Exception {
    EDIFileColumn column = new EDIFileColumn("name", "Label", true, true, 0, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("file.invalid.position");

    column.validate();
  }

  @Test
  public void shouldThrowErrorIfPositionIsNull() throws Exception {
    EDIFileColumn column = new EDIFileColumn("name", "Label", true, true, null, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("file.invalid.position");

    column.validate();
  }
}
