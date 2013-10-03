/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.select;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ShipmentFileColumnMapperIT {

  @Autowired
  private ShipmentFileColumnMapper mapper;

  @Autowired
  private QueryExecutor queryExecutor;


  @Test
  public void shouldUpdateOnlyRequiredFields() throws SQLException {

    List params = asList("ColumnName", "label", 100, false, false, "dd/MM/yyyy");

    queryExecutor.executeUpdate("INSERT INTO shipment_file_columns" +
      "(name, dataFieldLabel, position, include, mandatory, datePattern) VALUES" +
      "(?,?,?,?,?,?)", params);
    ShipmentFileColumn shipmentFileColumn = make(a(mandatoryShipmentFileColumn,
      with(fieldName, "ColumnName"),
      with(dataFieldLabel, "new label"),
      with(columnPosition, 120),
      with(includeInShipmentFile, true),
      with(modifiedById, 22L),
      with(modifiedOnDate, new Date(9898989898L)),
      with(dateFormat, "dd/MM")));

    mapper.update(shipmentFileColumn);

    ShipmentFileColumn column = filterColumns(mapper.getAll(), "ColumnName");

    assertThat(column.getDataFieldLabel(), is("label"));
    assertThat(column.getDatePattern(), is("dd/MM"));
    assertThat(column.getInclude(), is(true));
    assertThat(column.getMandatory(), is(false));
    assertThat(column.getPosition(), is(120));
    assertThat(column.getModifiedBy(), is(22L));
    assertThat(column.getModifiedDate(), is(new Date(9898989898L)));
  }

  private ShipmentFileColumn filterColumns(List<ShipmentFileColumn> updatedColumns, final String columnName) {
    return (ShipmentFileColumn) select(updatedColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return (((ShipmentFileColumn) o).getName().equals(columnName));
      }
    }).toArray(new ShipmentFileColumn[1])[0];
  }

}
