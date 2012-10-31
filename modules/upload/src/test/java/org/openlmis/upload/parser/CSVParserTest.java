package org.openlmis.upload.parser;


import org.junit.Test;
import org.openlmis.upload.DummyRecordHandler;
import org.openlmis.upload.model.DummyImportable;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = "classpath*:applicationContext-upload.xml")
public class CSVParserTest {

    @Test
    public void shouldProcessCsvFile() throws Exception {

        DummyRecordHandler recordHandler = new DummyRecordHandler();
        
        CSVParser csvParser = new CSVParser(new File(this.getClass().getResource("/upload_dummyImportable.csv").getFile()),
                DummyImportable.class, recordHandler);
        csvParser.process();
        assertEquals(2, recordHandler.getImportedObjects().size());


    }


}
