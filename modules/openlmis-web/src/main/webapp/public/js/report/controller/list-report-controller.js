/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ListReportController($scope, reportTemplates) {
  $scope.reportTemplates = reportTemplates.reportTemplateList;
}

ListReportController.resolve = {
  reportTemplates:function ($q, $timeout, ReportTemplates) {
    var deferred = $q.defer();
    $timeout(function () {
      ReportTemplates.get({}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  }
}