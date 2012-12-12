package org.openlmis.core.repository.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.PRODUCT_CODE;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramProductMapperIT {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProgramProductMapper programProductMapper;

    @Autowired
    ProgramMapper programMapper;

    @Test
    public void shouldInsertProductForAProgram() throws Exception {
        productMapper.insert(make(a(ProductBuilder.product, with(displayOrder, 1))));
        programMapper.insert(make(a(ProgramBuilder.defaultProgram)));
        ProgramProduct programProduct = new ProgramProduct(PROGRAM_CODE, PRODUCT_CODE, 10);
        int status = programProductMapper.insert(programProduct);
        assertThat(status, is(1));
    }
}
