/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
var distributionModule = angular.module('distribution',
  ['openlmis', 'IndexedDB', 'ui.bootstrap.dialog', 'ui.bootstrap.modal']);

distributionModule.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/manage',
      {controller: DistributionController, templateUrl: 'partials/init.html', resolve: DistributionController.resolve}).
      when('/list', {controller: DistributionListController, templateUrl: 'partials/list.html'}).
      when('/view-load-amounts/:deliveryZoneId/:programId/:periodId',
      {controller: ViewLoadAmountController, templateUrl: 'partials/view-load-amount.html', resolve: ViewLoadAmountController.resolve}).
      when('/record-facility-data/:distribution',
      {templateUrl: 'partials/record-facility-data.html', resolve: ResolveDistribution}).
      when('/record-facility-data/:distribution/:facility/refrigerator-data',
      {controller: RefrigeratorController, templateUrl: 'partials/refrigerator.html', resolve: ResolveDistribution}).
      when('/record-facility-data/:distribution/:facility/epi-use',
      {controller: EPIUseController, templateUrl: 'partials/epi-use.html', resolve: ResolveDistribution}).
      when('/record-facility-data/:distribution/:facility/epi-inventory',
      {controller: EPIInventoryController, templateUrl: 'partials/epi-inventory.html', resolve: ResolveDistribution}).
      when('/record-facility-data/:distribution/:facility/coverage',
      {controller: CoverageController, templateUrl: 'partials/coverage.html', resolve: ResolveDistribution}).
      when('/record-facility-data/:distribution/:facility/facility-visit',
      {controller: FacilityVisitController, templateUrl: 'partials/facility-visit.html', resolve: ResolveDistribution}).
      otherwise({redirectTo: '/manage'});

  }]).config(function (IndexedDBProvider) {
    IndexedDBProvider
      .setDbName("open_lmis")
      .migration(4, migrationFunc);
  });

distributionModule.directive('notRecorded', function ($timeout) {
  return {
    require: '?ngModel',
    link: function (scope, element, attrs, ctrl) {
      $timeout(function () {
        $.each(document.getElementsByName(element.attr('id')), function (index, ele) {
          ele.disabled = element.attr('disabled') || ctrl.$modelValue;
        });
      }, 0);

      scope.$watch(attrs.ngModel, function () {
        $.each(document.getElementsByName(element.attr('id')), function (index, associatedElement) {
          associatedElement.disabled = ctrl.$modelValue;
          if (!isUndefined(attrs.notRecorded)) {
            scope[attrs.notRecorded](ctrl.$modelValue);
          }

          if (!ctrl.$modelValue) return;

          var evaluatedVar = scope;
          var ngModel = $(associatedElement).attr('ng-model').split('.');
          $(ngModel).each(function (index, value) {
            if (index == ngModel.length - 1) {
              evaluatedVar[value] = undefined;
              return false;
            }
            evaluatedVar = evaluatedVar[value];
            return true;
          });
        });
      });
    }
  };
});


distributionModule.directive('disableForm', function ($timeout) {
  return {
    require: '?ngModel',
    link: function (scope, element) {
      $timeout(function () {
        if (element.attr('disable-form') === "true") {
          element.find('input, textarea').attr('disabled', 'disabled');
        }
      });
    }
  };
});