/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
var distributionModule = angular.module('distribution', ['openlmis', 'IndexedDB', 'ui.bootstrap.dialog', 'ui.bootstrap.modal']);

distributionModule.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/manage', {controller: DistributionController, templateUrl: 'partials/init.html', resolve: DistributionController.resolve}).
      when('/list', {controller: DistributionListController, templateUrl: 'partials/list.html'}).
      when('/view-load-amounts/:deliveryZoneId/:programId/:periodId', {controller: ViewLoadAmountController, templateUrl: 'partials/view-load-amount.html', resolve: ViewLoadAmountController.resolve}).
      when('/record-facility-data/:distribution', {templateUrl: 'partials/record-facility-data.html', resolve: RecordFacilityDataController.resolve}).
      when('/record-facility-data/:distribution/:facility/refrigerator-data', {controller: RefrigeratorController, templateUrl: 'partials/refrigerator.html', resolve: RefrigeratorController.resolve}).
      otherwise({redirectTo: '/manage'});

  }]).directive('notRecorded',function () {
    return {
      require: '?ngModel',
      link: function (scope, element, attrs) {
        distributionModule["notRecordedDirective"](element, scope);
      }
    };
  }).config(function (IndexedDBProvider) {
    IndexedDBProvider
      .setDbName("open_lmis")
      .migration(4, migrationFunc);
  });

distributionModule.notRecordedDirective = function (element, scope) {

  var model = $(document.getElementsByName(element.attr('id'))[0]).attr('ng-model');

  var evaluatedVariable = scope;
  var keyExists = true;

  $(model.split('.')).each(function (index, key) {
    if (!(key in evaluatedVariable)) {
      keyExists = false;
      return false;
    }
    evaluatedVariable = evaluatedVariable[key];
    return true;
  });

  if (evaluatedVariable === undefined && keyExists) {
    element.prop('checked', true);
    $.each(document.getElementsByName(element.attr('id')), function (index, ele) {
      ele.disabled = element.is(":checked");
    });

    if (!scope.$$phase) scope.$apply();
  }

  element.bind('click', function () {
    $.each(document.getElementsByName(element.attr('id')), function (index, ele) {
      ele.disabled = element.is(":checked");
      var evaluatedVar = scope;

      var ngModel = $(ele).attr('ng-model').split('.');
      $(ngModel).each(function (index, value) {
        if (index == ngModel.length - 1) {
          evaluatedVar[value] = undefined;
          return false;
        }
        evaluatedVar = evaluatedVar[value];
        return true;
      });
    });
    scope.$apply();
  });
};
