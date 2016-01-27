package org.openlmis.email.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailMessage;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EmailMessageRowMapperTest {

  @Mock
  private ResultSet resultSet;

  @InjectMocks
  EmailMessageRowMapper emailMessageRowMapper;

  @Test
  public void shouldCreateOrderFromResultSet() throws Exception {
    when(resultSet.getString("receiver")).thenReturn("receiver");
    when(resultSet.getString("subject")).thenReturn("subject");
    when(resultSet.getString("content")).thenReturn("content");
    when(resultSet.getLong("id")).thenReturn(1L);

    EmailMessage emailMessage = emailMessageRowMapper.mapRow(resultSet, 1);

    assertThat(emailMessage.getTo()[0], is("receiver"));
    assertThat(emailMessage.getSubject(), is("subject"));
    assertThat(emailMessage.getText(), is("content"));
    assertThat(emailMessage.getId(), is(1L));
  }
}
