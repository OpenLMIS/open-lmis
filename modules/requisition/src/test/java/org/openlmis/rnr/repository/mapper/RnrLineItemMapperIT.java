package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.rnr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_ID;

@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RnrLineItemMapperIT {

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProgramProductMapper programProductMapper;
    @Autowired
    private FacilityApprovedProductMapper facilityApprovedProductMapper;
    @Autowired
    private RnrMapper rnrMapper;
    @Autowired
    private RnrLineItemMapper rnrLineItemMapper;

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    FacilityApprovedProduct facilityApprovedProduct;
    Facility facility;

    @Before
    public void setUp() {
        Product product = make(a(ProductBuilder.defaultProduct));
        Program program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, 12.5F);
        facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        productMapper.insert(product);
        programProductMapper.insert(programProduct);
        facilityApprovedProduct = new FacilityApprovedProduct("warehouse", programProduct, 3);
        facilityApprovedProductMapper.insert(facilityApprovedProduct);
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        Rnr rnr = new Rnr(facility.getId(), PROGRAM_ID, RnrStatus.INITIATED, "user");
        rnrMapper.insert(rnr);
        RnrLineItem rnrLineItem = new RnrLineItem(rnr.getId(), facilityApprovedProduct, "user");
        rnrLineItemMapper.insert(rnrLineItem);
        assertNotNull(rnrLineItem.getId());
    }

    @Test
    public void shouldReturnRnrLineItemsByRnrId() {
        Rnr rnr = new Rnr(facility.getId(), PROGRAM_ID, RnrStatus.INITIATED, "user");
        rnrMapper.insert(rnr);
        RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityApprovedProduct, "user");
        lineItem.setPacksToShip(20);
        rnrLineItemMapper.insert(lineItem);

        LossesAndAdjustments lossesAndAdjustmentsClinicReturn = new LossesAndAdjustments();
        LossesAndAdjustmentsType lossesAndAdjustmentsTypeClinicReturn = new LossesAndAdjustmentsType();
        lossesAndAdjustmentsTypeClinicReturn.setName("CLINIC_RETURN");
        lossesAndAdjustmentsClinicReturn.setType(lossesAndAdjustmentsTypeClinicReturn);
        lossesAndAdjustmentsClinicReturn.setQuantity(20);

        LossesAndAdjustments lossesAndAdjustmentsTransferIn = new LossesAndAdjustments();
        LossesAndAdjustmentsType lossesAndAdjustmentsTypeTransferIn = new LossesAndAdjustmentsType();
        lossesAndAdjustmentsTypeTransferIn.setName("TRANSFER_IN");
        lossesAndAdjustmentsTransferIn.setType(lossesAndAdjustmentsTypeTransferIn);
        lossesAndAdjustmentsTransferIn.setQuantity(45);

        lossesAndAdjustmentsMapper.insert(lineItem, lossesAndAdjustmentsClinicReturn);
        lossesAndAdjustmentsMapper.insert(lineItem, lossesAndAdjustmentsTransferIn);

        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

        assertThat(rnrLineItems.size(), is(1));
        RnrLineItem rnrLineItem = rnrLineItems.get(0);
        assertThat(rnrLineItem.getRnrId(), is(rnr.getId()));
        assertThat(rnrLineItem.getDosesPerMonth(), is(30));
        assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
        assertThat(rnrLineItem.getProduct(), is("Primary Name Tablet strength mg"));
        assertThat(rnrLineItem.getPacksToShip(), is(20));
        assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
        assertThat(rnrLineItem.getRoundToZero(), is(true));
        assertThat(rnrLineItem.getPackSize(), is(10));
        assertThat(rnrLineItem.getPrice(), is(12.5F));
    }

    @Test
    public void shouldUpdateRnrLineItem() {
        Rnr rnr = new Rnr(facility.getId(), PROGRAM_ID, RnrStatus.INITIATED, "user");
        rnrMapper.insert(rnr);
        RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityApprovedProduct, "user");
        rnrLineItemMapper.insert(lineItem);
        lineItem.setModifiedBy("user1");
        lineItem.setBeginningBalance(43);
        lineItem.setTotalLossesAndAdjustments(20);
        lineItem.setReasonForRequestedQuantity("Quantity Requested more in liu of coming rains");
        int updateCount = rnrLineItemMapper.update(lineItem);
        assertThat(updateCount, is(1));
        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

        assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
        assertThat(rnrLineItems.get(0).getTotalLossesAndAdjustments(), is(20));
        assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
        assertThat(rnrLineItems.get(0).getReasonForRequestedQuantity(), is("Quantity Requested more in liu of coming rains"));
    }
}
