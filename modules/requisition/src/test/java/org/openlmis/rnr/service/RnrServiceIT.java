package org.openlmis.rnr.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertEquals;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE;
import static org.openlmis.core.builder.ProductBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RnrServiceIT {

    public static final String HIV = "HIV";
    @Autowired
    private ProgramSupportedMapper programSupportedMapper;
    @Autowired
    private RnrMapper rnrMapper;
    @Autowired
    private RnrLineItemMapper rnrLineItemMapper;
    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProgramProductMapper programProductMapper;
    @Autowired
    private FacilityApprovedProductMapper facilityApprovedProductMapper;

    @Autowired
    private RnrService rnrService;

    @Before
    public void setup() {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        programSupportedMapper.deleteAll();
        programProductMapper.deleteAll();
        facilityApprovedProductMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.deleteAll();
        Facility facility = make(a(FacilityBuilder.facility));
        facilityMapper.insert(facility);
        product(HIV);
    }

    @Test
    public void shouldInitRequisition() {
        Rnr rnr = rnrService.initRnr(FACILITY_CODE, HIV, "user");
        assertEquals(FACILITY_CODE, rnr.getFacilityCode());
        assertEquals(HIV, rnr.getProgramCode());
        assertEquals(RnrStatus.INITIATED, rnr.getStatus());
        assertEquals("user", rnr.getModifiedBy());
        assertEquals(1, rnr.getLineItems().size());
        assertEquals(rnr.getId(), rnr.getLineItems().get(0).getRnrId());
        assertEquals(PRODUCT_CODE, rnr.getLineItems().get(0).getProductCode());
        assertEquals("user", rnr.getLineItems().get(0).getModifiedBy());
    }

    private Product product(String programCode) {
        Product product = make(a(ProductBuilder.product));
        productMapper.insert(product);
        programProductMapper.insert(new ProgramProduct(programCode, product.getCode()));
        facilityApprovedProductMapper.insert(new FacilityApprovedProduct(FACILITY_TYPE, product.getCode()));
        return product;
    }

    @After
    public void tearDown() {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        programSupportedMapper.deleteAll();
        programProductMapper.deleteAll();
        facilityApprovedProductMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.deleteAll();
    }


}
