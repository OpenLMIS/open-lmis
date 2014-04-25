/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function OrderNumberConfigurationController($scope, $location, orderNumberConfiguration, OrderNumberConfiguration, messageService) {

  $scope.orderNumberConfiguration = orderNumberConfiguration;
  $scope.cancelOrderNumberConfigurationSave = function () {
    $scope.$parent.message = "";
    $location.path('configure-system-settings');
  };
  $scope.error = "";
  $scope.$parent.message = "";

  $scope.saveOrderNumberConfiguration = function () {
    OrderNumberConfiguration.save({}, $scope.orderNumberConfiguration, function (data) {
      $scope.$parent.message = messageService.get(data.success);
      $location.path('configure-system-settings');
    }, function (data) {
      $scope.error = data.error;
    });
  };

}

OrderNumberConfigurationController.resolve = {
  orderNumberConfiguration: function ($q, $timeout, OrderNumberConfiguration) {
    var deferred = $q.defer();
    $timeout(function () {
      OrderNumberConfiguration.get({}, function (data) {
        deferred.resolve(data.orderNumberConfiguration);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
