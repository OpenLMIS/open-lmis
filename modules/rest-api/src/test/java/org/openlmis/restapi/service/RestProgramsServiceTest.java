package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ProgramWithProductsBuilder;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramsService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProgramsServiceTest {

    @InjectMocks
    private RestProgramsService restProgramsService;

    @Mock
    private ProgramService programService;

    @Mock
    private FacilityService facilityService;

    @Mock
    private ProgramProductService programProductService;

    @Test
    public void shouldGetAllProgramsWithProductsByFacilityCode() {

        // get facility by code
        Facility facility = new Facility(123L);
        when(facilityService.getFacilityByCode("F1")).thenReturn(facility);

        // set up programs associated with this facility
        List<Program> programsInFacility = new ArrayList();
        Program program1 = makeProgram("PR1", "Program 1");
        Program program2 = makeProgram("PR2", "Program 2");
        programsInFacility.add(program1);
        programsInFacility.add(program2);
        when(programService.getByFacility(123L)).thenReturn(programsInFacility);

        // set up products associated with the programs
        Product product1 = makeProduct("PD1", "Product 1");
        Product product2 = makeProduct("PD2", "Product 2");
        Product product3 = makeProduct("PD3", "Product 3");
        List<ProgramProduct> programProducts1 = new ArrayList();
        programProducts1.add(makeProgramProduct(program1, product1));
        programProducts1.add(makeProgramProduct(program1, product2));
        List<ProgramProduct> programProducts2 = new ArrayList();
        programProducts2.add(makeProgramProduct(program2, product3));
        when(programProductService.getByProgram(program1)).thenReturn(programProducts1);
        when(programProductService.getByProgram(program2)).thenReturn(programProducts2);

        // set up expected objects
        ProgramWithProducts programWithProducts1 = new ProgramWithProductsBuilder().withProgramCode("PR1").withProgramName("Program 1")
                .addProduct(product1).addProduct(product2).build();
        ProgramWithProducts programWithProducts2 = new ProgramWithProductsBuilder().withProgramCode("PR2").withProgramName("Program 2")
                .addProduct(product3).build();

        // test
        List<ProgramWithProducts> programsWithProducts = restProgramsService.getAllProgramsWithProductsByFacilityCode("F1");
        assertEquals(2, programsWithProducts.size());
        assertEquals(programWithProducts1, programsWithProducts.get(0));
        assertEquals(programWithProducts2, programsWithProducts.get(1));

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
