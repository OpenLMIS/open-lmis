package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

public class RnrColumnBuilder {


  public static final Property<RnrColumn, String> columnName = newProperty();
  public static final Property<RnrColumn, Boolean> visible = newProperty();
  public static final Property<RnrColumn, RnRColumnSource> source = newProperty();
  public static final Property<RnrColumn, LossesAndAdjustments> lossesAndAdjustments = newProperty();

  public static final String DEFAULT_NAME = "stockInHand";
  public static final Boolean DEFAULT_VISIBLE = Boolean.TRUE;
  public static final Instantiator<RnrColumn> defaultRnrColumn = new Instantiator<RnrColumn>() {

    @Override
    public RnrColumn instantiate(PropertyLookup<RnrColumn> lookup) {
      RnrColumn rnrColumn = new RnrColumn();
      rnrColumn.setName(lookup.valueOf(columnName, DEFAULT_NAME));
      rnrColumn.setVisible(lookup.valueOf(visible, DEFAULT_VISIBLE));
      rnrColumn.setSource(lookup.valueOf(source, USER_INPUT));
      return rnrColumn;
    }
  };
}
