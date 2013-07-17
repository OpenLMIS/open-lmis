/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ApproveRnrController($scope, requisition, Requisitions, rnrColumns, regimenTemplate, $location, currency, $routeParams, $dialog, $rootScope, messageService) {
  $scope.visibleTab = $routeParams.supplyType;
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.regimenColumns : [];
  $scope.currency = currency;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.error = "";
  $scope.message = "";
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;

  $scope.pageLineItems = [];
  $scope.errorPages = {};
  $scope.shownErrorPages = [];

  var NON_FULL_SUPPLY = 'non-full-supply';
  var FULL_SUPPLY = 'full-supply';
  var REGIMEN = 'regimen';

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  $scope.getFullScreen = function () {
    $rootScope.fullScreen = !$rootScope.fullScreen;
    angular.element(window).scrollTop(0);
  }

  $scope.highlightRequired = function (value) {
    if ($scope.approvedQuantityRequiredFlag && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index - 1].productCategory));
  };

  function updateShownErrorPages() {
    $scope.shownErrorPages = $scope.visibleTab ? $scope.errorPages.nonFullSupply : $scope.errorPages.fullSupply;
    $scope.errorPagesCount = !isUndefined($scope.shownErrorPages) ? $scope.shownErrorPages.length : null;
  }

  function fillPageData() {
    updateShownErrorPages();
    var pageLineItems = $scope.visibleTab == NON_FULL_SUPPLY ? $scope.rnr.nonFullSupplyLineItems : $scope.visibleTab == FULL_SUPPLY ? $scope.rnr.fullSupplyLineItems : [];
    $scope.numberOfPages = Math.ceil(pageLineItems.length / $scope.pageSize) ? Math.ceil(pageLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = pageLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  fillPageData();

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    $scope.visibleTab = supplyType;
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    $scope.visibleTab = $routeParams.supplyType == NON_FULL_SUPPLY ? NON_FULL_SUPPLY : ($routeParams.supplyType == REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;
    $location.search('supplyType', $scope.visibleTab);

    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    if ($scope.approvalForm.$dirty) $scope.saveRnr();
    fillPageData();
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    if ($scope.visibleTab != REGIMEN) {
      if (!$scope.pageLineItems[0].fullSupply) {
        rnr.nonFullSupplyLineItems = _.map($scope.pageLineItems, function (rnrLineItem) {
          return rnrLineItem.reduceForApproval()
        });
      } else {
        rnr.fullSupplyLineItems = _.map($scope.pageLineItems, function (rnrLineItem) {
          return rnrLineItem.reduceForApproval()
        });
      }
    }
    return rnr;
  }

  var fadeSaveMessage = function () {
    $scope.$apply(function () {
      angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
        $scope.message = '';
      });
    });
  };

  $scope.saveRnr = function (preventMessage) {
    if (!$scope.approvalForm.$dirty) {
      return;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"},
      rnr, function (data) {
        if (preventMessage == true) return;
        $scope.message = data.success;
        $scope.error = "";
        setTimeout(fadeSaveMessage, 3000);
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
    $scope.approvalForm.$setPristine();
  };

  function validateAndSetErrorClass() {
    var fullSupplyError = $scope.rnr.validateFullSupplyForApproval();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupplyForApproval();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;

    return fullSupplyError || nonFullSupplyError;
  }

  function setErrorPages() {
    $scope.errorPages = $scope.rnr.getErrorPages($scope.pageSize);
    updateShownErrorPages();
  }

  function resetErrorPages() {
    $scope.errorPages = {fullSupply: [], nonFullSupply: []};
    updateShownErrorPages();
  }

  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab == NON_FULL_SUPPLY ? _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  $scope.dialogCloseCallback = function (result) {
    if (result) {
      approveValidatedRnr();
    }
  };

  showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: messageService.get("label.confirm.action"),
      body: messageService.get("msg.question.confirmation")
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog, messageService);
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = true;
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var error = validateAndSetErrorClass();
    if (error) {
      setErrorPages();
      $scope.error = error;
      $scope.message = '';
      return;
    }
    showConfirmModal();
  };

  function resetFlags() {
    $scope.error = "";
    $scope.message = "";
  }

  var approveValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "approve"}, {}, function (data) {
      $scope.$parent.message = data.success;
      $scope.error = "";
      $location.path("rnr-for-approval");
    }, function (data) {
      $scope.error = data.error;
      $scope.message = "";
    });
  };

}

ApproveRnrController.resolve = {

  requisition: function ($q, $timeout, Requisitions, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      Requisitions.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns: function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  currency: function ($q, $timeout, ReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      ReferenceData.get({}, function (data) {
        deferred.resolve(data.currency);
      }, {});
    }, 100);
    return deferred.promise;
  },

  regimenTemplate: function ($q, $timeout, $route, ProgramRegimenTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRegimenTemplate.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

