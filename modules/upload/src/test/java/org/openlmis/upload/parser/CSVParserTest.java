package org.openlmis.upload.parser;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-upload.xml")
public class CSVParserTest {

    @Autowired
    ImportFieldParser importFieldParser;


    @Test
    public void shouldCreateAllRecordsInCSVFile() throws Exception {
        CSVParser csvParser = new CSVParser(importFieldParser);
        File csvFile = new File(this.getClass().getResource("/upload_dummyImportable.csv").getFile());

        DummyRecordHandler recordHandler = new DummyRecordHandler();
        csvParser.process(csvFile, DummyImportable.class, recordHandler);

        List<Importable> importedObjects = recordHandler.getImportedObjects();

        assertEquals(2, importedObjects.size());
        assertEquals(23, ((DummyImportable)importedObjects.get(0)).getMandatoryIntField());
        assertEquals("Random1", ((DummyImportable)importedObjects.get(0)).getMandatoryStringField());

    }


}
