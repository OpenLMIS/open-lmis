package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProductServiceTest {

  @InjectMocks
  RestProductService restProductService;

  @Mock
  ProductRepository productRepository;

  private Product product = new Product();

  @Before
  public void setUp() throws Exception {
    product.setCode("some kit code");
    product.setPrimaryName("primary name");
    ArrayList<KitProduct> kitProductList = new ArrayList<>();
    KitProduct kitProduct1 = new KitProduct();
    kitProduct1.setProductCode("code 1");
    kitProduct1.setQuantity(100);
    kitProductList.add(kitProduct1);
    KitProduct kitProduct2 = new KitProduct();
    kitProduct2.setProductCode("code 2");
    kitProduct2.setQuantity(200);
    kitProductList.add(kitProduct2);
    product.setKitProductList(kitProductList);

  }

  @Test
  public void shouldPopulateKitCodeForKitProductsInList() {


    Product convertedProduct = restProductService.buildAndSave(product);

    assertThat(convertedProduct.getDispensingUnit(), is("1"));
    assertThat(convertedProduct.getPackSize(), is(1));
    assertThat(convertedProduct.getDosesPerDispensingUnit(), is(1));
    assertThat(convertedProduct.getActive(), is(true));
    assertThat(convertedProduct.getFullSupply(), is(true));
    assertThat(convertedProduct.getTracer(), is(false));
    assertThat(convertedProduct.getRoundToZero(), is(false));
    assertThat(convertedProduct.getPackRoundingThreshold(), is(0));
    assertThat(convertedProduct.getKitProductList().size(), is(2));
    assertThat(convertedProduct.getKitProductList().get(0).getKitCode(), is("some kit code"));
    assertThat(convertedProduct.getKitProductList().get(0).getProductCode(), is("code 1"));
    assertThat(convertedProduct.getKitProductList().get(0).getQuantity(), is(100));
    assertThat(convertedProduct.getKitProductList().get(1).getKitCode(), is("some kit code"));
    assertThat(convertedProduct.getKitProductList().get(1).getProductCode(), is("code 2"));
    assertThat(convertedProduct.getKitProductList().get(1).getQuantity(), is(200));
  }

  @Test
  public void shouldSaveKit(){

    restProductService.buildAndSave(product);

    verify(productRepository).insert(product);

  }
}