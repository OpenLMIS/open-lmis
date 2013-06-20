/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

directives.directive('focusTableInputElements', function() {
  return {
    restrict: 'EA',
    link: function (scope, element, attr, ctrl) {
      var showProductCode = $("<div class='show-product-code'></div>");
      var showCode = $("<div class='code'>nsabd</div>");
      var showProduct = $("<div class='product'>mdsand</div>");
      showProductCode.hide();

      setTimeout(function(){
        showProductCode.append(showCode).append(showProduct);
        element.append(showProductCode);
        var inputElements = element.find("input");
        $.each(inputElements, function(index,value){
          $(inputElements[index]).focus(function() {

            parentRow = $(inputElements[index]).parents('tr');
            productCodeElement = $(parentRow.find('td')[0]);
            product = $(parentRow.find('td')[1]);

            showCode.text(productCodeElement.text());
            showProduct.text(product.text());

            showProductCode.css('left', parentRow.offset().left + element.scrollLeft());
            showProductCode.css('top', parentRow.offset().top + 1);

            showCode.css('height', parseInt(parentRow.css('height'))-19 +"px");
            showProduct.css('height', parseInt(parentRow.css('height'))-19 +"px");
            showCode.css('width', productCodeElement.css('width'));
            showProduct.css('width', parseInt(product.css('width'))-23 +"px");
            showCode.show();
            showProduct.show();
            showProductCode.show();
            if ($(this).offset().left < 429) {
              element.scrollLeft(element.scrollLeft() - 429 + $(this).offset().left);
            }
          });
          $(inputElements[index]).blur(function(){
            showProductCode.hide();
          });
        });
      });
    }
  };
});



//directives.directive('focusTableInputElements', function() {
//  return {
//    restrict: 'EA',
//    link: function (scope, element, attr, ctrl) {
//      var showProductCode = $("<div class='show-product-code'></div>");
//      showProductCode.hide();
//
//      setTimeout(function(){
//        element.append(showProductCode);
//        var inputElements = element.find("input");
//        $.each(inputElements, function(index,value){
//          $(inputElements[index]).focus(function() {
//            showProductCode.text(element.find("#productCode_"+Math.floor(index/9)).text());
//            showProductCode.css('left', $(inputElements[index]).parents('tr').offset().left + element.scrollLeft());
//            showProductCode.css('top', $(inputElements[index]).parents('tr').offset().top + 1);
//            showProductCode.css('height', parseInt($(inputElements[index]).parents('tr').css('height'))-19 +"px");
//            showProductCode.show();
//            if ($(this).offset().left < 120) {
//              element.scrollLeft(element.scrollLeft() - 120 + $(this).offset().left);
//            }
//          });
//          $(inputElements[index]).blur(function(){
//            showProductCode.hide();
//          });
//        });
//      });
//    }
//  };
//});
