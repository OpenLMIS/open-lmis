package org.openlmis.rnr.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
    Long facilityId;

    @Before
    public void setUp() {
        rnrLineItemMapper.deleteAll();
        rnrMapper.deleteAll();
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();
        productMapper.deleteAll();
        facilityId = facilityMapper.insert(make(a(defaultFacility)));
        product  = make(a(ProductBuilder.product));
        productMapper.insert(product);
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        Long rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        Long requisitionLineItemId = rnrLineItemMapper.insert(new RnrLineItem(rnrId, product, "user"));
        Long requisitionLineItemId1 = rnrLineItemMapper.insert(new RnrLineItem(rnrId, product, "user"));
        assertThat(requisitionLineItemId1 - requisitionLineItemId, is(1L));
    }

    @Test
    public void shouldReturnRnrLineItemsByRnrId() {
        Long rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, product, "user");
        rnrLineItemMapper.insert(lineItem);

        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);
        assertThat(rnrLineItems.size(), is(1));
        assertThat(rnrLineItems.get(0).getRnrId(), is(rnrId));
        assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
    }


    @Test
    public void shouldUpdateRnrLineItem() {
        Long rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, product, "user");
        Long generatedId = rnrLineItemMapper.insert(lineItem);
        lineItem.setId(generatedId);
        lineItem.setModifiedBy("user1");
        lineItem.setBeginningBalance(43);
        lineItem.setLossesAndAdjustments(10);
        rnrLineItemMapper.update(lineItem);
        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);

        assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
        assertThat(rnrLineItems.get(0).getLossesAndAdjustments(), is(10));
        assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
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
