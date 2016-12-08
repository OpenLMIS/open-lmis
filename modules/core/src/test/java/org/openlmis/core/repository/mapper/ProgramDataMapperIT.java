package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Signature;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(transactionManager = "openLmisTransactionManager")
public class ProgramDataMapperIT {

  @Autowired
  ProgramDataMapper programDataMapper;

  @Autowired
  SupplementalProgramMapper supplementalProgramMapper;

  @Autowired
  ProgramDataColumnMapper programDataColumnMapper;

  @Autowired
  ProgramDataItemMapper programDataItemMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Autowired
  SignatureMapper signatureMapper;

  @Test
  public void shouldInsertProgramDataFormWithItems() throws SQLException {
    queryExecutor.executeQuery("INSERT INTO supplemental_programs (code, name, description, active) " +
        "VALUES ('RAPID_TEST', 'Rapid Test', 'Rapid test', TRUE);");

    queryExecutor.executeQuery("INSERT INTO program_data_columns (code, supplementalProgramId) VALUES " +
        "('HIV-DETERMINE-CONSUME', (SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));");

    queryExecutor.executeQuery("INSERT INTO program_data_columns (code, supplementalProgramId) VALUES " +
        "('HIV-DETERMINE-POSITIVE', (SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));");

    SupplementalProgram supplementalProgram = supplementalProgramMapper.getSupplementalProgramByCode("RAPID_TEST");

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProgramDataForm programDataForm = new ProgramDataForm();
    programDataForm.setFacility(facility);
    Date startDate = DateUtil.parseDate("2016-11-21", DateUtil.FORMAT_DATE);
    programDataForm.setStartDate(startDate);
    Date endDate = DateUtil.parseDate("2016-12-20", DateUtil.FORMAT_DATE);
    programDataForm.setEndDate(endDate);
    programDataForm.setSubmittedTime(new Date());
    programDataForm.setSupplementalProgram(supplementalProgram);
    programDataForm.setCreatedBy(1L);
    programDataForm.setModifiedBy(1L);
    programDataMapper.insert(programDataForm);

    ProgramDataColumn programDataColumn1 = programDataColumnMapper.getColumnByCode("HIV-DETERMINE-CONSUME");
    ProgramDataColumn programDataColumn2 = programDataColumnMapper.getColumnByCode("HIV-DETERMINE-POSITIVE");

    ProgramDataItem programDataItem1 = new ProgramDataItem();
    programDataItem1.setName("PUBLIC_PHARMACY");
    programDataItem1.setProgramDataColumn(programDataColumn1);
    programDataItem1.setValue(100L);
    programDataItem1.setProgramDataForm(programDataForm);

    ProgramDataItem programDataItem2 = new ProgramDataItem();
    programDataItem1.setName("WARD");
    programDataItem1.setProgramDataColumn(programDataColumn2);
    programDataItem1.setValue(300L);
    programDataItem2.setProgramDataForm(programDataForm);
    programDataItemMapper.insert(programDataItem1);
    programDataItemMapper.insert(programDataItem2);

    Signature signature1 = new Signature(Signature.Type.SUBMITTER, "mystique");
    Signature signature2 = new Signature(Signature.Type.APPROVER, "magneto");
    List<Signature> signatures = asList(signature1, signature2);
    programDataForm.setProgramDataFormSignatures(signatures);
    signatureMapper.insertSignature(signature1);
    signatureMapper.insertSignature(signature2);
    programDataMapper.insertProgramDataFormSignature(programDataForm, signature1);
    programDataMapper.insertProgramDataFormSignature(programDataForm, signature2);

    List<ProgramDataForm> programDataFormResult = programDataMapper.getByFacilityId(facility.getId());
    assertThat(programDataFormResult.get(0).getFacility().getCode(), is(facility.getCode()));
    assertThat(programDataFormResult.get(0).getSupplementalProgram().getCode(), is(supplementalProgram.getCode()));
    assertThat(programDataFormResult.get(0).getStartDate(), is(startDate));
    assertThat(programDataFormResult.get(0).getEndDate(), is(endDate));
    assertThat(programDataFormResult.get(0).getCreatedBy(), is(1L));
    assertThat(programDataFormResult.get(0).getModifiedBy(), is(1L));
    assertThat(programDataFormResult.get(0).getProgramDataItems().size(), is(2));
    assertThat(programDataFormResult.get(0).getProgramDataFormSignatures().size(), is(2));
  }
}