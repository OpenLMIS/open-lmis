package org.openlmis.utils.csv;

import org.junit.Test;
import org.openlmis.core.domain.Product;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvParserServiceTest1 {

    @Test
    public void shouldGetListOfListOfObjectsForCsvFile() throws Exception {

        File csvFile = new File(this.getClass().getResource("/BulkLoadProducts.csv").getFile());
        CsvParserService csvParserService = new CsvParserService();
        CellProcessor[] csvCellProcessor = CsvCellProcessor.getProductProcessors();

        List<List<Object>> csvData = csvParserService.parse(csvFile, csvCellProcessor, Product.class);

        assertThat(csvData.size(),is(4));
        assertThat(((Product)csvData.get(0)).getProductCode(),is("123"));
        assertThat(((Product)csvData.get(1)).getProductCode(),is("234"));
  }
}