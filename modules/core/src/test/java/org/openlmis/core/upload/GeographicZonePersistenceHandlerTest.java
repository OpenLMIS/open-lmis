package org.openlmis.core.upload;

import org.junit.Test;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GeographicZonePersistenceHandlerTest {

  @Test
  public void shouldSaveGeographicZone() throws Exception {

    GeographicZoneRepository repository = mock(GeographicZoneRepository.class);
    GeographicZonePersistenceHandler geographicZonePersistenceHandler = new GeographicZonePersistenceHandler(repository);
    GeographicZone geographicZone = new GeographicZone();

    geographicZonePersistenceHandler.save(geographicZone, 1);
    assertThat(geographicZone.getModifiedBy(), is(1));
    verify(repository).save(geographicZone);

  }
}
