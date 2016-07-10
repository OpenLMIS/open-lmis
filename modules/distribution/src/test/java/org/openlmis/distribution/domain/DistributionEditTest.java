package org.openlmis.distribution.domain;

import com.beust.jcommander.internal.Lists;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.UnitTests;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionEditTest {

  @Mock
  private User user;

  @Mock
  private Distribution distribution;

  @Test
  public void shouldSortByDateDesc() {
    Date third = getDate(7, 30, 0);
    Date second = getDate(7, 45, 8);
    Date first = getDate(8, 2, 56);
    Date last = getDate(6, 50, 0);

    List<DistributionEdit> list = Lists.newArrayList();
    list.add(create(third));
    list.add(create(second));
    list.add(create(first));
    list.add(create(last));

    Collections.sort(list);

    assertThat(list.get(0).getStartedAt(), is(first));
    assertThat(list.get(1).getStartedAt(), is(second));
    assertThat(list.get(2).getStartedAt(), is(third));
    assertThat(list.get(3).getStartedAt(), is(last));
  }

  private DistributionEdit create(Date time) {
    DistributionEdit edit = new DistributionEdit();
    edit.setUser(user);
    edit.setDistribution(distribution);
    edit.setStartedAt(time);

    return edit;
  }

  private DistributionEdit create(int hours, int minutes, int seconds) {
    Date time = getDate(hours, minutes, seconds);

    DistributionEdit edit = new DistributionEdit();
    edit.setUser(user);
    edit.setDistribution(distribution);
    edit.setStartedAt(time);

    return edit;
  }

  private Date getDate(int hours, int minutes, int seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2016, Calendar.JUNE, 23, hours, minutes, seconds);

    return calendar.getTime();
  }

}
