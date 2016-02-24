package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertEquals;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ArchivedProductsMapperIT {
    @Autowired
    ArchivedProductsMapper archivedProductsMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProductMapper productMapper;
    private Facility facility;
    private Product product;

    @Before
    public void setUp() throws Exception {
        facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        product = make(a(defaultProduct));
        productMapper.insert(product);
        archivedProductsMapper.updateArchivedProductList(facility.getId(), product.getCode());
    }

    @Test
    public void shouldUpdateArchivedProductsCode() {
        List<String> codes = archivedProductsMapper.listArchivedProducts(facility.getId());

        assertEquals(product.getCode(), codes.get(0));
    }

    @Test
    public void shouldClearBeforeUpdateArchivedProductsCode() {
        archivedProductsMapper.clearArchivedProductList(facility.getId());
        List<String> codes = archivedProductsMapper.listArchivedProducts(facility.getId());

        assertEquals(0, codes.size());
    }
}