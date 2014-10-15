package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryProductTree {

    int program_id;
    int category_id;
    int product_id;
    String category;
    String product;
    String code;

    List<ProductCategoryProductTree> children = new ArrayList<ProductCategoryProductTree>();
}
