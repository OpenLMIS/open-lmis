/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AbstractModelPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  MessageService messageService;

  AbstractModelPersistenceHandler handler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }


  @Test
  public void shouldAddAuditInformationToModel() throws Exception {

    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    handler = instantiateHandler(null);

    handler.execute(currentRecord, 1, auditFields);

    assertThat(((BaseModel) currentRecord).getModifiedDate(), is(currentTimestamp));
    assertThat(((BaseModel) currentRecord).getModifiedBy(), is(1L));
    assertThat(((BaseModel) currentRecord).getId(), is(nullValue()));
  }

  @Test
  public void shouldAddFromExistingModel() throws Exception {

    final Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    BaseModel existing = new BaseModel() {
    };
    existing.setId(2L);
    existing.setModifiedDate(DateUtils.addDays(currentTimestamp, -1));

    handler = instantiateHandler(existing);

    handler.execute(currentRecord, 1, auditFields);

    assertThat(((BaseModel) currentRecord).getModifiedDate(), is(currentTimestamp));
    assertThat(((BaseModel) currentRecord).getModifiedBy(), is(1L));
    assertThat(((BaseModel) currentRecord).getId(), is(2L));
  }

  @Test
  public void shouldThrowExceptionIfModifiedDateOfExistingRecordIsSameAsCurrentTimeStamp() throws Exception {

    final Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    BaseModel existing = new BaseModel() {
    };
    existing.setId(2L);
    existing.setModifiedDate(currentTimestamp);

    handler = instantiateHandler(existing);
    handler.messageService = messageService;
    when(messageService.message("duplicate.record.error.code")).thenReturn("Duplicate Record");
    when(messageService.message("upload.record.error", "Duplicate Record", "1")).thenReturn("Upload error, Duplicate Record in row 1");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Upload error, Duplicate Record in row 1");

    handler.execute(currentRecord, 2, auditFields);

  }

  @Test
  public void shouldThrowIncorrectDataLengthError() throws Exception {
    handler = new AbstractModelPersistenceHandler() {
      @Override
      protected BaseModel getExisting(BaseModel record) {
        return null;
      }

      @Override
      protected void save(BaseModel record) {
        throw new DataIntegrityViolationException("some error");
      }

    };

    handler.messageService = messageService;
    when(messageService.message("error.incorrect.length")).thenReturn("invalid data length");
    when(messageService.message("upload.record.error", "invalid data length", "1")).thenReturn("final error message");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("final error message");

    handler.execute(new TestImportable(), 2, new AuditFields(null));
  }

  private AbstractModelPersistenceHandler instantiateHandler(final BaseModel existing) {
    return new AbstractModelPersistenceHandler() {
      @Override
      protected BaseModel getExisting(BaseModel record) {
        return existing;
      }

      @Override
      protected void save(BaseModel record) {
      }

      @Override
      public String getMessageKey() {
        return "duplicate.record.error.code";
      }

    };
  }

  class TestImportable extends BaseModel implements Importable {

  }
}
