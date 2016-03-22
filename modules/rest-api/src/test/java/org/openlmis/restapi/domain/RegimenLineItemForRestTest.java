package org.openlmis.restapi.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openlmis.rnr.builder.RegimenLineItemBuilder.defaultRegimenLineItem;
import static org.openlmis.rnr.builder.RegimenLineItemBuilder.patientsOnTreatment;
import static org.openlmis.rnr.builder.RegimenLineItemBuilder.patientsStoppedTreatment;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RegimenLineItemForRestTest {

    @Test
    public void shouldConvertFromRnrLineItem() throws Exception {
        RegimenLineItem regimenLineItem = make(a(defaultRegimenLineItem, with(patientsOnTreatment, 10), with(patientsStoppedTreatment, 5)));
        RegimenLineItemForRest regimenLineItemForRest = RegimenLineItemForRest.convertFromRegimenLineItem(regimenLineItem);

        assertThat(regimenLineItemForRest.getCode(), is(regimenLineItem.getCode()));
        assertThat(regimenLineItemForRest.getName(), is(regimenLineItem.getName()));
        assertThat(regimenLineItemForRest.getPatientsOnTreatment(), is(regimenLineItem.getPatientsOnTreatment()));
        assertThat(regimenLineItemForRest.getCategoryName(), is(regimenLineItem.getCategory().getName()));

    }
}
