/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateRequisitionController($scope, requisitionData, comments , pageSize, rnrColumns, lossesAndAdjustmentsTypes, facilityApprovedProducts, requisitionRights, equipmentOperationalStatus , regimenTemplate, $location, DeleteRequisition, SkipRequisition,Requisitions, $routeParams, $dialog, requisitionService, $q) {

  var NON_FULL_SUPPLY = 'nonFullSupply';
  var FULL_SUPPLY = 'fullSupply';
  var REGIMEN = 'regimen';
  var EQUIPMENT = 'equipment';


  $scope.pageSize = pageSize;
  $scope.rnr = new Rnr(requisitionData.rnr, rnrColumns, requisitionData.numberOfMonths, equipmentOperationalStatus);
  $scope.rnrComments = comments;

  $scope.deleteRnR = function( ){

    var callBack = function (result) {
      if (result) {
        DeleteRequisition.post({id: $scope.rnr.id}, function(data){
          OpenLmisDialog.newDialog({
                                      id: "confirmDialog",
                                      header: "label.confirm.action",
                                      body: data.success
                                    }, function(){
            $location.url('/public/pages/logistics/rnr/index.html#/init-rnr');
          }, $dialog);
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "label.rnr.confirm.delete"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);

  };

  $scope.skipRnR = function( ){

    var callBack = function (result) {
      if (result) {
        SkipRequisition.post({id: $scope.rnr.id}, function(){
          OpenLmisDialog.newDialog({
            id: "confirmDialog",
            header: "label.confirm.action",
            body: 'msg.rnr.skipped'
          }, function(){
            $location.url('/public/pages/logistics/rnr/index.html#/init-rnr');
          }, $dialog);
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "label.rnr.confirm.skip"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);

  };


  resetCostsIfNull();

  $scope.lossesAndAdjustmentTypes = lossesAndAdjustmentsTypes;
  $scope.facilityApprovedProducts = facilityApprovedProducts;
  $scope.equipmentOperationalStatus = equipmentOperationalStatus;

  $scope.visibleColumns = requisitionService.getMappedVisibleColumns(rnrColumns, RegularRnrLineItem.frozenColumns,
      ['quantityApproved','remarks']);

  $scope.programRnrColumnList = rnrColumns;
  $scope.requisitionRights = requisitionRights;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.visibleRegimenColumns = _.where($scope.regimenColumns, {'visible': true});
  $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name': 'quantityRequested'});
  $scope.errorPages = {fullSupply: [], nonFullSupply: [], regimen: [], equipment: []};
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;
  $scope.equipmentCount = $scope.rnr.equipmentLineItems.length;

  requisitionService.populateScope($scope, $location, $routeParams);
  resetFlags();

  if (!($scope.programRnrColumnList && $scope.programRnrColumnList.length > 0)) {
    $scope.error = "error.rnr.template.not.defined";
    $location.path("/init-rnr");
  }
  $scope.hasPermission = function (permission) {
    return _.find($scope.requisitionRights, function (right) {
      return right.name === permission;
    });
  };

  $scope.toggleSkipFlag = function () {
    _.each($scope.page.fullSupply, function (rnrLineItem) {
      if(rnrLineItem.canSkip()){
        rnrLineItem.skipped = $scope.rnr.skipAll;
      }
    });
    $scope.rnr.calculateFullSupplyItemsSubmittedCost();
  };

  $scope.formDisabled = function () {
    var status = $scope.rnr.status;
    if (status === 'INITIATED' && $scope.hasPermission('CREATE_REQUISITION')) return false;
    return !(status === 'SUBMITTED' && $scope.hasPermission('AUTHORIZE_REQUISITION'));
  }();

  $scope.setSkipAll = function (skipAllFlag) {
    if (!$scope.formDisabled) {
      $scope.saveRnrForm.$dirty = true;
      $scope.rnr.skipAll = skipAllFlag;
      $scope.toggleSkipFlag();
    }
  };

  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab === NON_FULL_SUPPLY ?
        _.contains($scope.errorPages.nonFullSupply, page) :
        $scope.visibleTab === FULL_SUPPLY ? _.contains($scope.errorPages.fullSupply, page) :
        $scope.visibleTab === REGIMEN ? _.contains($scope.errorPages.regimen, page) :
        $scope.visibleTab === EQUIPMENT ? _.contains($scope.errorPages.equipment, page) : [];
  };

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
    $scope.rnr.skipAll = false;
  });

  $scope.saveRnr = function (preventMessage) {
    var deferred = $q.defer();
    if (!$scope.saveRnrForm || !$scope.saveRnrForm.$dirty) {
      deferred.resolve();
      return deferred.promise;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"}, rnr, function (data) {
      deferred.resolve();
      if (preventMessage) return;
      $scope.message = data.success;
      $scope.saveRnrForm.$setPristine();
    }, function (data) {
      deferred.reject();
      if (!preventMessage) {
        $scope.error = data.data.error;
      }
    });

    return deferred.promise;
  };

  function promoteRnr(promoteFunction) {
    resetFlags();
    requisitionService.resetErrorPages($scope);

    var saveRnrPromise = $scope.saveRnr(true);

    saveRnrPromise.then(function () {
      if (!setError()) {
        confirm(promoteFunction);
      }
    });
  }

  $scope.submitRnr = function () {
    promoteRnr(submitValidatedRnr);
  };

  $scope.authorizeRnr = function () {
    promoteRnr(authorizeValidatedRnr);
  };

  function setError() {
    $scope.showError = true;
    var fullSupplyError = $scope.rnr.validateFullSupply();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupply();
    var equipmentError = $scope.rnr.validateEquipments();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;
    $scope.equipmentTabError = !!equipmentError;
    
    if ($scope.rnr.regimenLineItems)
      validateRegimenLineItems();
    var regimenError;
    if ($scope.regimenLineItemInValid) {
      regimenError = "error.rnr.validation";
    }
    var errorMessage = fullSupplyError || nonFullSupplyError || regimenError || equipmentError;
    if (errorMessage) {
      requisitionService.setErrorPages($scope);
      $scope.submitError = errorMessage;
    }
    return !!errorMessage;
  }


  function validateRegimenLineItems() {
    var setError = false;
    $.each($scope.rnr.regimenLineItems, function (index, regimenLineItem) {
      regimenLineItem.hasError = false;
      if(!regimenLineItem.skipped) {
        $.each($scope.visibleRegimenColumns, function (index, regimenColumn) {
          if ((regimenColumn.name !== "remarks" && isUndefined(regimenLineItem[regimenColumn.name]))) {
            regimenLineItem.hasError = true;
            setError = true;
            $scope.regimenLineItemInValid = true;
          }
        });
      }
    });
    if (!setError)
      $scope.regimenLineItemInValid = false;
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

  var confirm = function (promoteFunction) {
    var callBack = function (result) {
      if (result) promoteFunction();
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "msg.question.confirmation"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarning = function (showError, value) {
    if (showError && (isUndefined(value) || value === false)) {
      return "warning-error";
    }
    return null;
  };

  $scope.getCellErrorClass = function (rnrLineItem) {
    return !!rnrLineItem.getErrorMessage() ? 'cell-error-highlight' : '';
  };

  $scope.lineItemErrorMessage = function (rnrLineItem) {
    return rnrLineItem.getErrorMessage();
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

    var nonLineItemFields = ['rnr', 'programRnrColumnList', 'numberOfMonths', 'rnrStatus', 'cost', 'productName', 'hasError','equipments','IsRemarkRequired','isEquipmentValid','isEquipmentValid', 'unskip' ];

    function transform(copyFrom) {
      return _.map(copyFrom, function (lineItem) {
        return _.omit(lineItem, nonLineItemFields);
      });
    }

    //Who wrote this? This is awesome!!
    rnr[$scope.visibleTab + 'LineItems'] = transform($scope.page[$scope.visibleTab]);
    rnr.nonFullSupplyLineItems = transform($scope.rnr.nonFullSupplyLineItems);

    return rnr;
  }
}

CreateRequisitionController.resolve = {
  requisitionData: function ($q, $timeout, Requisitions, $route, $rootScope) {
    var deferred = $q.defer();
    $timeout(function () {
      var rnrData = $rootScope.rnrData;
      if (rnrData) {
        deferred.resolve(rnrData);
        $rootScope.rnrData = undefined;
        return;
      }
      Requisitions.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },
  comments: function($q, $timeout, RequisitionComment, $route){
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionComment.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data.comments);
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

  pageSize: function ($q, $timeout, LineItemsPerPage) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemsPerPage.get({}, function (data) {
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

  equipmentOperationalStatus: function ($q, $timeout, EquipmentOperationalStatus) {
    var deferred = $q.defer();
    $timeout(function () {
      EquipmentOperationalStatus.get({}, function (data) {
        deferred.resolve(data.status);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facilityApprovedProducts: function ($q, $timeout, $route, FacilityApprovedProducts) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityApprovedProducts.get({facilityId: $route.current.params.facility, programId: $route.current.params.program},
          function (data) {
            deferred.resolve(data.nonFullSupplyProducts);
          }, {});
    }, 100);
    return deferred.promise;
  },

  requisitionRights: function ($q, $timeout, $route, FacilityProgramRights) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityProgramRights.get({facilityId: $route.current.params.facility, programId: $route.current.params.program},
          function (data) {
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

