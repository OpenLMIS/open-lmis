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

  private final static String versionCode = "86";
  @InjectMocks
  RestProductService restProductService;
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
  @Mock
  private StaticReferenceDataService staticReferenceDataService;

  @Before
  public void setUp() throws Exception {
    product.setCode("some kit code");
    product.setPrimaryName("primary name");
    ArrayList<KitProduct> kitProductList = new ArrayList<>();
    kitProductList.add(new KitProduct("kit code 1", "code 1", 100));
    kitProductList.add(new KitProduct("kit code 2", "code 2", 200));
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
  public void shouldSaveKit() {

    restProductService.buildAndSave(product);

    verify(productService).save(product, true);

  }

  @Test
  public void shouldGetAllLatestProgramsWithProductsByFacilityIdWhenAfterUpdatedTimeIsEmpty() {
    ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, makeProgram("PR1", "program 1"))));
    ProgramSupported programSupported2 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, makeProgram("PR2", "program 2"))));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported1, programSupported2));
    facility.setId(1L);

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());

    when(productService.getAllProducts()).thenReturn(asList(makeProduct("P1", "product 1", false), makeProduct("P2", "product 2", false)));
    when(programProductService.getActiveProgramCodesByProductCode("P1")).thenReturn(asList("PR1", "PR2"));
    when(programProductService.getActiveProgramCodesByProductCode("P2")).thenReturn(asList("PR1"));

    when(programProductService.getByProductCode("P1")).thenReturn(asList(makeProgramProduct("PR1", "P1", true), makeProgramProduct("PR2", "P1", true)));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(makeProgramProduct("PR1", "P1", true)));

    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());
    when(archivedProductService.getAllArchivedProducts(anyLong())).thenReturn(asList("P1"));

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(null, versionCode, user.getId());

    assertEquals(2, products.size());
    assertEquals("PR1", products.get(0).getProductPrograms().get(0).getProgramCode());
    assertEquals("PR2", products.get(0).getProductPrograms().get(1).getProgramCode());
    assertEquals("PR1", products.get(1).getProductPrograms().get(0).getProgramCode());
    assertTrue(products.get(0).getProduct().getArchived());
    assertFalse(products.get(1).getProduct().getArchived());
  }

  @Test
  public void shouldRetrieveProductsAfterUpdatedDate() {
    Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");

    Product product1 = makeProduct("P1", "product 1");
    Product product2 = makeProduct("P2", "product 2");
    Product product3 = makeProduct("P3", "product 3");
    ArrayList<Product> latestUpdatedProducts = new ArrayList<>();
    latestUpdatedProducts.add(product1);
    latestUpdatedProducts.add(product2);

    Program program1 = makeProgram("PR1", "program 1");
    Program program2 = makeProgram("PR2", "program 2");
    ProgramProduct programProduct4 = makeProgramProduct(program1, product3);

    ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, program1)));
    ProgramSupported programSupported2 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, program2)));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported1, programSupported2));

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());
    List<ProgramProduct> programProducts = new ArrayList<>();
    programProducts.add(programProduct4);

    when(productService.getProductsAfterUpdatedDate(afterUpdatedTime)).thenReturn(latestUpdatedProducts);
    when(programProductService.getByProductCode("P1")).thenReturn(asList(makeProgramProduct("PR1", "P1", true), makeProgramProduct("PR2", "P1", true)));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(makeProgramProduct("PR1", "P2", true)));
    when(programProductService.getByProductCode("P3")).thenReturn(asList(makeProgramProduct("PR1", "P3", true)));

    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());
    when(programProductService.getLatestUpdatedProgramProduct(afterUpdatedTime)).thenReturn(programProducts);

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, versionCode, user.getId());

    assertEquals(3, products.size());
    assertEquals("PR1", products.get(0).getProductPrograms().get(0).getProgramCode());
    assertEquals("PR2", products.get(0).getProductPrograms().get(1).getProgramCode());
    assertEquals("PR1", products.get(1).getProductPrograms().get(0).getProgramCode());
  }

  @Test
  public void shouldNotResponseProductTwiceWhenProductsUpdatedAndAssociateProgramProductUpdated() {
    Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");

    Product product = makeProduct("P1", "product 1");

    ArrayList<Product> latestUpdatedProducts = new ArrayList<>();
    latestUpdatedProducts.add(product);

    Program program1 = makeProgram("PR1", "program 1");
    ProgramProduct programProduct = makeProgramProduct(program1, product);

    ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, program1)));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported1));

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());
    List<ProgramProduct> programProducts = new ArrayList<>();
    programProducts.add(programProduct);

    when(productService.getProductsAfterUpdatedDate(afterUpdatedTime)).thenReturn(latestUpdatedProducts);
    when(programProductService.getByProductCode("P1")).thenReturn(asList(makeProgramProduct("PR1", "P1", true)));

    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());
    when(programProductService.getLatestUpdatedProgramProduct(afterUpdatedTime)).thenReturn(programProducts);

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, versionCode, user.getId());

    assertEquals(1, products.size());
  }

  @Test
  public void shouldNotGetProductsInProgramsNotSupportedByFacility() {
    Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");

    ProgramSupported programSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported,
            with(ProgramSupportedBuilder.supportedProgram, makeProgram("PR1", "program 1"))));

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facility.setSupportedPrograms(asList(programSupported));

    User user = make(a(UserBuilder.defaultUser));
    user.setFacilityId(facility.getId());

    when(productService.getProductsAfterUpdatedDate(afterUpdatedTime)).thenReturn(asList(makeProduct("P1", "product 1"), makeProduct("P2", "product 2"), makeProduct("P3", "product 3"), makeProduct("P4", "product 4")));
    when(programProductService.getByProductCode("P1")).thenReturn(asList(makeProgramProduct("PR1", "P1", true), makeProgramProduct("PR2", "P1", true)));
    when(programProductService.getByProductCode("P2")).thenReturn(asList(makeProgramProduct("PR1", "P2", true)));
    when(programProductService.getByProductCode("P3")).thenReturn(asList(makeProgramProduct("PR2", "P3", true)));

    when(userService.getById(user.getId())).thenReturn(user);
    when(programSupportedService.getAllByFacilityId(user.getFacilityId())).thenReturn(facility.getSupportedPrograms());

    List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, versionCode, user.getId());

    assertEquals(2, products.size());
    assertEquals("PR1", products.get(0).getProductPrograms().get(0).getProgramCode());
    assertEquals("PR1", products.get(1).getProductPrograms().get(0).getProgramCode());
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
    return makeProduct(code, name, false);
  }

  private Product makeProduct(String code, String name, boolean isArchived) {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.setCode(code);
    product.setPrimaryName(name);
    product.setArchived(isArchived);
    return product;
  }

  private ProgramProduct makeProgramProduct(String programCode, String productCode, boolean isActive) {
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setProduct(makeProduct(productCode, "default product name"));
    programProduct.setProgram(makeProgram(programCode, "default program name"));
    programProduct.setProductCategory(new ProductCategory("Code", "Other", 10));
    programProduct.setActive(isActive);
    return programProduct;
  }
}