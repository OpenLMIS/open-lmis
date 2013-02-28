package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;

import static org.mockito.Mockito.mock;

public class AbstractModelPersistenceHandlerTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldAppendRowNumberToExceptionMessage() throws Exception {
    AbstractModelPersistenceHandler handler = new AbstractModelPersistenceHandler() {
      @Override
      protected void save(Importable modelClass, Integer modifiedBy) {
        throw new DataException("error");
      }
    };

    Importable importable = mock(Importable.class);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("code: upload.record.error, params: { error; 1 }");

    handler.execute(importable, 2, 1);
  }
}
