/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewReportController($scope, template) {
  $scope.parameterMap = {};
  $scope.template = template;
  $scope.parameters = template.parameters;
  $scope.validDataTypes = ["java.lang.Integer", "java.lang.Short", "java.lang.Long", "java.lang.Boolean", "java.lang.String",
                           "java.util.Date", "java.lang.Float", "java.lang.Double", "java.math.BigDecimal"];

  angular.forEach($scope.parameters, function (parameter) {
    $scope.parameterMap[parameter.name] = parameter.defaultValue;
  });

  $scope.refreshParams = function () {
    $scope.params = "";
    angular.forEach($scope.parameters, function (parameter) {
      $scope.params = $scope.params + parameter.name + "=" + $scope.parameterMap[parameter.name] + "&&";
    });
  };

  $scope.refreshParams();

  $scope.isInvalid = function(dataType) {
    return _.indexOf($scope.validDataTypes, dataType) === -1;
  };
}

ViewReportController.resolve = {

  template: function ($q, $timeout, $route, Reports) {
    var deferred = $q.defer();
    var id = $route.current.params.id;
    $timeout(function () {
      Reports.get({id: id}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};