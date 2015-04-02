package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.repository.MaintenanceRequestRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class MaintenanceRequestServiceTest {

  @Mock
  MaintenanceRequestRepository repository;

  @InjectMocks
  MaintenanceRequestService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetAllForFacility() throws Exception {
    service.getAllForFacility(1L);
    verify(repository).getAllForFacility(1L);
  }

  @Test
  public void shouldGetAllForVendor() throws Exception {
    service.getAllForVendor(3L);
    verify(repository).getAllForVendor(3L);
  }

  @Test
  public void shouldGetOutstandingForVendor() throws Exception {
    service.getOutstandingForVendor(3L);
    verify(repository).getOutstandingForVendor(3L);
  }

  @Test
  public void shouldGetOutstandingForUser() throws Exception {
    service.getOutstandingForUser(3L);
    verify(repository).getOutstandingForUser(3L);
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(6L);
    verify(repository).getById(6L);
  }

  @Test
  public void shouldSaveAnUpdate() throws Exception {
    MaintenanceRequest request = new MaintenanceRequest();
    request.setId(3L);
    service.save(request);
    verify(repository, never()).insert(request);
    verify(repository).update(request);
  }

  @Test
  public void shouldSaveANewRecord() throws Exception {
    MaintenanceRequest request = new MaintenanceRequest();

    service.save(request);
    verify(repository).insert(request);
    verify(repository, never()).update(request);
  }

  @Test
  public void shouldGetFullHistory() throws Exception {
    service.getFullHistory(3L);
    verify(repository).getFullHistory(3L);
  }
}