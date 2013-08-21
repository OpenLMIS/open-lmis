/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

  function OrderFileTemplateController($scope, orderFileTemplate, OrderFileTemplate) {
  $scope.orderFileTemplate = orderFileTemplate;

  $scope.saveOrderFileTemplate = function () {
    updatePosition();
    OrderFileTemplate.save({}, $scope.orderFileTemplate, function (data) {
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $scope.message = '';
          });
        });
      }, 3000);
    });
  };

  function updatePosition() {
    $scope.orderFileTemplate.orderFileColumns.forEach(function (orderFileColumn, index) {
      orderFileColumn.position = index + 1;
    });
  };

};

OrderFileTemplateController.resolve = {
  orderFileTemplate: function ($q, $timeout, OrderFileTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      OrderFileTemplate.get({}, function (data) {
        deferred.resolve(data.orderFileTemplate);
      }, {});
    }, 100);
    return deferred.promise;
  }
};