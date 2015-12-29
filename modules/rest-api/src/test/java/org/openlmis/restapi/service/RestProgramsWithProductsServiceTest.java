package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ProgramWithProductsBuilder;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProgramsWithProductsServiceTest {

    @InjectMocks
    private RestProgramsWithProductsService restProgramsWithProductsService;

    @Mock
    private ProgramService programService;

    @Mock
    private FacilityService facilityService;

    @Mock
    private ProgramProductService programProductService;
    private Facility facility;
    private Program program1;
    private Program program2;
    private List<Program> programsInFacility;
    private Product product1;
    private Product product2;
    private Product product3;
    private List<ProgramProduct> programProducts1;
    private List<ProgramProduct> programProducts2;

    @Before
    public void setUp() throws Exception {
        // get facility by code
        facility = new Facility(123L);
        when(facilityService.getFacilityByCode("F1")).thenReturn(facility);

        // set up programs associated with this facility
        programsInFacility = new ArrayList();
        program1 = makeProgram("PR1", "Program 1");
        program2 = makeProgram("PR2", "Program 2");
        programsInFacility.add(program1);
        programsInFacility.add(program2);
        when(programService.getByFacility(facility.getId())).thenReturn(programsInFacility);

        // set up products associated with the programs
        product1 = makeProduct("PD1", "Product 1");
        product2 = makeProduct("PD2", "Product 2");
        product3 = makeProduct("PD3", "Product 3");
        programProducts1 = new ArrayList();
        programProducts1.add(makeProgramProduct(program1, product1));
        programProducts1.add(makeProgramProduct(program1, product2));
        programProducts2 = new ArrayList();
        programProducts2.add(makeProgramProduct(program2, product3));
        when(programProductService.getByProgram(program1)).thenReturn(programProducts1);
        when(programProductService.getByProgram(program2)).thenReturn(programProducts2);
    }

    @Test
    public void shouldGetAllProgramsWithProductsByFacilityCode() {
        // set up expected objects
        ProgramWithProducts programWithProducts1 = new ProgramWithProductsBuilder().withProgramCode("PR1").withProgramName("Program 1")
                .addProduct(product1).addProduct(product2).build();
        ProgramWithProducts programWithProducts2 = new ProgramWithProductsBuilder().withProgramCode("PR2").withProgramName("Program 2")
                .addProduct(product3).build();

        // test
        List<ProgramWithProducts> programsWithProducts = restProgramsWithProductsService.getAllProgramsWithProductsByFacilityCode("F1");
        assertEquals(2, programsWithProducts.size());
        assertEquals(programWithProducts1, programsWithProducts.get(0));
        assertEquals(programWithProducts2, programsWithProducts.get(1));

    }

    @Test
    public void shouldReturnEmptyListIfNoProgramAssociatedWithFacility() {
        long facilityId = 123L;
        when(programService.getByFacility(facilityId)).thenReturn(new ArrayList<Program>());

        List<ProgramWithProducts> latestProgramsWithProducts = restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, null);
        assertEquals(0, latestProgramsWithProducts.size());
    }

    @Test
    public void shouldGetAllLatestProgramsWithProductsByFacilityIdWhenAfterUpdatedTimeIsEmpty() {
        // set up expected objects
        ProgramWithProducts programWithProducts1 = new ProgramWithProductsBuilder().withProgramCode("PR1").withProgramName("Program 1")
                .addProduct(product1).addProduct(product2).build();
        ProgramWithProducts programWithProducts2 = new ProgramWithProductsBuilder().withProgramCode("PR2").withProgramName("Program 2")
                .addProduct(product3).build();

        // test with null AfterUpdatedTime
        List<ProgramWithProducts> latestProgramWithProducts = restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facility.getId(), null);
        assertEquals(2, latestProgramWithProducts.size());
        assertEquals(programWithProducts1, latestProgramWithProducts.get(0));
        assertEquals(programWithProducts2, latestProgramWithProducts.get(1));
    }

    @Test
    public void shouldRetrieveProductsAfterUpdatedDate() {
        Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");
        when(programProductService.getProductsByProgramAfterUpdatedDate(program1, afterUpdatedTime)).thenReturn(programProducts1);
        when(programProductService.getProductsByProgramAfterUpdatedDate(program2, afterUpdatedTime)).thenReturn(new ArrayList());

        // set up expected objects
        ProgramWithProducts programWithProducts1 = new ProgramWithProductsBuilder().withProgramCode("PR1").withProgramName("Program 1")
                .addProduct(product1).addProduct(product2).build();

        // test with AfterUpdatedTime
        List<ProgramWithProducts> latestProgramWithProducts = restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facility.getId(), afterUpdatedTime);
        assertEquals(2, latestProgramWithProducts.size());
        assertEquals(programWithProducts1, latestProgramWithProducts.get(0));
        assertEquals(0, latestProgramWithProducts.get(1).getProducts().size());
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
