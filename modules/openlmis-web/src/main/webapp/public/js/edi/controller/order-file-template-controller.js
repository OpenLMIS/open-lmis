/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function OrderFileTemplateController($scope, orderFileTemplate, OrderFileTemplate, dateFormats, $location, messageService) {

  $scope.orderFileTemplate = orderFileTemplate;
  $scope.orderDateFormats = _.pluck(_.where(dateFormats, {"orderDate": true}), "format");
  $scope.periodDateFormats = _.pluck(dateFormats, "format");
  $scope.orderFileColumns = $scope.orderFileTemplate.orderFileColumns;
  $scope.newOrderFileColumn = {includeInOrderFile: true, dataFieldLabel: "label.not.applicable"};

  $scope.saveOrderFileTemplate = function () {
    updatePosition();
    OrderFileTemplate.save({}, $scope.orderFileTemplate, function (data) {
      $scope.$parent.message = messageService.get(data.success);
      $location.path('configure-system-settings');
    });
  };

  $scope.cancelEdiSave = function () {
    $scope.$parent.message = "";
    $location.path('configure-system-settings');
  };

  $scope.addNewOrderFileColumn = function () {
    $scope.newOrderFileColumn.openLmisField = false;
    $scope.newOrderFileColumn.position = $scope.orderFileColumns.length + 1;
    $scope.orderFileColumns.push($scope.newOrderFileColumn);
    $scope.newOrderFileColumn = {includeInOrderFile: true, dataFieldLabel: "label.not.applicable"};
    $("html, body").animate({ scrollTop: $(document).height() }, 300);
  };

  $scope.removeOrderFileColumn = function (index) {
    $scope.orderFileColumns.splice(index, 1);
    updatePosition();
  };

  function updatePosition() {
    $scope.orderFileColumns.forEach(function (orderFileColumn, index) {
      orderFileColumn.position = index + 1;
    });
  }
}

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

  dateFormats: function ($q, $timeout, DateFormats) {
    var deferred = $q.defer();
    $timeout(function () {
      DateFormats.get({}, function (data) {
        deferred.resolve(data.dateFormats);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
