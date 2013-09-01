/*
 * Copyright � 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

  function OrderFileTemplateController($scope, orderFileTemplate, OrderFileTemplate, dateFormats) {

  $scope.orderFileTemplate = orderFileTemplate;
  $scope.orderDateFormats = _.pluck(_.where(dateFormats, {"orderDate": true}), "format");
  $scope.periodDateFormats = _.pluck(dateFormats, "format");
  $scope.orderFileColumns = $scope.orderFileTemplate.orderFileColumns;
  $scope.newOrderFileColumn = {includeInOrderFile: true, dataFieldLabel: "label.not.applicable"};

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

  $scope.addNewOrderFileColumn = function () {
    $scope.newOrderFileColumn.openLmisField = false;
    $scope.newOrderFileColumn.position = $scope.orderFileColumns.length + 1;
    $scope.orderFileColumns.push($scope.newOrderFileColumn);
    $scope.newOrderFileColumn = {includeInOrderFile:true, dataFieldLabel: "label.not.applicable"};
    $("html, body").animate({ scrollTop: $(document).height() }, 300);
  };

  $scope.removeOrderFileColumn = function (index) {
    $scope.orderFileColumns.splice(index-1, 1);
    updatePosition();
  };

  function updatePosition() {
    $scope.orderFileColumns.forEach(function (orderFileColumn, index) {
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
  },

  dateFormats: function($q, $timeout, DateFormats) {
    var deferred = $q.defer();
    $timeout(function () {
      DateFormats.get({}, function (data) {
        deferred.resolve(data.dateFormats);
      }, {});
    }, 100);
    return deferred.promise;
  }
};