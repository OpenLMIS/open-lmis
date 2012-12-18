package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE_CODE;
import static org.openlmis.core.builder.ProductBuilder.PRODUCT_CODE;

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

        FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct(FACILITY_TYPE_CODE, programProduct.getId(), MAX_MONTHS_OF_STOCK);
        int status = facilityApprovedProductMapper.insert(facilityApprovedProduct);

        assertThat(status, is(1));
    }

}
