package org.openlmis.core.repository.mapper;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE_CODE;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class FacilityApprovedProductMapperIT {

    public static final Integer MAX_MONTHS_OF_STOCK = 3;

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProgramProductMapper programProductMapper;
    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    FacilityApprovedProductMapper facilityApprovedProductMapper;
    @Autowired
    private ProgramMapper programMapper;

    @Test
    public void shouldInsertFacilityApprovedProduct() throws Exception {
        Program program = make(a(ProgramBuilder.defaultProgram));
        Product product = make(a(ProductBuilder.defaultProduct));
        program.setId(programMapper.insert(program));
        productMapper.insert(product);

        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
        programProduct.setId(programProductMapper.insert(programProduct));

        FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct(FACILITY_TYPE_CODE, programProduct, MAX_MONTHS_OF_STOCK);
        int status = facilityApprovedProductMapper.insert(facilityApprovedProduct);

        assertThat(status, is(1));
    }

    @Test
    public void shouldGetFullSupplyAndActiveProductsByFacilityAndProgramInOrderOfDisplayAndProductCode() {
      Facility facility = make(a(FacilityBuilder.defaultFacility));
      facilityMapper.insert(facility);
        Program yellowFeverProgram = make(a(defaultProgram));
        Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

        programMapper.insert(bpProgram);
        programMapper.insert(yellowFeverProgram);

        Product pro01 = product("PRO01", true, 6);
        Product pro02 = product("PRO02", true, 4);
        Product pro03 = product("PRO03", false, 1);
        Product pro04 = product("PRO04", true, 2);
        Product pro05 = product("PRO05", true, 5);
        Product pro06 = product("PRO06", true, 5);
        Product pro07 = product("PRO07", true, null);

        ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true);
        ProgramProduct programProduct2 = addToProgramProduct(yellowFeverProgram, pro02, true);
        ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true);
        ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false);
        ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true);
        ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true);
        ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true);

        addToFacilityType("warehouse", programProduct1);
        addToFacilityType("warehouse", programProduct3);
        addToFacilityType("warehouse", programProduct4);
        addToFacilityType("warehouse", programProduct5);
        addToFacilityType("warehouse", programProduct6);
        addToFacilityType("warehouse", programProduct7);

        List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductMapper.getFullSupplyProductsByFacilityAndProgram(
            facility.getId(), yellowFeverProgram.getCode());
        assertEquals(3, facilityApprovedProducts.size());

        FacilityApprovedProduct facilityApprovedProduct = facilityApprovedProducts.get(0);

        assertEquals(programProduct5.getId(), facilityApprovedProduct.getProgramProduct().getId());
        assertEquals(30, facilityApprovedProduct.getProgramProduct().getDosesPerMonth().intValue());
        Product product = facilityApprovedProduct.getProgramProduct().getProduct();
        assertEquals("PRO05", product.getCode());
        assertEquals("Primary Name", product.getPrimaryName());
        assertEquals("strength", product.getStrength());
        assertThat(product.getForm().getCode(), Is.is("Tablet"));
        assertEquals("Strip", product.getDispensingUnit());
        assertThat(product.getDosageUnit().getCode(), Is.is("mg"));
        assertNotNull(product.getForm());
        assertEquals("Tablet", product.getForm().getCode());
        assertNotNull(product.getDosageUnit());
        assertEquals("mg", product.getDosageUnit().getCode());
        assertEquals(10, product.getDosesPerDispensingUnit().intValue());

        assertEquals("PRO06", facilityApprovedProducts.get(1).getProgramProduct().getProduct().getCode());
        assertEquals("PRO01", facilityApprovedProducts.get(2).getProgramProduct().getProduct().getCode());
    }


    private void addToFacilityType(String facilityType, ProgramProduct programProduct) {
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(facilityType, programProduct, MAX_MONTHS_OF_STOCK));
    }

    private Product product(String productCode, boolean isFullSupply, Integer order) {
        Product product = make(a(ProductBuilder.defaultProduct, with(code, productCode), with(fullSupply, isFullSupply), with(displayOrder, order)));
        productMapper.insert(product);
        return product;
    }

    private ProgramProduct addToProgramProduct(Program program, Product product, boolean isActive) {
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, isActive);
        programProduct.setId(programProductMapper.insert(programProduct));
        return programProduct;
    }



}
