package org.openlmis.rnr.domain;

import org.junit.Test;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.openlmis.core.builder.ProductBuilder.code;

public class RnrLineItemTest {

    @Test
    public void shouldConstructRnrLineItem() {

        Program program = make(a(ProgramBuilder.defaultProgram));
        Product product = make(a(ProductBuilder.defaultProduct, with(code, "ASPIRIN")));
        product.setDispensingUnit("Strip");

        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
        RnrLineItem rnrLineItem = new RnrLineItem(1, new FacilityApprovedProduct("warehouse", programProduct, 3), 1);
        assertEquals(3, rnrLineItem.getMaxMonthsOfStock().intValue());
        assertEquals(1, rnrLineItem.getRnrId().intValue());
        assertEquals("Strip", rnrLineItem.getDispensingUnit());
        assertEquals("ASPIRIN", rnrLineItem.getProductCode());
        assertEquals(30, rnrLineItem.getDosesPerMonth().intValue());
        assertEquals(1, rnrLineItem.getModifiedBy().intValue());
        assertEquals(10, rnrLineItem.getDosesPerDispensingUnit().intValue());

    }
}
