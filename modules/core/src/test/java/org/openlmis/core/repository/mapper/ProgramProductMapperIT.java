package org.openlmis.core.repository.mapper;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramProductMapperIT {

    public static final String HIV = "HIV";
    public static final int MAX_MONTHS_OF_STOCK = 3;

    @Autowired
    ProgramMapper programMapper;
    @Autowired
    FacilityMapper facilityMapper;
    @Autowired
    ProgramSupportedMapper programSupportedMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    FacilityApprovedProductMapper facilityApprovedProductMapper;
    @Autowired
    ProgramProductMapper programProductMapper;

    @Test
    public void shouldInsertProductForAProgram() throws Exception {
        Product product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
        productMapper.insert(product);
        Program program = make(a(defaultProgram));
        programMapper.insert(program);
        ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
        Integer id = programProductMapper.insert(programProduct);
        assertNotNull(id);
    }

    @Test
    public void shouldGetFullSupplyAndActiveProductsByFacilityAndProgramInOrderOfDisplayAndProductCode() {
        Integer facilityId = facilityMapper.insert(make(a(FacilityBuilder.defaultFacility)));
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

        addToProgram(yellowFeverProgram, pro01, true);
        addToProgram(yellowFeverProgram, pro02, true);
        addToProgram(yellowFeverProgram, pro03, true);
        addToProgram(yellowFeverProgram, pro04, false);
        addToProgram(yellowFeverProgram, pro05, true);
        addToProgram(yellowFeverProgram, pro06, true);
        addToProgram(bpProgram, pro07, true);

        addToFacilityType("warehouse", pro01);
        addToFacilityType("warehouse", pro03);
        addToFacilityType("warehouse", pro04);
        addToFacilityType("warehouse", pro05);
        addToFacilityType("warehouse", pro06);
        addToFacilityType("warehouse", pro07);

        List<ProgramProduct> programProducts = programProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, yellowFeverProgram.getCode());
        assertEquals(3, programProducts.size());

        ProgramProduct programProduct = programProducts.get(0);

        assertEquals(yellowFeverProgram.getCode(), programProduct.getProgram().getCode());
        assertEquals(30, programProduct.getDosesPerMonth().intValue());
        Product product = programProduct.getProduct();
        assertEquals("PRO05", product.getCode());
        assertEquals("Primary Name", product.getPrimaryName());
        assertEquals("strength", product.getStrength());
        Assert.assertThat(product.getForm().getCode(), Is.is("Tablet"));
        assertEquals("Strip", product.getDispensingUnit());
        Assert.assertThat(product.getDosageUnit().getCode(), Is.is("mg"));
        assertNotNull(product.getForm());
        assertEquals("Tablet", product.getForm().getCode());
        assertNotNull(product.getDosageUnit());
        assertEquals("mg", product.getDosageUnit().getCode());
        assertEquals(10, product.getDosesPerDispensingUnit().intValue());

        assertEquals("PRO06", programProducts.get(1).getProduct().getCode());
        assertEquals("PRO01", programProducts.get(2).getProduct().getCode());
    }

    private void addToFacilityType(String facilityType, Product product) {
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(facilityType, product.getCode(), MAX_MONTHS_OF_STOCK));
    }

    private Product product(String productCode, boolean isFullSupply, Integer order) {
        Product product = make(a(ProductBuilder.defaultProduct, with(code, productCode), with(fullSupply, isFullSupply), with(displayOrder, order)));
        productMapper.insert(product);
        return product;
    }

    private void addToProgram(Program program, Product product, boolean isActive) {
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, isActive);
        programProductMapper.insert(programProduct);

    }
}
