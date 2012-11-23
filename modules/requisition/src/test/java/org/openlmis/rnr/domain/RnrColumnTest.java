package org.openlmis.rnr.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RnrColumnTest {

    @Test
    public void shouldSetAvailableDataSources() {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setAvailableColumnTypesString(RnrColumnType.UserInput + "/" + RnrColumnType.Derived);
        assertEquals(2, rnrColumn.getAvailableColumnTypes().size());
    }

}
