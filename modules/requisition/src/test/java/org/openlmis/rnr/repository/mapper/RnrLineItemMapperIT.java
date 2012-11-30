package org.openlmis.rnr.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RnrLineItemMapperIT {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RnrMapper rnrMapper;

    @Autowired
    private RnrLineItemMapper rnrLineItemMapper;

    @Autowired
    private ProgramSupportedMapper programSupportedMapper;

    Product product;

    @Before
    public void setUp() {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.insert(make(a(defaultFacility)));
        product  = make(a(ProductBuilder.product));
        product.setProductForm(new ProductForm());
        product.setProductDosageUnit(new DosageUnit());
        productMapper.insert(product);
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        int rnrId = rnrMapper.insert(new Rnr(FACILITY_CODE, "HIV", RnrStatus.INITIATED));
        int status = rnrLineItemMapper.insert(new RnrLineItem(rnrId, product, "user"));
        assertEquals(1, status);
    }

    @After
    public void tearDown() throws Exception {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();
        productMapper.deleteAll();
    }

}
