package org.openlmis.rnr.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RnrColumnTest {

    @Test
    public void shouldSetAvailableDataSources() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.User_Input + "/" + RnrColumnType.Calculated);
        assertEquals(2, rnrColumn.getAvailableColumnTypes().size());
    }

    @Test
    public void shouldGetFirstAvailableSourceAsSelectedColumnTypeIfAvailableSizeIsOne() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.User_Input.toString());
        RnrColumnType columnType = rnrColumn.getSelectedColumnType();
        assertThat(columnType, is(RnrColumnType.User_Input));
    }

    @Test
    public void shouldGetSelectedColumnType() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.User_Input + "/" + RnrColumnType.Calculated);
        rnrColumn.setSelectedColumnType(RnrColumnType.Calculated);
        RnrColumnType columnType = rnrColumn.getSelectedColumnType();
        assertThat(columnType, is(RnrColumnType.Calculated));
    }
}
