/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RegimenColumnBuilder;

import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RegimenColumnBuilder.defaultRegimenColumn;
import static org.openlmis.rnr.builder.RegimenLineItemBuilder.*;

@Category(UnitTests.class)
public class RegimenLineItemTest {

  @Test
  public void shouldCopyCreatorEditableField() throws Exception {

    final RegimenLineItem savedRegimenLineItem = make(a(defaultRegimenLineItem));
    final RegimenLineItem regimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10),
      with(patientsStoppedTreatment, 10), with(patientsToInitiateTreatment, 20)));
    final RegimenColumn patientsOnTreatment = make(a(defaultRegimenColumn, with(RegimenColumnBuilder.name, "patientsOnTreatment")));
    final RegimenColumn patientsStoppedTreatment = make(a(defaultRegimenColumn, with(RegimenColumnBuilder.name, "patientsStoppedTreatment")));
    RegimenColumn patientsToInitiateTreatment = make(a(defaultRegimenColumn, with(RegimenColumnBuilder.name, "patientsToInitiateTreatment")));
    RegimenColumn remarks = make(a(defaultRegimenColumn, with(RegimenColumnBuilder.name, "remarks")));
    List<RegimenColumn> regimenColumns = Arrays.asList(patientsOnTreatment, patientsStoppedTreatment, patientsToInitiateTreatment, remarks);

    savedRegimenLineItem.copyCreatorEditableFieldsForRegimen(regimenLineItem, new RegimenTemplate(1L, regimenColumns));

    assertThat(savedRegimenLineItem.getCode(), is(regimenLineItem.getCode()));
    assertThat(savedRegimenLineItem.getPatientsOnTreatment(), is(regimenLineItem.getPatientsOnTreatment()));
    assertThat(savedRegimenLineItem.getPatientsOnTreatment(), is(regimenLineItem.getPatientsStoppedTreatment()));
    assertThat(savedRegimenLineItem.getPatientsToInitiateTreatment(), is(regimenLineItem.getPatientsToInitiateTreatment()));
    assertThat(savedRegimenLineItem.getRemarks(), is(regimenLineItem.getRemarks()));

  }
}
