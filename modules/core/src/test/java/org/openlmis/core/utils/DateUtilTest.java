package org.openlmis.core.utils;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.utils.DateUtil.*;

public class DateUtilTest {


    @Test
    public void shouldParseDateWhenGivenFormat() throws Exception {

        Date date = parseDate("2015-10-10 12:00:00", FORMAT_DATE_TIME);
        assertThat(date.getYear(), is(2015 - 1900));
        assertThat(date.getDate(), is(10));

    }

    @Test
    public void shouldReturnNullWhenGivenNullDateValue() throws Exception {
        Date date = parseDate(null, FORMAT_DATE_TIME);
        assertThat(date, is(nullValue()));
    }

    @Test
    public void shouldReturnFormattedDateWhenGivenDate() throws Exception {
        String givenDate = "2015-10-10 12:00:00";
        Date date = parseDate(givenDate, FORMAT_DATE_TIME);

        String result = formatDate(date);
        assertThat(result, is(givenDate));
    }
}