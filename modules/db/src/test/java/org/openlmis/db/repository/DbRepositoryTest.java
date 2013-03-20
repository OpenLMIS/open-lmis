package org.openlmis.db.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.repository.mapper.DbMapper;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbRepositoryTest {

  @Mock
  DbMapper dbMapper;

  @InjectMocks
  DbRepository dbRepository;

  @Test
  public void shouldGetCurrentDbTimeStamp() throws Exception {
    Date expectedTimeStamp = new Date();
    when(dbMapper.getCurrentTimeStamp()).thenReturn(expectedTimeStamp);

    Date currentTimeStamp = dbRepository.getCurrentTimeStamp();

    assertThat(currentTimeStamp, is(expectedTimeStamp));
    verify(dbMapper).getCurrentTimeStamp();
  }

  @Test
  public void shouldGetCountByTableName() throws Exception {
    String table = "facilities";
    when(dbMapper.getCount(table)).thenReturn(15);

    int facilityCount = dbRepository.getCount(table);

    assertThat(facilityCount, is(15));
    verify(dbMapper).getCount(table);
  }
}
