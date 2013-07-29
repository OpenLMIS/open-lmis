/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
