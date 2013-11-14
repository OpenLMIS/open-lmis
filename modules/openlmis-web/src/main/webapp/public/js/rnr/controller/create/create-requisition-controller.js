/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateRequisitionController($scope, requisition, pageSize, rnrColumns, lossesAndAdjustmentsTypes, facilityApprovedProducts, requisitionRights, regimenTemplate, $location, Requisitions, $routeParams, $dialog, messageService, requisitionService) {

  var NON_FULL_SUPPLY = 'nonFullSupply';
  var FULL_SUPPLY = 'fullSupply';

  $scope.pageSize = pageSize;
  $scope.rnr = new Rnr(requisition, rnrColumns);

  resetCostsIfNull();

  $scope.lossesAndAdjustmentTypes = lossesAndAdjustmentsTypes;
  $scope.facilityApprovedProducts = facilityApprovedProducts;

  $scope.visibleColumns = requisitionService.getMappedVisibleColumns(rnrColumns, RegularRnrLineItem.frozenColumns, ['quantityApproved']);

  $scope.programRnrColumnList = rnrColumns;
  $scope.requisitionRights = requisitionRights;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.visibleRegimenColumns = _.where($scope.regimenColumns, {'visible': true});
  $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name': 'quantityRequested'});
  $scope.errorPages = {fullSupply: [], nonFullSupply: []};
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;

  requisitionService.populateScope($scope, $location, $routeParams);
  resetFlags();

  if (!($scope.programRnrColumnList && $scope.programRnrColumnList.length > 0)) {
    $scope.error = "error.rnr.template.not.defined";
    $location.path("/init-rnr");
  }
  $scope.hasPermission = function (permission) {
    return _.find($scope.requisitionRights, function (right) {
      return right.right === permission;
    });
  };

  $scope.toggleSkipFlag = function(){
    _.each($scope.page.fullSupply, function(rnrLineItem){
      rnrLineItem.skipped = $scope.rnr.skipAll;
    });
  };

  $scope.formDisabled = function () {
    var status = $scope.rnr.status;
    if (status === 'INITIATED' && $scope.hasPermission('CREATE_REQUISITION')) return false;
    return !(status === 'SUBMITTED' && $scope.hasPermission('AUTHORIZE_REQUISITION'));
  }();

  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab === NON_FULL_SUPPLY ?
      _.contains($scope.errorPages.nonFullSupply, page) :
      $scope.visibleTab === FULL_SUPPLY ? _.contains($scope.errorPages.fullSupply, page) : [];
  };

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
    $scope.rnr.skipAll = false;
  });

  $scope.saveRnr = function (preventMessage) {
    if (!$scope.saveRnrForm || !$scope.saveRnrForm.$dirty) {
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
      $scope.saveRnrForm.$setPristine();
    }, function (data) {
      if (!preventMessage)
        $scope.error = data.data.error;
    });
  };

  function validateAndSetErrorClass() {
    $scope.inputClass = true;
    var fullSupplyError = $scope.rnr.validateFullSupply();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupply();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;

    if ($scope.rnr.regimenLineItems) validateRegimenLineItems();
    var regimenError;
    if ($scope.regimenLineItemInValid) {
      regimenError = messageService.get("error.rnr.validation");
    }
    return fullSupplyError || nonFullSupplyError || regimenError;
  }

  $scope.submitRnr = function () {
    resetFlags();
    requisitionService.resetErrorPages($scope);
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      requisitionService.setErrorPages($scope);
      $scope.submitError = errorMessage;
      return;
    }
    showConfirmModal();
  };

  function validateRegimenLineItems() {
    var setError = false;
    $.each($scope.rnr.regimenLineItems, function (index, regimenLineItem) {
      $.each($scope.visibleRegimenColumns, function (index, regimenColumn) {
        if (regimenColumn.name !== "remarks" && isUndefined(regimenLineItem[regimenColumn.name])) {
          setError = true;
          $scope.regimenLineItemInValid = true;
        }
      });
    });
    if (!setError) $scope.regimenLineItemInValid = false;
  }

  var submitValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "submit"},
      {}, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$scope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
        $scope.saveRnrForm.$setPristine();
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.callBack = function (result) {
    if (result && $scope.rnr.status === 'INITIATED') {
      submitValidatedRnr();
    }
    if (result && $scope.rnr.status === 'SUBMITTED') {
      authorizeValidatedRnr();
    }
  };

  var showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: messageService.get("label.confirm.action"),
      body: messageService.get("msg.question.confirmation")
    };
    OpenLmisDialog.newDialog(options, $scope.callBack, $dialog, messageService);
  };

  $scope.authorizeRnr = function () {
    resetFlags();
    requisitionService.resetErrorPages($scope);
    $scope.saveRnr(true);
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      requisitionService.setErrorPages($scope);
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
      $scope.saveRnrForm.$setPristine();
    }, function (data) {
      $scope.submitError = data.data.error;
    });
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarningBasedOnField = function (value, field) {
    if ($scope.inputClass && (isUndefined(value) || value === false) && field) {
      return "warning-error";
    }
    return null;
  };

  $scope.highlightWarning = function (value) {
    if ($scope.inputClass && (isUndefined(value) || value === false)) {
      return "warning-error";
    }
    return null;
  };

  $scope.getCellErrorClass = function (rnrLineItem) {
    return !!rnrLineItem.getErrorMessage() ? 'cell-error-highlight' : '';
  };

  $scope.lineItemErrorMessage = function (rnrLineItem) {
    return messageService.get(rnrLineItem.getErrorMessage());
  };

  $scope.getRowErrorClass = function (rnrLineItem) {
    return $scope.getCellErrorClass(rnrLineItem) ? 'row-error-highlight' : '';
  };

  function resetCostsIfNull() {
    var rnr = $scope.rnr;
    if (rnr === null) return;
    if (!rnr.fullSupplyItemsSubmittedCost) {
      rnr.fullSupplyItemsSubmittedCost = 0;
    }
    if (!rnr.nonFullSupplyItemsSubmittedCost) {
      rnr.nonFullSupplyItemsSubmittedCost = 0;
    }
  }

  $scope.$on('$routeUpdate', function () {
    requisitionService.refreshGrid($scope, $location, $routeParams, true);
  });

  requisitionService.refreshGrid($scope, $location, $routeParams, true);

  function resetFlags() {
    $scope.submitError = $scope.submitMessage = $scope.error = $scope.message = "";
  }

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id": $scope.rnr.id, "fullSupplyLineItems": [], "nonFullSupplyLineItems": [], "regimenLineItems": []};
    if (!$scope.page[$scope.visibleTab].length) return rnr;

    function transform(copyFrom) {
      return _.map(copyFrom, function (lineItem) {
        return _.omit(lineItem, ['rnr', 'programRnrColumnList']);
      });
    }

    //Who wrote this? This is awesome!!
    rnr[$scope.visibleTab + 'LineItems'] = transform($scope.page[$scope.visibleTab]);

    return rnr;
  }
}

CreateRequisitionController.resolve = {
  requisition: function ($q, $timeout, Requisitions, $route, $rootScope) {
    var deferred = $q.defer();
    $timeout(function () {
      var rnr = $rootScope.rnr;
      if (rnr) {
        deferred.resolve(rnr);
        $rootScope.rnr = undefined;
        return;
      }
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

  pageSize: function ($q, $timeout, LineItemPageSize) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemPageSize.get({}, function (data) {
        deferred.resolve(data.pageSize);
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

