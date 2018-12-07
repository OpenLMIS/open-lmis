package org.openlmis.restapi.service;

import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Signature;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramDataColumnRepository;
import org.openlmis.core.repository.ProgramDataRepository;
import org.openlmis.core.repository.SyncUpHashRepository;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramDataColumnMapper;
import org.openlmis.core.repository.mapper.SupplementalProgramMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ProgramDataFormBuilder;
import org.openlmis.restapi.builder.ProgramDataFormItemBuilder;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.domain.ProgramDataFormItemDTO;
import org.openlmis.rnr.domain.Service;
import org.openlmis.rnr.repository.ServiceRepository;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProgramDataServiceTest {

  @Mock
  private ProgramDataRepository programDataRepository;

  @Mock
  private SupplementalProgramMapper supplementalProgramMapper;

  @Mock
  private FacilityMapper facilityMapper;

  @Mock
  private ProgramDataColumnMapper programDataColumnMapper;

  @Mock
  private SyncUpHashRepository syncUpHashRepository;

  @Mock
  private RestRequisitionService restRequisitionService;

  @Mock
  private ServiceRepository serviceRepository;

  @Mock
  private ProgramDataColumnRepository programDataColumnRepository;

  @InjectMocks
  private RestProgramDataService restProgramDataService;

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldConvertProgramDataDTOToProgramData() throws Exception {
    Mockito.when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);

    Date periodStartDate = DateUtil.parseDate("2016-01-21", DateUtil.FORMAT_DATE);
    Date periodEndDate = DateUtil.parseDate("2016-02-20", DateUtil.FORMAT_DATE);
    Date submittedTime = DateUtil.parseDate("2016-12-01 11:11:11");
    ProgramDataFormDTO programDataFormDTO = make(a(ProgramDataFormBuilder.defaultProgramDataForm,
        with(ProgramDataFormBuilder.facilityId, 123L),
        with(ProgramDataFormBuilder.programCode, "RAPID_TEST"),
        with(ProgramDataFormBuilder.periodBegin, periodStartDate),
        with(ProgramDataFormBuilder.periodEnd, periodEndDate),
        with(ProgramDataFormBuilder.submittedTime, submittedTime)));

    List<Signature> signatures = asList(new Signature(Signature.Type.SUBMITTER, "mystique"), new Signature(Signature.Type.APPROVER, "magneto"));
    programDataFormDTO.setProgramDataFormSignatures(signatures);

    SupplementalProgram formProgram = new SupplementalProgram();
    formProgram.setCode("RAPID_TEST");
    when(supplementalProgramMapper.getSupplementalProgramByCode("RAPID_TEST")).thenReturn(formProgram);

    Facility facility = make(a(FacilityBuilder.defaultFacility,
        with(FacilityBuilder.facilityId, 123L),
        with(FacilityBuilder.code, "F1")));
    when(facilityMapper.getById(123L)).thenReturn(facility);

    ProgramDataFormItemDTO programDataFormItemDTO = make(a(ProgramDataFormItemBuilder.defaultProgramDataItem,
        with(ProgramDataFormItemBuilder.name, "PUBLIC_PHARMACY"),
        with(ProgramDataFormItemBuilder.columnCode, "MALARIA"),
        with(ProgramDataFormItemBuilder.value, 100L)));

    ProgramDataColumn column = new ProgramDataColumn();
    column.setCode("MALARIA");
    when(programDataColumnMapper.getColumnByCode("MALARIA")).thenReturn(column);

    ProgramDataFormItemDTO programDataFormItemDTO2 = make(a(ProgramDataFormItemBuilder.defaultProgramDataItem,
        with(ProgramDataFormItemBuilder.name, "PUBLIC_PHARMACY"),
        with(ProgramDataFormItemBuilder.columnCode, "SYPHILLIS"),
        with(ProgramDataFormItemBuilder.value, 200L)));

    ProgramDataColumn column2 = new ProgramDataColumn();
    column2.setCode("SYPHILLIS");
    when(programDataColumnMapper.getColumnByCode("SYPHILLIS")).thenReturn(column2);

    programDataFormDTO.setProgramDataFormItems(asList(programDataFormItemDTO, programDataFormItemDTO2));

    ArgumentCaptor<ProgramDataForm> captor = ArgumentCaptor.forClass(ProgramDataForm.class);

    when(serviceRepository.getAll()).thenReturn(Lists.newArrayList(new Service("code", "PUBLIC_PHARMACY", "programId", true)));
    when(programDataColumnRepository.getAll())
            .thenReturn(Lists.newArrayList(new ProgramDataColumn("SYPHILLIS", "SYPHILLIS", "programId"),
                    new ProgramDataColumn("MALARIA", "MALARIA", "programId")));

    restProgramDataService.createProgramDataForm(programDataFormDTO, 1L);
    List<ProgramDataForm> captorAllValues = captor.getAllValues();
    verify(programDataRepository, times(1)).createProgramDataForm(captor.capture());
    ProgramDataForm convertedProgramDataForm = captorAllValues.get(0);
    assertThat(convertedProgramDataForm.getSupplementalProgram().getCode(), is("RAPID_TEST"));
    assertThat(convertedProgramDataForm.getStartDate(), is(periodStartDate));
    assertThat(convertedProgramDataForm.getEndDate(), is(periodEndDate));
    assertThat(convertedProgramDataForm.getSubmittedTime(), is(submittedTime));
    assertThat(convertedProgramDataForm.getFacility().getCode(), is("F1"));
    assertThat(convertedProgramDataForm.getProgramDataItems().size(), is(2));
    assertThat(convertedProgramDataForm.getCreatedBy(), is(1L));
    assertThat(convertedProgramDataForm.getModifiedBy(), is(1L));
    assertThat(convertedProgramDataForm.getProgramDataFormSignatures().get(0).getType(), is(Signature.Type.SUBMITTER));
    assertThat(convertedProgramDataForm.getProgramDataFormSignatures().get(0).getText(), is("mystique"));
    assertThat(convertedProgramDataForm.getProgramDataItems().get(0).getName(), is("PUBLIC_PHARMACY"));
    assertThat(convertedProgramDataForm.getProgramDataItems().get(0).getValue(), is(100L));
    assertThat(convertedProgramDataForm.getProgramDataItems().get(0).getProgramDataColumn(), is(column));
  }

  @Test
  public void shouldThrowDataExceptionIfFacilityDoesNotExist() {
    Mockito.when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);

    expectException.expect(DataException.class);
    expectException.expectMessage("error.facility.unknown");

    restProgramDataService.createProgramDataForm(make(a(ProgramDataFormBuilder.defaultProgramDataForm)), 12345L);

  }

  @Test
  public void shouldThrowDataExceptionIfColumnIsInvalid() {
    Mockito.when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);

    expectException.expect(DataException.class);
    expectException.expectMessage("error.wrong.program.column");

    ProgramDataFormDTO programDataFormDTO = make(a(ProgramDataFormBuilder.defaultProgramDataForm));

    when(supplementalProgramMapper.getSupplementalProgramByCode("RAPID_TEST")).thenReturn(new SupplementalProgram());

    when(facilityMapper.getById(1L)).thenReturn(new Facility());

    ProgramDataFormItemDTO programDataFormItemDTO = make(a(ProgramDataFormItemBuilder.defaultProgramDataItem,
        with(ProgramDataFormItemBuilder.name, "PUBLIC_PHARMACY"),
        with(ProgramDataFormItemBuilder.columnCode, "MALARIA"),
        with(ProgramDataFormItemBuilder.value, 100L)));

    programDataFormDTO.setProgramDataFormItems(asList(programDataFormItemDTO));

    restProgramDataService.createProgramDataForm(programDataFormDTO, 1L);
  }

  @Test
  public void shouldNotSaveProgramFormWhenHashExists() throws Exception {
    Mockito.when(syncUpHashRepository.hashExists(anyString())).thenReturn(true);

    restProgramDataService.createProgramDataForm(make(a(ProgramDataFormBuilder.defaultProgramDataForm)), 1L);

    verify(syncUpHashRepository, never()).save(anyString());
  }

  @Test
  public void shouldSaveProgramFormWhenHashDoesNotExist() throws Exception {
    Mockito.when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);

    when(facilityMapper.getById(1L)).thenReturn(new Facility());
    restProgramDataService.createProgramDataForm(make(a(ProgramDataFormBuilder.defaultProgramDataForm)), 1L);

    verify(syncUpHashRepository).save(anyString());
  }

  @Test
  public void shouldGetProgramFormsByFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    SupplementalProgram supplementalProgram = new SupplementalProgram("P1", "", "", true);
    Date startDate = DateUtil.parseDate("2016-10-21", DateUtil.FORMAT_DATE);
    Date endDate = DateUtil.parseDate("2016-11-20", DateUtil.FORMAT_DATE);
    Date submittedTime = new Date();

    ProgramDataForm programDataForm = new ProgramDataForm(facility, supplementalProgram,
        startDate, endDate, submittedTime, "");
    ProgramDataColumn programDataColumn1 = new ProgramDataColumn("A1", "", "", supplementalProgram);
    ProgramDataColumn programDataColumn2 = new ProgramDataColumn("A2", "", "", supplementalProgram);
    ProgramDataItem programDataItem1 = new ProgramDataItem(programDataForm, "category 1", programDataColumn1, 10L);
    ProgramDataItem programDataItem2 = new ProgramDataItem(programDataForm, "category 1", programDataColumn2, 5L);
    ProgramDataItem programDataItem3 = new ProgramDataItem(programDataForm, "category 2", programDataColumn2, 100L);
    programDataForm.setProgramDataItems(asList(programDataItem1, programDataItem2, programDataItem3));
    List<Signature> signatures = asList(new Signature(Signature.Type.SUBMITTER, "mystique"), new Signature(Signature.Type.APPROVER, "magneto"));
    programDataForm.setProgramDataFormSignatures(signatures);

    Date startDate2 = DateUtil.parseDate("2016-11-21", DateUtil.FORMAT_DATE);
    Date endDate2 = DateUtil.parseDate("2016-12-20", DateUtil.FORMAT_DATE);
    ProgramDataForm programDataForm2 = new ProgramDataForm(facility, supplementalProgram,
        startDate2, endDate2, submittedTime, "");
    ProgramDataItem programDataItem4 = new ProgramDataItem(programDataForm2, "category 1", programDataColumn1, 20L);
    ProgramDataItem programDataItem5 = new ProgramDataItem(programDataForm2, "category 2", programDataColumn2, 25L);
    programDataForm2.setProgramDataItems(asList(programDataItem4, programDataItem5));
    when(programDataRepository.getProgramDataFormsByFacilityId(12L)).thenReturn(asList(programDataForm, programDataForm2));

    List<ProgramDataFormDTO> programDataFormDTOs = restProgramDataService.getProgramDataFormsByFacility(12L);
    assertThat(programDataFormDTOs.size(), is(2));
    assertThat(programDataFormDTOs.get(0).getFacilityId(), is(facility.getId()));
    assertThat(programDataFormDTOs.get(0).getPeriodBegin(), is(startDate));
    assertThat(programDataFormDTOs.get(0).getPeriodEnd(), is(endDate));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormItems().size(), is(3));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormItems().get(0).getColumnCode(), is("A1"));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormItems().get(0).getName(), is("category 1"));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormItems().get(0).getValue(), is(10L));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormSignatures().get(0).getType(), is(Signature.Type.SUBMITTER));
    assertThat(programDataFormDTOs.get(0).getProgramDataFormSignatures().get(0).getText(), is("mystique"));
  }
}