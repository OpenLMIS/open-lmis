package org.openlmis.core.repository.mapper;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
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
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramProductMapperIT {

    public static final String HIV = "HIV";

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
        productMapper.insert(make(a(ProductBuilder.product, with(displayOrder, 1))));
        programMapper.insert(make(a(defaultProgram)));
        ProgramProduct programProduct = new ProgramProduct(PROGRAM_CODE, PRODUCT_CODE, 10);
        Integer id = programProductMapper.insert(programProduct);
        assertNotNull(id);
    }

    @Test
    public void shouldGetFullSupplyAndActiveProductsByFacilityAndProgramInOrderOfDisplayAndProductCode() {
        Integer facilityId = facilityMapper.insert(make(a(FacilityBuilder.defaultFacility)));

        Product pro01 = product(HIV, "PRO01", true, true, 6);
        addToProgram("ARV", pro01, true);
        addToFacilityType("warehouse", pro01);

        product(HIV, "PRO02", true, true, 4);

        Product pro03 = product(HIV, "PRO03", false, true, 1);
        addToFacilityType("warehouse", pro03);

        Product pro04 = product(HIV, "PRO04", true, false, 2);
        addToFacilityType("warehouse", pro04);

        Product pro06 = product(HIV, "PRO06", true, true, 5);
        addToFacilityType("warehouse", pro06);

        Product pro07 = product(HIV, "PRO07", true, true, null);
        addToFacilityType("warehouse", pro07);

        Product pro05 = product(HIV, "PRO05", true, true, 5);
        addToFacilityType("warehouse", pro05);

        List<ProgramProduct> programProducts = programProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, HIV);
        assertEquals(4, programProducts.size());

        ProgramProduct programProduct = programProducts.get(0);
        assertEquals(HIV, programProduct.getProgramCode());
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

        assertEquals("PRO06", programProducts.get(1).getProduct().getCode());
        assertEquals("PRO01", programProducts.get(2).getProduct().getCode());
        assertEquals("PRO07", programProducts.get(3).getProduct().getCode());
    }

    private void addToFacilityType(String facilityType, Product product) {
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(facilityType, product.getCode()));
    }

    private Product product(String programCode, String productCode, boolean isFullSupply, boolean isActive, Integer order) {
        Product product = make(a(ProductBuilder.product, with(code, productCode), with(fullSupply, isFullSupply), with(displayOrder, order)));
        productMapper.insert(product);
        addToProgram(programCode, product, isActive);
        return product;
    }

    private void addToProgram(String programCode, Product product, boolean isActive) {
        ProgramProduct programProduct = new ProgramProduct(programCode, product.getCode(), 10);
        programProduct.setActive(isActive);
        programProductMapper.insert(programProduct);

    }
}
