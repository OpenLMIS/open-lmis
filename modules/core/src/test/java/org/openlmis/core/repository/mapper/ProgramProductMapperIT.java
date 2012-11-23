package org.openlmis.core.repository.mapper;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.allOf;
import static org.openlmis.core.builder.ProductBuilder.PRODUCT_CODE;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramProductMapperIT {

   @Autowired
   ProductMapper productMapper;

    @Autowired
    ProgramProductMapper programProductMapper;

    @Autowired
    ProgramMapper programMapper;

    @After
    @Before
    public void setUp() throws Exception {
        programProductMapper.deleteAll();
        productMapper.deleteAll();
        programMapper.delete(PROGRAM_CODE);

    }

    @Test
    public void shouldInsertProductForAProgram() throws Exception {
        productMapper.insert(make(a(ProductBuilder.product,with(displayOrder,1))));
        programMapper.insert(make(a(ProgramBuilder.program)));
        ProgramProduct programProduct = new ProgramProduct(PROGRAM_CODE, PRODUCT_CODE);
        programProduct.setActive(true);
        int status = programProductMapper.insert(programProduct);
        assertThat(status, is(1));
    }
}
