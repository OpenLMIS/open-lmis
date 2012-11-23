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
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.fullSupply;

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
    public void shouldGetFullSupplyAndActiveProductsByFacilityAndProgram() {
        facilityMapper.insert(make(a(FacilityBuilder.facility)));

        Product pro01 = product(HIV, "PRO01", true);
        addToProgram("ARV", pro01);
        addToFacilityType("warehouse", pro01);

        product(HIV, "PRO02", true);

        Product pro03 = product(HIV, "PRO03", false);
        addToFacilityType("warehouse", pro03);

        List<Product> products = productMapper.getFullSupplyProductsByFacilityAndProgram(FACILITY_CODE, HIV);
        assertEquals(1, products.size());
        assertEquals("PRO01", products.get(0).getCode());
    }

    private void addToFacilityType(String facilityType, Product product) {
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(facilityType, product.getCode()));
    }

    private Product product(String programCode, String productCode, boolean isFullSupply) {
        Product product = make(a(ProductBuilder.product, with(code, productCode), with(fullSupply, isFullSupply)));
        productMapper.insert(product);
        addToProgram(programCode, product);
        return product;
    }

    private void addToProgram(String programCode, Product product) {
        programProductMapper.insert(new ProgramProduct(programCode, product.getCode()));
    }

}
