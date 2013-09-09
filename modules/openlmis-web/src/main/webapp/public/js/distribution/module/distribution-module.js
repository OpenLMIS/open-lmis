/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
      when('/record-facility-data/:distribution/:facility/general-observation',
      {controller: GeneralObservationController, templateUrl: 'partials/general-observation.html', resolve: ResolveDistribution}).
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
          ele.disabled = ctrl.$modelValue;
        });
      }, 0);

      scope.$watch(attrs.ngModel, function () {
        $.each(document.getElementsByName(element.attr('id')), function (index, associatedElement) {
          associatedElement.disabled = ctrl.$modelValue;
          if (!isUndefined(attrs.notRecorded)) {
            scope[attrs.notRecorded](ctrl.$modelValue);
          }

          if(!ctrl.$modelValue) return;

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
