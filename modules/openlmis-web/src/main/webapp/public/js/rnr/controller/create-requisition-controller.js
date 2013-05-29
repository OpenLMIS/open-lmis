/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateRequisitionController($scope, requisition, currency, rnrColumns, lossesAndAdjustmentsTypes, facilityApprovedProducts, requisitionRights, RequisitionComment, $location, Requisitions, $routeParams, $rootScope, $dialog) {
  $scope.showNonFullSupply = $routeParams.supplyType == 'non-full-supply';
  $scope.baseUrl = "/create-rnr/" + $routeParams.rnr + '/' + $routeParams.facility + '/' + $routeParams.program;
  $scope.fullSupplyLink = $scope.baseUrl + "?supplyType=full-supply&page=1";
  $scope.nonFullSupplyLink = $scope.baseUrl + "?supplyTpe=non-full-supply&page=1";

  $scope.rnr = requisition;
  $scope.allTypes = lossesAndAdjustmentsTypes;
  $scope.facilityApprovedProducts = facilityApprovedProducts;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.programRnrColumnList = rnrColumns;
  $scope.requisitionRights = requisitionRights;
  $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name': 'quantityRequested'});
  $scope.errorPages = {fullSupply: [], nonFullSupply: []};

  $scope.fillPagedGridData = function () {
    var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize) ? Math.ceil(gridLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  };


  $scope.hasPermission = function (permission) {
    return _.find($scope.requisitionRights, function (right) {
      return right.right == permission
    });
  };

  prepareRnr();

  $scope.currency = currency;

  $scope.checkErrorOnPage = function (page) {
    return $scope.showNonFullSupply ? _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  if ($scope.programRnrColumnList && $scope.programRnrColumnList.length > 0) {
  } else {
    $scope.error = "rnr.template.not.defined.error";
    $location.path("/init-rnr");
  }

  $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

  $scope.switchSupplyType = function (supplyType) {
    $scope.showNonFullSupply = supplyType == 'non-full-supply';
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.goToPage = function (page, event) {
    angular.element(event.target).parents(".dropdown").click();
    $location.search('page', page);
  };

  $scope.$on('$routeUpdate', function () {
    $scope.showNonFullSupply = $routeParams.supplyType == "non-full-supply";
    if ($scope.saveRnrForm.$dirty) $scope.saveRnr();
    $scope.fillPagedGridData();
  });

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.saveRnr = function (preventMessage) {
    if (!$scope.saveRnrForm.$dirty) {
      return;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"}, rnr, function (data) {
      if (preventMessage) return;
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $scope.message = '';
          });
        });
      }, 3000);
      $scope.saveRnrForm.$dirty = false;
    }, function (data) {
      $scope.error = data.error;
    });
  };

  function validateAndSetErrorClass() {
    $scope.inputClass = true;
    var fullSupplyError = $scope.rnr.validateFullSupply();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupply();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;

    return fullSupplyError || nonFullSupplyError;
  }

  function setErrorPages() {
    $scope.errorPages = $scope.rnr.getErrorPages($scope.pageSize);
  }

  $scope.submitRnr = function () {
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      setErrorPages();
      $scope.submitError = errorMessage;
      return;
    }
    showConfirmModal();
  };

  var submitValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "submit"},
      {}, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$scope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.dialogCloseCallback = function (result) {
    if (result && $scope.rnr.status == 'INITIATED')
      submitValidatedRnr();
    if (result && $scope.rnr.status == 'SUBMITTED')
      authorizeValidatedRnr();
  };

  showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: "Confirm Action",
      body: "Are you sure? Please confirm."
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog);
  };

  $scope.authorizeRnr = function () {
    resetFlags();
    resetErrorPages();
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      setErrorPages();
      $scope.submitError = errorMessage;
      return;
    }
    showConfirmModal();
  };

  var authorizeValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "authorize"}, {}, function (data) {
      resetFlags();
      $scope.rnr.status = "AUTHORIZED";
      $scope.formDisabled = true;
      $scope.submitMessage = data.success;
    }, function (data) {
      $scope.submitError = data.data.error;
    });
  };

  $scope.hide = function () {
    return "";
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarningBasedOnField = function (value, field) {
    if ($scope.inputClass && (isUndefined(value) || value == false) && field) {
      return "warning-error";
    }
    return null;
  };

  $scope.highlightWarning = function (value) {
    if ($scope.inputClass && (isUndefined(value) || value == false)) {
      return "warning-error";
    }
    return null;
  };

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index - 1].productCategory));
  };

  $scope.getCellErrorClass = function (rnrLineItem) {
    return (typeof(rnrLineItem.getErrorMessage) != "undefined" && rnrLineItem.getErrorMessage()) ? 'cell-error-highlight' : '';
  };

  $scope.getRowErrorClass = function (rnrLineItem) {
    return $scope.getCellErrorClass(rnrLineItem) ? 'row-error-highlight' : '';
  };

  function resetCostsIfNull() {
    var rnr = $scope.rnr;
    if (rnr == null) return;
    if (!rnr.fullSupplyItemsSubmittedCost)
      rnr.fullSupplyItemsSubmittedCost = 0;
    if (!rnr.nonFullSupplyItemsSubmittedCost)
      rnr.nonFullSupplyItemsSubmittedCost = 0;
  }

  function prepareRnr() {
    var rnr = $scope.rnr;
    $scope.rnr = new Rnr(rnr, rnrColumns);

    resetCostsIfNull();
    $scope.fillPagedGridData();
    $scope.formDisabled = (function () {
      if ($scope.rnr) {
        var status = $scope.rnr.status;
        if (status == 'INITIATED' && $scope.hasPermission('CREATE_REQUISITION')) return false;
        if (status == 'SUBMITTED' && $scope.hasPermission('AUTHORIZE_REQUISITION')) return false;
      }
      return true;
    })();
  }

  function resetErrorPages() {
    $scope.errorPages = {fullSupply: [], nonFullSupply: []};
  }

  function resetFlags() {
    $scope.submitError = "";
    $rootScope.submitMessage = "";
    $scope.error = "";
    $scope.message = "";
  }

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id": $scope.rnr.id, "fullSupplyLineItems": [], "nonFullSupplyLineItems": []};

    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.pageLineItems, function (lineItem) {
        rnr.fullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
      });

    return rnr;
  }
}

CreateRequisitionController.resolve = {
  requisition: function ($q, $timeout, RequisitionById, $route, $rootScope) {
    var deferred = $q.defer();
    $timeout(function () {
      var rnr = $rootScope.rnr;
      if (rnr) {
        deferred.resolve(rnr);
        $rootScope.rnr = undefined;
        return;
      }
      RequisitionById.get({id: $route.current.params.rnr}, function (data) {
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

  lossesAndAdjustmentsTypes: function ($q, $timeout, LossesAndAdjustmentsReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      LossesAndAdjustmentsReferenceData.get({}, function (data) {
        deferred.resolve(data.lossAdjustmentTypes);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facilityApprovedProducts: function ($q, $timeout, $route, FacilityApprovedProducts) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityApprovedProducts.get({facilityId: $route.current.params.facility, programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.nonFullSupplyProducts);
      }, {});
    }, 100);
    return deferred.promise;
  },

  requisitionRights: function ($q, $timeout, $route, FacilityProgramRights) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityProgramRights.get({facilityId: $route.current.params.facility, programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rights);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

