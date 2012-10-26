import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CSVParserServiceTest {
    @Test
    public void shouldBeAbleToParseALineItemInCSVFile() throws IOException {
        String productCode = new String("55049");
        String alternateItemCode = new String("");
        String productPrimaryName = new String("TDF/FTC/EFV");

        List<String[]> lineItems = new CSVParserService().parseCSV(new File("/Users/Manan/Documents/open-lmis/modules/admin-web/src/test/test-resources/BulkLoadProducts.csv"));

        assertThat(lineItems.get(1)[0],is(productCode));
        assertThat(lineItems.get(1)[1],is(alternateItemCode));
        assertThat(lineItems.get(1)[6],is(productPrimaryName));
    }
}
