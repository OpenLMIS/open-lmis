/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
