package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RegimenCategoryMapperIT {

  @Autowired
  RegimenCategoryMapper regimenCategoryMapper;

  @Test
  public void shouldGetAllRegimenCategories() {
    List<RegimenCategory> regimenCategories = regimenCategoryMapper.getAll();
    assertThat(regimenCategories.size(), is(2));
    assertThat(regimenCategories.get(0).getCode(), is("ADULTS"));
  }

}
