package org.openlmis.rnr.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RnrColumnTest {

    @Test
    public void shouldSetAvailableDataSources() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.UserInput + "/" + RnrColumnType.Derived);
        assertEquals(2, rnrColumn.getAvailableColumnTypes().size());
    }

    @Test
    public void shouldGetFirstAvailableSourceAsSelectedColumnTypeIfAvailableSizeIsOne() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.UserInput.toString());
        RnrColumnType columnType = rnrColumn.getSelectedColumnType();
        assertThat(columnType, is(RnrColumnType.UserInput));
    }

    @Test
    public void shouldGetSelectedColumnType() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.UserInput + "/" + RnrColumnType.Derived);
        rnrColumn.setSelectedColumnType(RnrColumnType.Derived);
        RnrColumnType columnType = rnrColumn.getSelectedColumnType();
        assertThat(columnType, is(RnrColumnType.Derived));
    }
}
