package org.openlmis.vaccine.service.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.domain.Vitamin;
import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.openlmis.vaccine.domain.reports.ColdChainLineItem;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.repository.VitaminRepository;
import org.openlmis.vaccine.repository.VitaminSupplementationAgeGroupRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportColdChainRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportRepository;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.vaccine.service.VaccineProductDoseService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportServiceTest {

  @Mock
  VaccineReportRepository repository;

  @Mock
  ProgramProductService programProductService;

  @Mock
  VaccineLineItemService lLineItemService;

  @Mock
  DiseaseService diseaseService;

  @Mock
  ProcessingPeriodRepository periodService;

  @Mock
  VaccineProductDoseService productDoseService;

  @Mock
  ConfigurationSettingService settingService;

  @Mock
  VaccineReportColdChainRepository coldChainRepository;

  @Mock
  VitaminRepository vitaminRepository;

  @Mock
  VitaminSupplementationAgeGroupRepository ageGroupRepository;

  @Mock
  ProgramService programService;

  @InjectMocks
  VaccineReportService service;

  @Before
  public void setup() throws Exception{
    when(programProductService.getActiveByProgram(1L))
      .thenReturn(new ArrayList<ProgramProduct>());
    when(productDoseService.getForProgram(1L))
      .thenReturn(asList(new VaccineProductDose()));
    when(diseaseService.getAll())
      .thenReturn(new ArrayList<VaccineDisease>());
    when(coldChainRepository.getNewEquipmentLineItems(1L,1L))
      .thenReturn(new ArrayList<ColdChainLineItem>());
    when(vitaminRepository.getAll())
      .thenReturn(new ArrayList<Vitamin>());
    when(ageGroupRepository.getAll())
      .thenReturn(new ArrayList<VitaminSupplementationAgeGroup>());

  }


  @Test
  public void shouldReturnExistingRecordIfAlreadyInitialized() throws Exception{
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    when(repository.getByProgramPeriod(1L, 1L, 1L)).thenReturn(report);

    VaccineReport result = service.initialize(1L, 1L, 1L);
    verify(programProductService, never() ).getActiveByProgram(1L);
    assertThat(result, is(report));
  }

  @Test
  public void shouldInitializeWhenRecordIsNotFound() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));

    when(repository.getByProgramPeriod(1L, 1L, 1L)).thenReturn(null);

    doNothing().when(repository).insert(report);

    VaccineReport result = service.initialize(1L, 1L, 1L);

    verify(repository).insert(Matchers.any(VaccineReport.class));
  }

  @Test
  public void shouldGetPeriodsFor() throws Exception {

  }

  @Test
  public void shouldSave() throws Exception {

  }

  @Test
  public void shouldGetById() throws Exception {

  }
}