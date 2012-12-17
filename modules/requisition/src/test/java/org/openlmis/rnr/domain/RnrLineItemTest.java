package org.openlmis.rnr.domain;

import org.junit.Test;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
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
        RnrLineItem rnrLineItem = new RnrLineItem(1, new ProgramProduct(program, product, 30, true), "foo");
        assertEquals(1, rnrLineItem.getRnrId().intValue());
        assertEquals("Strip", rnrLineItem.getUnitOfIssue());
        assertEquals("ASPIRIN", rnrLineItem.getProductCode());
        assertEquals(30, rnrLineItem.getDosesPerMonth().intValue());
        assertEquals("foo", rnrLineItem.getModifiedBy());
        assertEquals(10, rnrLineItem.getDosesPerDispensingUnit().intValue());
    }
}
