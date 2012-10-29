package org.openlmis.utils.csv;

import org.junit.Test;
import org.openlmis.core.domain.Product;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvParserServiceTest {

    @Test
    public void shouldGetListOfListOfObjectsForCsvFile() throws Exception {

        File csvFile = new File("/Users/Manan/Documents/open-lmis/modules/utils/src/test/java/org/openlmis/utils/resources/BulkLoadProducts.csv");
        CsvParserService csvParserService = new CsvParserService();
        CellProcessor[] csvCellProcessor = CsvCellProcessor.getProductProcessors();

        List<List<Object>> csvData = csvParserService.parse(csvFile, csvCellProcessor, Product.class);

        assertThat(csvData.size(),is(4));
        assertThat(((Product)csvData.get(0)).getProductCode(),is("123"));
        assertThat(((Product)csvData.get(1)).getProductCode(),is("234"));
  }
}
