package org.openlmis.db.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.repository.DbRepository;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbServiceTest {

  @Mock
  DbRepository dbRepository;

  @InjectMocks
  DbService dbService;

  @Test
  public void shouldGetCurrentTimestamp() throws Exception {
    Date expectedTimestamp = new Date();
    when(dbRepository.getCurrentTimeStamp()).thenReturn(expectedTimestamp);

    Date currentTimeStamp = dbService.getCurrentTimestamp();

    assertThat(currentTimeStamp,is(expectedTimestamp));
    verify(dbRepository).getCurrentTimeStamp();
  }

  @Test
  public void shouldGetCountByTableName() throws Exception {
    String table = "facilities";
    when(dbRepository.getCount(table)).thenReturn(15);

    int facilityCount = dbService.getCount(table);

    assertThat(facilityCount, is(15));
    verify(dbRepository).getCount(table);
  }
}
