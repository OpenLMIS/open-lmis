package org.openlmis.core.repository.mapper;


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
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
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
        int facilityId = facilityMapper.insert(make(a(FacilityBuilder.defaultFacility)));

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


        List<Product> products = productMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, HIV);
        assertEquals(4, products.size());
        assertEquals("PRO05", products.get(0).getCode());
        assertEquals("Primary Name", products.get(0).getPrimaryName());
        assertEquals("strength", products.get(0).getStrength());
        assertThat(products.get(0).getFormCode(), is("Tablet"));
        assertEquals("Strip", products.get(0).getDispensingUnit());
        assertThat(products.get(0).getDosageUnitCode(), is("mg"));
        assertNotNull(products.get(0).getProductForm());
        assertEquals("Tablet", products.get(0).getProductForm().getCode());
        assertNotNull(products.get(0).getProductDosageUnit());
        assertEquals("mg", products.get(0).getProductDosageUnit().getCode());

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
