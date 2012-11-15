package org.openlmis.core.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.Importable;

import static org.mockito.Mockito.mock;

public class AbstractModelPersistenceHandlerTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void shouldAppendRowNumberToExceptionMessage() throws Exception {
        AbstractModelPersistenceHandler handler = new AbstractModelPersistenceHandler() {
            @Override
            protected void save(Importable modelClass) {
                throw new RuntimeException("error");
            }
        };

        Importable importable = mock(Importable.class);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("error in Record No. 1");

        handler.execute(importable, 2);
    }
}
