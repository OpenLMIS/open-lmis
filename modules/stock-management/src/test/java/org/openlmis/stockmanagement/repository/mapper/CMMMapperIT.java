package org.openlmis.stockmanagement.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.utils.DateUtil.parseDate;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-stock-management.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class CMMMapperIT {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CMMMapper cmmMapper;

    private Facility defaultFacility;
    private Product defaultProduct;
    private CMMEntry cmmEntry;
    private Date periodBegin;
    private Date periodEnd;

    @Before
    public void setup() {
        defaultFacility = make(a(FacilityBuilder.defaultFacility));
        defaultProduct = make(a(ProductBuilder.defaultProduct));
        facilityMapper.insert(defaultFacility);
        productMapper.insert(defaultProduct);

        cmmEntry = new CMMEntry();
        cmmEntry.setProductCode(defaultProduct.getCode());
        cmmEntry.setFacilityId(defaultFacility.getId());
        periodBegin = parseDate("2016-01-21", "yyyy-MM-dd");
        periodEnd = parseDate("2016-02-20", "yyyy-MM-dd");
        cmmEntry.setCmmValue(1.0F);
        cmmEntry.setPeriodBegin(periodBegin);
        cmmEntry.setPeriodEnd(periodEnd);
    }

    @Test
    public void shouldInsertCMMEntry() throws Exception {
        cmmMapper.insert(cmmEntry);
        CMMEntry cmmEntryActual = cmmMapper.getCMMEntryByFacilityAndPeriodAndProductCode(defaultFacility.getId(), defaultProduct.getCode(), periodBegin, periodEnd);
        assertEquals(cmmEntry.getCmmValue(), cmmEntryActual.getCmmValue());
        assertEquals(cmmEntry.getProductCode(), cmmEntryActual.getProductCode());
        assertEquals(cmmEntry.getFacilityId(), cmmEntryActual.getFacilityId());
    }

    @Test
    public void shouldUpdateCMMEntry() throws Exception {
        cmmMapper.insert(cmmEntry);
        cmmEntry.setCmmValue(5.0F);
        cmmMapper.update(cmmEntry);
        CMMEntry cmmEntryActual = cmmMapper.getCMMEntryByFacilityAndPeriodAndProductCode(defaultFacility.getId(), defaultProduct.getCode(), periodBegin, periodEnd);
        assertThat(cmmEntryActual.getCmmValue().floatValue(), is(5.0F));
    }
}