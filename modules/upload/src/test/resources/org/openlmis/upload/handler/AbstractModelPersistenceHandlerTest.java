package org.openlmis.upload.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.exception.UploadException;

import static org.mockito.Mockito.mock;

public class AbstractModelPersistenceHandlerTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldAppendRowNumberToExceptionMessage() throws Exception {
        AbstractModelPersistenceHandler handler = new AbstractModelPersistenceHandler() {
            @Override
            protected void save(Importable modelClass, String modifiedBy) {
                throw new RuntimeException("error");
            }
        };

        Importable importable = mock(Importable.class);
        expectedEx.expect(UploadException.class);
        expectedEx.expectMessage("error in Record No. 1");

        handler.execute(importable, 2, "user");
    }
    
}
