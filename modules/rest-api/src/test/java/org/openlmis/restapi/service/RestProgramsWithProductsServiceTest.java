package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.builder.KitProductBuilder;
import org.openlmis.restapi.builder.ProgramWithProductsBuilder;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProgramsWithProductsServiceTest {

    @InjectMocks
    private RestProgramsWithProductsService restProgramsWithProductsService;

    @Mock
    private ProgramService programService;

    @Mock
    private FacilityService facilityService;

    @Mock
    private ProductService productService;

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private FacilityType facilityType;
    private Long facilityId;


    @Before
    public void setUp() throws Exception {
        // get facility by code
        facilityId = 123L;
        facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, facilityId)));
        facilityType = facility.getFacilityType();
        when(facilityService.getFacilityByCode("F1")).thenReturn(facility);

        // set up programs associated with this facility
        programsInFacility = new ArrayList();
        program1 = makeProgram("PR1", "Program 1");
        program2 = makeProgram("PR2", "Program 2");
        programsInFacility.add(program1);
        programsInFacility.add(program2);
        when(programService.getByFacility(facilityId)).thenReturn(programsInFacility);

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
        when(programService.getByFacility(facilityId)).thenReturn(new ArrayList<Program>());
        when(facilityService.getById(facilityId)).thenReturn(facility);

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
        when(facilityService.getById(facilityId)).thenReturn(facility);

        // test with null AfterUpdatedTime
        List<ProgramWithProducts> latestProgramWithProducts = restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, null);
        assertEquals(2, latestProgramWithProducts.size());
        assertEquals(programWithProducts1, latestProgramWithProducts.get(0));
        assertEquals(programWithProducts2, latestProgramWithProducts.get(1));
    }

    @Test
    public void shouldRetrieveProductsAfterUpdatedDate() {
        Date afterUpdatedTime = DateUtil.parseDate("2015-11-11 10:10:10");
        when(programProductService.getProductsByProgramAfterUpdatedDateByFacilityType(program1, afterUpdatedTime, facilityType)).thenReturn(programProducts1);
        when(programProductService.getProductsByProgramAfterUpdatedDateByFacilityType(program2, afterUpdatedTime, facilityType)).thenReturn(new ArrayList());
        when(facilityService.getById(facilityId)).thenReturn(facility);

        // set up expected objects
        ProgramWithProducts programWithProducts1 = new ProgramWithProductsBuilder().withProgramCode("PR1").withProgramName("Program 1")
                .addProduct(product1).addProduct(product2).build();

        // test with AfterUpdatedTime
        List<ProgramWithProducts> latestProgramWithProducts = restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, afterUpdatedTime);
        assertEquals(2, latestProgramWithProducts.size());
        assertEquals(programWithProducts1, latestProgramWithProducts.get(0));
        assertEquals(0, latestProgramWithProducts.get(1).getProducts().size());
    }

    @Test
    public void shouldThrowFacilityInvalidErrorIfFacilityIdDoesNotExist() {
        expectedException.expect(DataException.class);
        expectedException.expectMessage("error.facility.unknown");

        when(facilityService.getById(facilityId)).thenReturn(null);

        restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, null);
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
