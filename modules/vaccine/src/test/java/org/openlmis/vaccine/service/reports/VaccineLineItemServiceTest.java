/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.service.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.repository.reports.*;
import org.springframework.beans.factory.annotation.Autowired;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineLineItemServiceTest {

  @Mock
  VaccineReportLogisticsLineItemRepository logisticsRepository;

  @Mock
  VaccineReportDiseaseLineItemRepository diseaseLineItemRepository;

  @Mock
  VaccineReportCoverageItemRepository coverageItemRepository;

  @Mock
  VaccineReportAdverseEffectRepository adverseLineItemRepository;

  @Mock
  VaccineReportCampaignLineItemRepository campaignLineItemRepository;

  @Mock
  VaccineReportColdChainRepository coldChainRepository;

  @Mock
  VitaminSupplementationLineItemRepository vitaminSupplementationLineItemRepository;


  @InjectMocks
  VaccineLineItemService service;

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void shouldInsertLogisticsLineItems() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    lineItem.setReportId(2L);
    service.saveLogisticsLineItems(asList(lineItem), 2L);
    verify(logisticsRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateLogisticsLineItems() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    lineItem.setReportId(2L);
    lineItem.setId(2L);
    service.saveLogisticsLineItems(asList(lineItem), 2L);
    verify(logisticsRepository).update(lineItem);
  }

  @Test
  public void shouldInsertDiseaseLineItems() throws Exception {
    DiseaseLineItem lineItem = new DiseaseLineItem();
    lineItem.setReportId(2L);
    service.saveDiseaseLineItems(asList(lineItem), 2L);
    verify(diseaseLineItemRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateDiseaseLineItems() throws Exception {
    DiseaseLineItem lineItem = new DiseaseLineItem();
    lineItem.setId(2L);
    lineItem.setReportId(2L);
    service.saveDiseaseLineItems(asList(lineItem), 2L);
    verify(diseaseLineItemRepository).update(lineItem);
  }

  @Test
  public void shouldInsertCoverageLineItems() throws Exception {
    VaccineCoverageItem lineItem = new VaccineCoverageItem();
    lineItem.setReportId(2L);
    service.saveCoverageLineItems(asList(lineItem), 2L);
    verify(coverageItemRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateCoverageLineItems() throws Exception {
    VaccineCoverageItem lineItem = new VaccineCoverageItem();
    lineItem.setReportId(2L);
    lineItem.setId(2L);
    service.saveCoverageLineItems(asList(lineItem), 2L);
    verify(coverageItemRepository).update(lineItem);
  }

  @Test
  public void shouldSaveAdverseEffectLineItems() throws Exception {
    AdverseEffectLineItem lineItem = new AdverseEffectLineItem();
    lineItem.setReportId(2L);
    service.saveAdverseEffectLineItems(asList(lineItem), 2L);
    verify(adverseLineItemRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateAdverseEffectLineItems() throws Exception {
    AdverseEffectLineItem lineItem = new AdverseEffectLineItem();
    lineItem.setReportId(2L);
    lineItem.setId(3L);
    service.saveAdverseEffectLineItems(asList(lineItem), 2L);
    verify(adverseLineItemRepository).update(lineItem);
  }

  @Test
  public void shouldInsertCampaignLineItems() throws Exception {
    CampaignLineItem lineItem = new CampaignLineItem();
    lineItem.setReportId(2L);
    service.saveCampaignLineItems(asList(lineItem), 2L);
    verify(campaignLineItemRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateCampaignLineItems() throws Exception {
    CampaignLineItem lineItem = new CampaignLineItem();
    lineItem.setReportId(2L);
    lineItem.setId(2L);
    service.saveCampaignLineItems(asList(lineItem), 2L);
    verify(campaignLineItemRepository).update(lineItem);
  }

  @Test
  public void shouldSaveColdChainLIneItems() throws Exception {
    ColdChainLineItem lineItem = new ColdChainLineItem();
    lineItem.setReportId(2L);
    service.saveColdChainLIneItems(asList(lineItem), 2L);
    verify(coldChainRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateColdChainLIneItems() throws Exception {
    ColdChainLineItem lineItem = new ColdChainLineItem();
    lineItem.setId(3L);
    lineItem.setReportId(2L);
    service.saveColdChainLIneItems(asList(lineItem), 2L);
    verify(coldChainRepository).update(lineItem);
  }

  @Test
  public void shouldInsertVitaminLineItems() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    lineItem.setReportId(2L);
    service.saveVitaminLineItems(asList(lineItem), 2L);
    verify(vitaminSupplementationLineItemRepository).insert(lineItem);
  }

  @Test
  public void shouldUpdateVitaminLineItems() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    lineItem.setId(3L);
    lineItem.setReportId(2L);
    service.saveVitaminLineItems(asList(lineItem), 2L);
    verify(vitaminSupplementationLineItemRepository).update(lineItem);
  }
}