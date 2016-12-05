package org.openlmis.restapi.service;

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
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.openlmis.core.exception.DataException;
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
    column.setCode("SYPHILLIS");
    when(programDataColumnMapper.getColumnByCode("SYPHILLIS")).thenReturn(column2);

    programDataFormDTO.setProgramDataFormItems(asList(programDataFormItemDTO, programDataFormItemDTO2));

    ArgumentCaptor<ProgramDataForm> captor = ArgumentCaptor.forClass(ProgramDataForm.class);
    restProgramDataService.createProgramDataForm(programDataFormDTO, 1L);
    List<ProgramDataForm> captorAllValues = captor.getAllValues();
    verify(programDataRepository, times(1)).createProgramDataForm(captor.capture());
    ProgramDataForm convertedProgramDataForm = captorAllValues.get(0);
    assertThat(convertedProgramDataForm.getSupplementalProgram().getCode(), is("RAPID_TEST"));
    assertThat(convertedProgramDataForm.getStartDate(), is(periodStartDate));
    assertThat(convertedProgramDataForm.getEndDate(), is(periodEndDate));
    assertThat(convertedProgramDataForm.getSubmittedTime(), is(submittedTime));
    assertThat(convertedProgramDataForm.getFacility().getCode(), is("F1"));
    assertThat(convertedProgramDataForm.getDataItemList().size(), is(2));
    assertThat(convertedProgramDataForm.getCreatedBy(), is(1L));
    assertThat(convertedProgramDataForm.getModifiedBy(), is(1L));
    assertThat(convertedProgramDataForm.getDataItemList().get(0).getName(), is("PUBLIC_PHARMACY"));
    assertThat(convertedProgramDataForm.getDataItemList().get(0).getValue(), is(100L));
    assertThat(convertedProgramDataForm.getDataItemList().get(0).getProgramDataColumn(), is(column));
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
}