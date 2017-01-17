/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RecordFacilityDataController($scope, $location, $route, $routeParams, distributionService, AuthorizationService, $dialog, $http, messageService, $timeout, IndexedDB) {
  $scope.label = $routeParams.facility ? 'label.change.facility' : "label.select.facility";

  $scope.distribution = distributionService.distribution;
  $scope.distributionReview = distributionService.distributionReview;
  $scope.geographicZones = _.sortBy(_.uniq(_.pluck($scope.distribution.facilityDistributions, 'geographicZone')), function(zone){
    return zone;
  });
  $scope.zoneFacilityMap = _.groupBy($scope.distribution.facilityDistributions, function (facility) {
    return facility.geographicZone;
  });
  $scope.facilitySelected = $scope.distribution.facilityDistributions[$routeParams.facility];

  $scope.hasPermission = AuthorizationService.hasPermission;

  $scope.format = function (dropDownObj) {
    if (dropDownObj.element[0].value) {
      var facilityId = utils.parseIntWithBaseTen(dropDownObj.element[0].value);
      return "<div class='" + $scope.distribution.facilityDistributions[facilityId].computeStatus($scope.distributionReview) + "'>" +
        "<span id=" + facilityId + " class='status-icon'></span>" + dropDownObj.text +
        "</div>";
    } else {
      return dropDownObj.text;
    }
  };

  $scope.chooseFacility = function () {
    if ($routeParams.facility != $scope.facilitySelected.facilityId) {
      if (distributionService.distributionReview) {
        distributionService.distributionReview.currentScreen = 'visit-info';
      }

      $location.path('record-facility-data/' + $routeParams.distribution + '/' + $scope.facilitySelected.facilityId + '/visit-info');
    }
  };

  function restore(bean) {
    var propertyName;
    var propertyValue;

    if (bean instanceof Refrigerators) {
      bean.restore();
      return;
    }

    for (propertyName in bean) {
      if (bean.hasOwnProperty(propertyName)) {
        propertyValue = bean[propertyName];

        if (propertyValue) {
            if (propertyValue.type === 'reading') {
              bean[propertyName].value = propertyValue.original.value;
              bean[propertyName].notRecorded = propertyValue.original.notRecorded;
            } else if (_.isObject(propertyValue)) {
              restore(propertyValue);
            }
        }
      }
    }
  }

  function syncCancelCallback(result) {
    if (result) {
      var screen = distributionService.distributionReview.currentScreen;
      var handler = $scope.facilitySelected.getByScreen(screen);

      restore(handler.bean);
      $scope.facilitySelected[handler.property] = handler.bean;

      $timeout(function () {
        IndexedDB.put('distributions', $scope.distribution);
        $route.reload();
      });
    } else {
      $scope.toggleEditMode();
    }
  }

  function same(a, b) {
    return a && b && a.value === b.value && a.notRecorded === b.notRecorded;
  }

  function modified(bean) {
    var propertyName;
    var propertyValue;

    if (bean instanceof Refrigerators) {
      return bean.modified();
    }

    for (propertyName in bean) {
      if (bean.hasOwnProperty(propertyName)) {
        propertyValue = bean[propertyName];

        if (propertyValue) {
            if (propertyValue.type === 'reading') {
              if (!same(propertyValue, propertyValue.original)) {
                return true;
              }
            } else if (_.isObject(propertyValue)) {
              var result = modified(propertyValue);

              if (result) {
                return true;
              }
            }
        }
      }
    }

    return false;
  }

  $scope.toggleEditMode = function () {
    if (distributionService.distributionReview) {
      distributionService.distributionReview.editMode[$routeParams.facility][distributionService.distributionReview.currentScreen] ^= true;
    }

    if (!distributionService.distributionReview.editMode[$routeParams.facility][distributionService.distributionReview.currentScreen]) {
      var screen = distributionService.distributionReview.currentScreen;
      var handler = $scope.facilitySelected.getByScreen(screen);

      if (modified(handler.bean)) {
        var dialogOpts = {
          id: 'distributionSyncCancel',
          header: 'label.distribution.sync',
          body: 'msg.sync.cancel'
        };

        OpenLmisDialog.newDialog(dialogOpts, syncCancelCallback, $dialog);
      } else {
        $route.reload();
      }
    } else {
      $route.reload();
    }
  };

  function onSuccess(data) {
    var results = data.results;

    if (results.conflict) {
      if (!$scope.syncResults.conflicts[results.facilityId]) {
        $scope.syncResults.conflicts[results.facilityId] = {};
      }

      $.each(results.details, function (ignore, elem) {
        if (!$scope.syncResults.conflicts[results.facilityId][elem.dataScreenUI]) {
          $scope.syncResults.conflicts[results.facilityId][elem.dataScreenUI] = [];
        }

        $scope.syncResults.conflicts[results.facilityId][elem.dataScreenUI].push(elem);
        $scope.syncResults.length += 1;
      });
    } else {
        $scope.message = messageService.get('msg.sync.success', results.distribution.deliveryZone.name, results.distribution.period.name);
    }

    $scope.distribution = results.distribution;
    distributionService.save(results.distribution);
    distributionService.distributionReview.editMode[$routeParams.facility][distributionService.distributionReview.currentScreen] ^= true;
    $route.reload();
  }

  function onError(data) {
    $scope.errorMessage = data.error;
  }

  function syncCallback(result) {
    var distributionId = $scope.distribution.id;
    var url = '/review-data/distribution/' + distributionId + '/sync.json';

    $scope.syncResults = {
      conflicts: {},
      length: 0
    };

    if (result) {
      $.each($scope.distribution.facilityDistributions, function (ignore, facilityDistribution) {
        $http.post(url, facilityDistribution).success(onSuccess).error(onError);
      });
    }
  }

  $scope.sync = function () {
    var dialogOpts = {
      id: 'distributionSync',
      header: 'label.distribution.sync',
      body: 'msg.sync.edit.data'
    };

    OpenLmisDialog.newDialog(dialogOpts, syncCallback, $dialog);
  };

  $scope.abandonAll = function () {
    $scope.syncResults = {
      conflicts: {},
      length: 0
    };
  };

  $scope.abandon = function (facility, dataScreenUI, detail) {
    var idx = $scope.syncResults.conflicts[facility][dataScreenUI].indexOf(detail);

    if (idx >= 0) {
      $scope.syncResults.length -= 1;
      $scope.syncResults.conflicts[facility][dataScreenUI].splice(idx, 1);
    }
  };

  function onSuccessForceSync(data) {
    $scope.distribution = data.distribution;
    distributionService.save(data.distribution);
  }

  function onErrorForceSync(data) {
    $scope.errorMessage = data.error;
  }

  function forceSync(facility, dataScreenUI, detail) {
    var distributionId = $scope.distribution.id;
    var url = '/review-data/distribution/' + distributionId + '/' + $routeParams.facility + '/force-sync.json';

    $http.post(url, detail).success(onSuccessForceSync).error(onErrorForceSync);
    $scope.abandon(facility, dataScreenUI, detail);
  }

  $scope.forceSyncAll = function () {
    $.each($scope.syncResults, function (facility, conflicts) {
      $.each(conflicts, function (dataScreenUI, details) {
        $.each(details, function (ignore, detail) {
          forceSync(facility, dataScreenUI, detail);
        });
      });
    });
  };

  $scope.forceSync = function (facility, dataScreenUI, detail) {
    forceSync(facility, dataScreenUI, detail);
  };

}
