package org.openlmis.rnr.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;
import static org.joda.time.DateTime.now;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.facility;
import static org.openlmis.core.builder.ProductBuilder.PRODUCT_CODE;
import static org.openlmis.core.builder.ProductBuilder.product;

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

    @Before
    public void setUp() {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        facilityMapper.deleteAll();
        productMapper.deleteAll();
        facilityMapper.insert(make(a(facility)));
        productMapper.insert(make(a(product)));
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        int rnrId = rnrMapper.insert(new Rnr(FACILITY_CODE, "HIV", RnrStatus.INITIATED));
        int status = rnrLineItemMapper.insert(new RnrLineItem(rnrId, PRODUCT_CODE, "user", now().toDate()));
        assertEquals(1, status);
    }

    @After
    public void tearDown() throws Exception {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        facilityMapper.deleteAll();
        productMapper.deleteAll();
    }

}
