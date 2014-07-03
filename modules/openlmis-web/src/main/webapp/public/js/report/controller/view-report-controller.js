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
  $scope.template = template;
  $scope.parameters = template.parameters;
  $scope.map = {};
  angular.forEach($scope.parameters, function (parameter) {
    $scope.map[parameter.name] = parameter.defaultValue;
  });
}

ViewReportController.resolve = {

  template: function ($q, $timeout, $route, ReportParameters) {
    var deferred = $q.defer();
    var templateId = $route.current.params.id;
    $timeout(function () {
      ReportParameters.get({id: templateId}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};