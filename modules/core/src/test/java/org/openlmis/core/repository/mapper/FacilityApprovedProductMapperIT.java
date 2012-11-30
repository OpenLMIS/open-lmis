package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE;
import static org.openlmis.core.builder.ProductBuilder.PRODUCT_CODE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class FacilityApprovedProductMapperIT {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    FacilityApprovedProductMapper facilityApprovedProductMapper;

    @Before
    @After
    public void setUp() throws Exception {
        facilityApprovedProductMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.deleteAll();

    }

    @Test
    public void shouldInsertFacilityApprovedProduct() throws Exception {
        productMapper.insert(make(a(ProductBuilder.product)));
        facilityMapper.insert(make(a(FacilityBuilder.defaultFacility)));
        FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();
        facilityApprovedProduct.setFacilityTypeCode(FACILITY_TYPE);
        facilityApprovedProduct.setProductCode(PRODUCT_CODE);
        int status = facilityApprovedProductMapper.insert(facilityApprovedProduct);

        assertThat(status,is(1));
    }
}
