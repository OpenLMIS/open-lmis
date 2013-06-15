package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@Category(IntegrationTests.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class IsaMapperIT {


  private Product product;
  private Program program;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  private IsaMapper mapper;

  @Before
  public void setUp() throws Exception {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void testUpdate() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(programProduct.getId(), 0.039f, 4, 10f, 25f, 50, 17, 1);
    mapper.insert(programProductISA);

    programProductISA.setCalculatedIsa(23);
    mapper.update(programProductISA);

    ProgramProductISA returnedIsa = mapper.getIsa(programProduct.getId());

    assertThat(returnedIsa, is(programProductISA));
  }

  @Test
  public void shouldInsertISAForAProgramProduct() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(programProduct.getId(), 0.039f, 4, 10f, 25f, 50, 17, 1);
    mapper.insert(programProductISA);

    ProgramProductISA returnedIsa = mapper.getIsa(programProduct.getId());

    assertThat(returnedIsa, is(programProductISA));
  }
}
