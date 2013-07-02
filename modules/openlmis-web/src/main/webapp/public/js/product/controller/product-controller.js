/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SupplylineController($scope,  ProductList, $location) {

 // all supply lines   for list
    ProductList.get({}, function (data) {
        $scope.productslist = data.productList;

    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });



};
