/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ApproveRnrController($scope, requisition, Requisitions, rnrColumns, $location, currency, $routeParams, $dialog) {
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.currency = currency;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.error = "";
  $scope.message = "";

  $scope.pageLineItems = [];
  $scope.errorPages = {};
  $scope.shownErrorPages = [];
  var isConfirmed = false;

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  function updateSupplyType() {
    $scope.showNonFullSupply = !!($routeParams.supplyType == 'non-full-supply');
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
    $scope.shownErrorPages = $scope.showNonFullSupply ? $scope.errorPages.nonFullSupply : $scope.errorPages.fullSupply;
  }

  function fillPageData() {
    updateShownErrorPages();
    var pageLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(pageLineItems.length / $scope.pageSize) ? Math.ceil(pageLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = pageLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  updateSupplyType();
  fillPageData();


  $scope.$watch("currentPage", function () {
    if (!$routeParams.supplyType) $location.search('supplyType', 'full-supply');
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    if ($scope.approvalForm.$dirty) $scope.saveRnr();
    updateSupplyType();
    fillPageData();
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };


  function removeExtraDataForPostFromRnr() {
    return $scope.rnr.reduceForApproval();
  }

  var fadeSaveMessage = function () {
    $scope.$apply(function () {
      angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
        $scope.message = '';
      });
    });
  };

  $scope.saveRnr = function (preventMessage) {
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
    $scope.approvalForm.$dirty = false;
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
    return $scope.showNonFullSupply ? _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  $scope.dialogCloseCallback = function (result) {
    if(result) {
      approveValidatedRnr();
    }
  };

  showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: "Confirm Action",
      body: "Are you sure? Please confirm."
    };
    OpenLmisDialog.new(options, $scope.dialogCloseCallback, $dialog);
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = true;
    resetFlags();
    resetErrorPages();
    var error = validateAndSetErrorClass();
    if (error) {
      setErrorPages();
      $scope.saveRnr(true);
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
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "approve"},
      rnr, function (data) {
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

  requisition: function ($q, $timeout, RequisitionForApprovalById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApprovalById.get({id: $route.current.params.rnr}, function (data) {
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
  }
};

