/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function CreateVaccineReportController($scope, $location, $filter, $dialog, report, discardingReasons, VaccineReportSave, VaccineReportSubmit) {

  // initial state of the display
  $scope.report = report;
  $scope.discardingReasons = discardingReasons;

  //prepare tab visibility settings
  $scope.tabVisibility = {};
  _.chain(report.tabVisibilitySettings).groupBy('tab').map(function(key, value){
                                                                                  $scope.tabVisibility[value] =  key[0].visible;
                                                                                  });

  $scope.save = function () {
    VaccineReportSave.update($scope.report, function () {
      $scope.message = "msg.ivd.saved.successfully";
    });
  };

  $scope.submit = function () {
    var callBack = function (result) {
      if (result) {
        VaccineReportSubmit.update($scope.report, function () {
          $scope.message = "msg.ivd.submitted.successfully";
          $location.path('/');
        });
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.submit.action",
      body: "msg.question.submit.ivd.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.cancel = function () {
    $location.path('/');
  };


  $scope.showAdverseEffect = function (effect, editMode) {
    effect.date = $filter('date')(new Date(effect.date), 'yyyy-MM-dd');
    effect.expiry = $filter('date')(new Date(effect.expiry), 'yyyy-MM-dd');

    $scope.currentEffect = effect;
    $scope.currentEffectMode = editMode;

    $scope.adverseEffectModal = true;
  };

  $scope.applyAdverseEffect = function () {
    var product = _.findWhere($scope.report.logisticsLineItems, {'productId': parseInt($scope.currentEffect.productId, 10)});
    $scope.currentEffect.productName = product.productName;
    if (!$scope.currentEffectMode) {
      $scope.report.adverseEffectLineItems.push($scope.currentEffect);
    }
    $scope.adverseEffectModal = false;
  };

  $scope.closeAdverseEffectsModal = function () {
    $scope.adverseEffectModal = false;
  };

  $scope.showCampaignForm = function (campaign, editMode) {

    campaign.startDate = $filter('date')(new Date(campaign.startDate), 'yyyy-MM-dd');
    campaign.endDate = $filter('date')(new Date(campaign.endDate), 'yyyy-MM-dd');

    $scope.currentCampaign = campaign;
    $scope.currentCampaignMode = editMode;

    $scope.campaignsModal = true;
  };

  $scope.deleteAdverseEffectLineItem = function (lineItem) {

    var callBack = function (result) {
      if (result) {
        $scope.report.adverseEffectLineItems = _.without($scope.report.adverseEffectLineItems, lineItem);
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.delete.adverse.effect.action",
      body: "msg.question.delete.adverse.effect.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.applyCampaign = function () {
    if (!$scope.currentCampaignMode) {
      $scope.report.campaignLineItems.push($scope.currentCampaign);
    }
    $scope.campaignsModal = false;
  };

  $scope.closeCampaign = function () {
    $scope.campaignsModal = false;
  };

  $scope.toNumber = function (val) {
    if (angular.isDefined(val) && val !== null) {
      return parseInt(val, 10);
    }
    return 0;
  };


  $scope.rowRequiresExplanation = function (product) {
    if (!isUndefined(product.discardingReasonId)) {
      var reason = _.findWhere($scope.discardingReasons, {id: parseInt(product.discardingReasonId, 10)});
      return reason.requiresExplanation;
    }
    return false;
  };

}
CreateVaccineReportController.resolve = {

  report: function ($q, $timeout, $route, VaccineReport) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineReport.get({id: $route.current.params.id}, function (data) {
        data.report.coverageLineItemViews = _.groupBy(data.report.coverageLineItems, 'productId');
        deferred.resolve(data.report);
      });
    }, 100);
    return deferred.promise;
  },
  discardingReasons: function ($q, $timeout, $route, VaccineDiscardingReasons) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineDiscardingReasons.get(function (data) {
        deferred.resolve(data.reasons);
      });
    }, 100);
    return deferred.promise;
  }
};
