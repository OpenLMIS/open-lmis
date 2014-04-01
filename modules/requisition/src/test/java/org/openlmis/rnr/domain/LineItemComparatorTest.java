package org.openlmis.rnr.domain;

import org.junit.Test;
import org.openlmis.rnr.builder.RnrLineItemBuilder;

import java.util.Collections;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;

public class LineItemComparatorTest {


  @Test
  public void shouldCompareTwoLineItemsOnBasisOfProductDisplayOrderIfCategoryIsSame() {
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(productCode, "PC1"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1),
      with(RnrLineItemBuilder.productDisplayOrder, 2)));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(productCode, "PC2"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1),
      with(RnrLineItemBuilder.productDisplayOrder, 1)));

    List<RnrLineItem> lineItemsList = asList(lineItem1, lineItem2);

    Collections.sort(lineItemsList, new LineItemComparator());

    assertThat(lineItemsList.get(0).getProductCode(), is("PC2"));
    assertThat(lineItemsList.get(1).getProductCode(), is("PC1"));

  }


  @Test
  public void shouldCompareTwoLineItemsOnBasisOfProductCodeIfDisplayOrderIsNotPresentAndCategoryIsSame(){
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(productCode, "PC3"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1)));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(productCode, "PC2"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1)));

    List<RnrLineItem> lineItemsList = asList(lineItem1, lineItem2);

    Collections.sort(lineItemsList, new LineItemComparator());

    assertThat(lineItemsList.get(0).getProductCode(), is("PC2"));
    assertThat(lineItemsList.get(1).getProductCode(), is("PC3"));

  }

  @Test
  public void shouldCompareTwoLineItemsOnBasisOfCategoryCodeIfCategoryDisplayOrderIsSame(){
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(productCode, "PC3"), with(productCategory, "C3"), with(productCategoryDisplayOrder, 1)));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(productCode, "PC2"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1)));

    List<RnrLineItem> lineItemsList = asList(lineItem1, lineItem2);

    Collections.sort(lineItemsList, new LineItemComparator());

    assertThat(lineItemsList.get(0).getProductCode(), is("PC2"));
    assertThat(lineItemsList.get(1).getProductCode(), is("PC3"));
  }

  @Test
  public void shouldCompareLineItemsFirstOnTheBasisOfProductCategory(){
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(productCode, "PC1"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1),
      with(RnrLineItemBuilder.productDisplayOrder, 2)));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(productCode, "PC2"), with(productCategory, "C1"), with(productCategoryDisplayOrder, 1),
      with(RnrLineItemBuilder.productDisplayOrder, 1)));
    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(productCode, "PC3"), with(productCategory, "C2"), with(productCategoryDisplayOrder, -1),
      with(RnrLineItemBuilder.productDisplayOrder, -1)));

    List<RnrLineItem> lineItemsList = asList(lineItem1, lineItem2, lineItem3);

    Collections.sort(lineItemsList, new LineItemComparator());

    assertThat(lineItemsList.get(0).getProductCode(), is("PC3"));
    assertThat(lineItemsList.get(1).getProductCode(), is("PC2"));
    assertThat(lineItemsList.get(2).getProductCode(), is("PC1"));
  }
}
