package org.openlmis.vaccine.repository.mapper.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.builders.reports.DiseaseLineItemBuilder;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.*;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportDiseaseLineItemMapperIT {

  @Autowired
  VaccineReportDiseaseLineItemMapper mapper;

  @Test
  public void testInsert() throws Exception {
    DiseaseLineItem item = make(a(DiseaseLineItemBuilder.defaultDiseaseLineItem));
    mapper.insert(item);
  }

  @Test
  public void testUpdate() throws Exception {

  }

  @Test
  public void testGetLineItems() throws Exception {

  }
}