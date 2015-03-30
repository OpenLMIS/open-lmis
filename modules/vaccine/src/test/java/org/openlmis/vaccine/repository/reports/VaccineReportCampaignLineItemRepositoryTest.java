package org.openlmis.vaccine.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.CampaignLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportCampaignLineItemMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportCampaignLineItemRepositoryTest {

  @Mock
  VaccineReportCampaignLineItemMapper mapper;

  @InjectMocks
  VaccineReportCampaignLineItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    CampaignLineItem lineItem = new CampaignLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    CampaignLineItem lineItem = new CampaignLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }
}