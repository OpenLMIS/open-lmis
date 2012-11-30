package org.openlmis.core.repository.mapper;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.ProductBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProductMapperIT {

    public static final String HIV = "HIV";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    ProgramProductMapper programProductMapper;

    @Autowired
    FacilityApprovedProductMapper facilityApprovedProductMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;
    @Autowired
    ProductMapper productMapper;

    @Before
    @After
    public void clean() {
        facilityApprovedProductMapper.deleteAll();
        programProductMapper.deleteAll();
        programSupportedMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.deleteAll();
    }

    @Test
    public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
        expectedEx.expect(DataIntegrityViolationException.class);
        expectedEx.expectMessage("null value in column \"primary_name\" violates not-null constraint");
        Product product = new Product();
        product.setCode("ABCD123");
        int status = productMapper.insert(product);
        assertEquals(0, status);
    }

    @Test
    public void shouldGetFullSupplyAndActiveProductsByFacilityAndProgramInOrderOfDisplayAndProductCode() {
        facilityMapper.insert(make(a(FacilityBuilder.defaultFacility)));

        Product pro01 = product(HIV, "PRO01", true,true,6);
        addToProgram("ARV", pro01,true);
        addToFacilityType("warehouse", pro01);

        product(HIV, "PRO02", true, true, 4);

        Product pro03 = product(HIV, "PRO03", false, true,1);
        addToFacilityType("warehouse", pro03);

        Product pro04 = product(HIV, "PRO04", true, false,2);
        addToFacilityType("warehouse", pro04);


        Product pro06 = product(HIV, "PRO06", true, true, 5);
        addToFacilityType("warehouse", pro06);

        Product pro07 = product(HIV, "PRO07", true, true, null);
        addToFacilityType("warehouse", pro07);

        Product pro05 = product(HIV, "PRO05", true, true, 5);
        addToFacilityType("warehouse", pro05);


        List<Product> products = productMapper.getFullSupplyProductsByFacilityAndProgram(FACILITY_CODE, HIV);
        assertEquals(4, products.size());
        assertEquals("PRO05", products.get(0).getCode());
        assertEquals("Primary Name", products.get(0).getPrimaryName());
        assertEquals("strength", products.get(0).getStrength());
        assertEquals(1, products.get(0).getForm());
        assertEquals("Strip", products.get(0).getDispensingUnit());
        assertEquals(1, products.get(0).getDosageUnit());
        assertNotNull(products.get(0).getProductForm());
        assertEquals("Tablet", products.get(0).getProductForm().getName());
        assertNotNull(products.get(0).getProductDosageUnit());
        assertEquals("mg", products.get(0).getProductDosageUnit().getName());

        assertEquals("PRO06", products.get(1).getCode());

        assertEquals("PRO01", products.get(2).getCode());

        assertEquals("PRO07", products.get(3).getCode());
    }

    private void addToFacilityType(String facilityType, Product product) {
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(facilityType, product.getCode()));
    }

    private Product product(String programCode, String productCode, boolean isFullSupply, boolean isActive, Integer order) {
        Product product = make(a(ProductBuilder.product, with(code, productCode), with(fullSupply, isFullSupply),with(displayOrder,order)));
        productMapper.insert(product);
        addToProgram(programCode, product, isActive);
        return product;
    }

    private void addToProgram(String programCode, Product product, boolean isActive) {
        ProgramProduct programProduct = new ProgramProduct(programCode, product.getCode());
        programProduct.setActive(isActive);
        programProductMapper.insert(programProduct);

    }

}
