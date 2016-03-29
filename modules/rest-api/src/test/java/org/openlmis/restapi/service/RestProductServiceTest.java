package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.*;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.ProductResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProductServiceTest {

  private Product product = new Product();

  @Mock
  private ProductService productService;

  @Mock
  private UserService userService;

  @Mock
  private ProgramProductService programProductService;

  @Mock
  private ArchivedProductService archivedProductService;

  @Mock
  private ProgramSupportedService programSupportedService;

  @InjectMocks
  RestProductService restProductService;

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

    verify(productService).save(product);

  }

  @Test
  public void shouldGetAllLatestProgramsWithProductsByFacilityIdWhenAfterUpdatedTimeIsEmpty() {
    Product product1 = makeProduct("P1", "product 1");
    product1.setArchived(false);
    Product product2 = makeProduct("P2", "product 2");
    product2.setArchived(false);

    Program program1 = makeProgram("PR1", "program 1");
    Program program2 = makeProgram("PR2", "program 2");
    ProgramProduct programProduct1 = makeProgramProduct(program1, product1);
    ProgramProduct programProduct2 = makeProgramProduct(program2, product1);
    ProgramProduct programProduct3 = makeProgramProduct(program1, product2);

    ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
        with(ProgramSupportedBuilder.supportedProgram, program1)));
    ProgramSupported programSupported2 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
        with(ProgramSupportedBuilder.supportedProgram, program2)));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported1, programSupported2));
    facility.setId(1L);

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());

    when(productService.getAllProducts()).thenReturn(asList(product1, product2));
    when(programProductService.getByProductCode("P1")).thenReturn(asList(programProduct1, programProduct2));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(programProduct3));
    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());
    when(archivedProductService.getAllArchivedProducts(anyLong())).thenReturn(asList("P1"));

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(null, user.getId());

    assertEquals(2, products.size());
    assertEquals("PR1", products.get(0).getSupportedPrograms().get(0));
    assertEquals("PR2", products.get(0).getSupportedPrograms().get(1));
    assertEquals("PR1", products.get(1).getSupportedPrograms().get(0));
    assertTrue(products.get(0).getProduct().getArchived());
    assertFalse(products.get(1).getProduct().getArchived());
  }

  @Test
  public void shouldRetrieveProductsAfterUpdatedDate() {
    Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");

    Product product1 = makeProduct("P1", "product 1");
    Product product2 = makeProduct("P2", "product 2");

    Program program1 = makeProgram("PR1", "program 1");
    Program program2 = makeProgram("PR2", "program 2");
    ProgramProduct programProduct1 = makeProgramProduct(program1, product1);
    ProgramProduct programProduct2 = makeProgramProduct(program2, product1);
    ProgramProduct programProduct3 = makeProgramProduct(program1, product2);

    ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
        with(ProgramSupportedBuilder.supportedProgram, program1)));
    ProgramSupported programSupported2 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
        with(ProgramSupportedBuilder.supportedProgram, program2)));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported1, programSupported2));

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());

    when(productService.getProductsAfterUpdatedDate(afterUpdatedTime)).thenReturn(asList(product1, product2));
    when(programProductService.getByProductCode("P1")).thenReturn(asList(programProduct1, programProduct2));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(programProduct3));
    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, user.getId());

    assertEquals(2, products.size());
    assertEquals("PR1", products.get(0).getSupportedPrograms().get(0));
    assertEquals("PR2", products.get(0).getSupportedPrograms().get(1));
    assertEquals("PR1", products.get(1).getSupportedPrograms().get(0));

  }

  @Test
  public void shouldNotGetProductsInProgramsNotSupportedByFacility() {
    Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");

    Product product1 = makeProduct("P1", "product 1");
    Product product2 = makeProduct("P2", "product 2");
    Product product3 = makeProduct("P3", "product 3");

    Program program1 = makeProgram("PR1", "program 1");
    Program program2 = makeProgram("PR2", "program 2");
    ProgramProduct programProduct1 = makeProgramProduct(program1, product1);
    ProgramProduct programProduct2 = makeProgramProduct(program2, product1);
    ProgramProduct programProduct3 = makeProgramProduct(program1, product2);
    ProgramProduct programProduct4 = makeProgramProduct(program2, product3);

    ProgramSupported programSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported,
        with(ProgramSupportedBuilder.supportedProgram, program1)));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported));

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());

    when(productService.getProductsAfterUpdatedDate(afterUpdatedTime)).thenReturn(asList(product1, product2, product3));
    when(programProductService.getByProductCode("P1")).thenReturn(asList(programProduct1, programProduct2));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(programProduct3));
    when(programProductService.getByProductCode("P3")).thenReturn(asList(programProduct4));
    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, user.getId());

    assertEquals(2, products.size());
    assertEquals("PR1", products.get(0).getSupportedPrograms().get(0));
    assertEquals("PR1", products.get(1).getSupportedPrograms().get(0));
  }

  private ProgramProduct makeProgramProduct(Program program, Product product) {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProduct.setProgram(program);
    programProduct.setProduct(product);
    return programProduct;
  }

  private Program makeProgram(String code, String name) {
    Program program = make(a(ProgramBuilder.defaultProgram));
    program.setCode(code);
    program.setName(name);
    return program;
  }

  private Product makeProduct(String code, String name) {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.setCode(code);
    product.setPrimaryName(name);
    return product;
  }
}