package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.rnr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class LossesAndAdjustmentsMapperIT {


    public static final Integer HIV = 1;
    @Autowired
    LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    @Autowired
    RnrMapper rnrMapper;

    @Autowired
    RnrLineItemMapper rnrLineItemMapper;
    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramMapper programMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProgramProductMapper programProductMapper;

    RnrLineItem rnrLineItem;
    LossesAndAdjustments lossesAndAdjustments;
    LossesAndAdjustmentsType lossesAndAdjustmentsType;

    @Before
    public void setUp() throws Exception {
        Product product = make(a(ProductBuilder.defaultProduct));
        Program program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, 12.5F);
        productMapper.insert(product);
        programProductMapper.insert(programProduct);
        FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct("warehouse", programProduct, 3);
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);

        Rnr requisition = new Rnr(facility.getId(), HIV, RnrStatus.INITIATED, "user");
        rnrMapper.insert(requisition);

        rnrLineItem = new RnrLineItem(requisition.getId(), facilityApprovedProduct, "user");
        rnrLineItemMapper.insert(rnrLineItem);
        lossesAndAdjustments = new LossesAndAdjustments();
        lossesAndAdjustmentsType = new LossesAndAdjustmentsType();
        lossesAndAdjustmentsType.setName(LossesAndAdjustmentsTypeEnum.CLINIC_RETURN);
        lossesAndAdjustments.setType(lossesAndAdjustmentsType);
        lossesAndAdjustments.setQuantity(20);
    }

    @Test
    public void shouldInsertLossesAndAdjustments() {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        assertThat(id, is(notNullValue()));
    }

    @Test
    public void shouldGetLossesAndAdjustmentByRequisitionLineItemId() {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        List<LossesAndAdjustments> lossesAndAdjustments = lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId());
        assertThat(lossesAndAdjustments.size(), is(1));
        assertThat(lossesAndAdjustments.get(0).getId(), is(id));
        assertThat(lossesAndAdjustments.get(0).getQuantity(), is(20));
    }

    @Test
    public void shouldGetLossesAndAdjustmentsTypesByName() throws Exception {
        LossesAndAdjustmentsType lossesAndAdjustmentType = lossesAndAdjustmentsMapper.getLossesAndAdjustmentTypeByName(LossesAndAdjustmentsTypeEnum.CLINIC_RETURN);
        assertThat(lossesAndAdjustmentType.getName(), is(LossesAndAdjustmentsTypeEnum.CLINIC_RETURN));
        assertThat(lossesAndAdjustmentType.isAdditive(), is(true));
        assertThat(lossesAndAdjustmentType.getDisplayOrder(), is(9));
    }


    @Test
    public void shouldDeleteLossesAndAdjustment() throws Exception {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        lossesAndAdjustmentsMapper.delete(id);
        assertThat(lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId()).size(), is(0));
    }

    @Test
    public void shouldUpdateLossesAndAdjustments() throws Exception {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        lossesAndAdjustments.setId(id);
        lossesAndAdjustments.setQuantity(50);
        lossesAndAdjustmentsType.setName(LossesAndAdjustmentsTypeEnum.TRANSFER_OUT);
        lossesAndAdjustments.setType(lossesAndAdjustmentsType);
        lossesAndAdjustmentsMapper.update(lossesAndAdjustments);
        List<LossesAndAdjustments> lossesAndAdjustments = lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId());
        assertThat(lossesAndAdjustments.size(), is(1));
        assertThat(lossesAndAdjustments.get(0).getQuantity(), is(50));
        assertThat(lossesAndAdjustments.get(0).getType().getName(), is(LossesAndAdjustmentsTypeEnum.TRANSFER_OUT));
    }

    @Test
    public void shouldReturnAllLossesAndAdjustmentsTypes(){
        List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
        assertThat(lossesAndAdjustmentsTypes.size(), is(9));
    }

}
