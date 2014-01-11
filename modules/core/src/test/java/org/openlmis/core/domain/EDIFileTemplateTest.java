package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class EDIFileTemplateTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfPositionIsDuplicate() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("test_column1", "Test Column 1", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    EDIFileTemplate fileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("file.duplicate.position");
    fileTemplate.validateAndSetModifiedBy(1l, asList("productCode", "orderId", "quantityShipped"));
  }

  @Test
  public void shouldValidateShipmentFileTemplateAndThrowErrorIfMandatoryColumnIsNotIncluded() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("productCode", "Product Code", false, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    EDIFileTemplate fileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumns);

    exException.expect(DataException.class);
    exException.expectMessage("file.mandatory.columns.not.included");
    fileTemplate.validateAndSetModifiedBy(1l, asList("productCode", "orderId", "quantityShipped"));
  }

  @Test
  public void shouldSetModifiedByIfNoError() {
    EDIConfiguration shipmentConfiguration = new EDIConfiguration();
    List<EDIFileColumn> shipmentFileColumns = new ArrayList<>();
    EDIFileColumn shipmentFileColumn = new EDIFileColumn("productCode", "Product Code", true, true, 1, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    shipmentFileColumn = new EDIFileColumn("test_column2", "Test Column 2", true, true, 2, "dd/MM/yy");
    shipmentFileColumns.add(shipmentFileColumn);
    EDIFileTemplate fileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumns);

    fileTemplate.validateAndSetModifiedBy(1l, asList("productCode", "orderId", "quantityShipped"));

    assertThat(shipmentConfiguration.getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(0).getModifiedBy(), is(1L));
    assertThat(shipmentFileColumns.get(1).getModifiedBy(), is(1L));
  }

  @Test
  public void shouldGetDateFormatForIncludedColumn() throws Exception {
    EDIConfiguration configuration = new EDIConfiguration();
    List<EDIFileColumn> columns = new ArrayList<>();
    EDIFileColumn column = new EDIFileColumn("periodStartDate", "Period Start Date", true, true, 1, "dd/MM/yy");
    columns.add(column);

    EDIFileTemplate fileTemplate = new EDIFileTemplate(configuration, columns);

    assertThat(fileTemplate.getDateFormatForColumn("periodStartDate"), is("dd/MM/yy"));
  }

  @Test
  public void shouldReturnNullIfDateColumnNotIncluded() throws Exception {
    EDIConfiguration configuration = new EDIConfiguration();
    List<EDIFileColumn> columns = new ArrayList<>();
    EDIFileColumn column = new EDIFileColumn("periodStartDate", "Period Start Date", false, true, 1, "dd/MM/yy");
    columns.add(column);

    EDIFileTemplate fileTemplate = new EDIFileTemplate(configuration, columns);

    assertNull(fileTemplate.getDateFormatForColumn("periodStartDate"));
  }

  @Test
  public void shouldReturnNullIfColumnNameInvalid() throws Exception {
    EDIConfiguration configuration = new EDIConfiguration();
    List<EDIFileColumn> columns = new ArrayList<>();
    EDIFileColumn column = new EDIFileColumn("periodStartDate", "Period Start Date", false, true, 1, "dd/MM/yy");
    columns.add(column);

    EDIFileTemplate fileTemplate = new EDIFileTemplate(configuration, columns);

    assertNull(fileTemplate.getDateFormatForColumn("invalidColumnName"));
  }

  @Test
  public void shouldReturnRowOffsetIfConfigIncludesHeaderLine() throws Exception {
    EDIConfiguration configuration = new EDIConfiguration();
    configuration.setHeaderInFile(true);
    EDIFileTemplate fileTemplate = new EDIFileTemplate(configuration, null);

    assertThat(fileTemplate.getRowOffset(), is(1));
  }

  @Test
  public void shouldReturnRowOffsetAsZeroIfConfigDoesNotIncludeHeaderLine() throws Exception {
    EDIConfiguration configuration = new EDIConfiguration();
    configuration.setHeaderInFile(false);
    EDIFileTemplate fileTemplate = new EDIFileTemplate(configuration, null);

    assertThat(fileTemplate.getRowOffset(), is(0));
  }
}
