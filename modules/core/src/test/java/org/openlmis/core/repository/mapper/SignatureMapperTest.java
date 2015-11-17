package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Signature;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class SignatureMapperTest {

  @Autowired
  private SignatureMapper signatureMapper;

  @Test
  public void insertSignatureShouldInsertSignature() throws Exception {
    Signature signature = new Signature(Signature.Type.SUBMITTER, "sign my name");
    signature.setCreatedBy(1L);
    signature.setModifiedBy(1L);

    signatureMapper.insertSignature(signature);
    Signature savedSignature = signatureMapper.getById(signature.getId());
    assertThat(savedSignature.getText(), is(signature.getText()));
    assertThat(savedSignature.getType(), is(signature.getType()));
  }
}