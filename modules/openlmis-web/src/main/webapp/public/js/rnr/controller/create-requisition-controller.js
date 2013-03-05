function CreateRequisitionController($scope, Requisition, ReferenceData, ProgramRnRColumnList, $location, Requisitions, $routeParams, $rootScope) {

  $scope.programRnrColumnList = [];
  $scope.showNonFullSupply = $routeParams.supplyType == 'non-full-supply';
  $scope.baseUrl = "/create-rnr/" + $routeParams.facility + '/' + $routeParams.program + '/' + $routeParams.period;
  $scope.fullSupplyLink = $scope.baseUrl + "?supplyType=full-supply&page=1";
  $scope.nonFullSupplyLink = $scope.baseUrl + "?supplyTpe=non-full-supply&page=1";
  $scope.pageSize = 10;
  $scope.fillPagedGridData = function() {
      var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.lineItems;
      $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize);
      $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
      $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
    };


  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program, periodId:$routeParams.period},
      function (data) {
        if (data.rnr) {
          $scope.$parent.rnr = data.rnr;
          prepareRnr();
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path('/init-rnr');
        }
      }, function () {
      });
  } else {
    $scope.fillPagedGridData();
  }

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, function () {
  });

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    if (data.rnrColumnList && data.rnrColumnList.length > 0) {
      $scope.visibleColumns = _.where(data.rnrColumnList, {'visible':true});
      $scope.programRnrColumnList = data.rnrColumnList;
      $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name':'quantityRequested'});
      $scope.$broadcast("rnrPrepared");
    } else {
      $scope.error = "rnr.template.not.defined.error";
    }
  }, function () {
    $location.path("/init-rnr");
  });

  $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

  $scope.switchSupplyType = function (supplyType) {
    $scope.showNonFullSupply = supplyType == 'non-full-supply';
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    $scope.fillPagedGridData();
    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    $scope.checkDirtyAndSaveForm();
  });

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.periodDisplayName = function () {
    if (!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);

    var endDate = new Date($scope.rnr.period.endDate);
    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };


  $scope.saveRnr = function () {
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"}, rnr, function (data) {
      $rootScope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $rootScope.message = '';
          });
        });
      }, 3000);
      $scope.error = "";
      $scope.saveRnrForm.$dirty = false;
    }, function (data) {
      $scope.error = data.error;
      $scope.message = "";
    });
  };

  $scope.checkDirtyAndSaveForm = function () {
    if ($scope.saveRnrForm.$dirty) {
      $scope.saveRnr();
    }
    return true;
  };

  $scope.submitRnr = function () {
    resetFlags();
    if (!valid()) return;
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
      rnr, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$rootScope.hasPermission('AUTHORIZE_REQUISITION');
        $rootScope.submitMessage = data.success;
        $scope.submitError = "";
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.authorizeRnr = function () {
    if (!valid()) return;
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"authorize"}, rnr, function (data) {
      $scope.rnr.status = "AUTHORIZED";
      $scope.formDisabled = true;
      $rootScope.submitMessage = data.success;
      $scope.submitError = "";
    }, function (data) {
      $scope.submitError = data.data.error;
    });
  };

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != isUndefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.hide = function () {
    return "";
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass == 'required' && (isUndefined(value) || value.toString().trim().length == 0)) {
      return "required-error";
    }
    return null;
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarningBasedOnField = function (value, field) {
    if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required' && field) {
      return "warning-error";
    }
    return null;
  };

  $scope.highlightWarning = function (value) {
    if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required') {
      return "warning-error";
    }
    return null;
  };

  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
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

    var lineItemsJson = rnr.lineItems;
    rnr.lineItems = [];
    $(lineItemsJson).each(function (i, lineItem) {
      rnr.lineItems.push(new RnrLineItem(lineItem, $scope.rnr, $scope.programRnrColumnList));
    });

    var nonFullSupplyLineItemsJson = rnr.nonFullSupplyLineItems;
    rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      rnr.nonFullSupplyLineItems.push(new RnrLineItem(lineItem, $scope.rnr, $scope.programRnrColumnList))
    });

    resetCostsIfNull();
    $scope.fillPagedGridData();
    $scope.formDisabled = (function () {
      if ($scope.rnr) {
        var status = $scope.rnr.status;
        if (status == 'INITIATED' && $rootScope.hasPermission('CREATE_REQUISITION')) return false;
        if (status == 'SUBMITTED' && $rootScope.hasPermission('AUTHORIZE_REQUISITION')) return false;
      }
      return true;
    })();
  }

  function valid() {
    if ($scope.saveRnrForm.$error.required) {
      $scope.saveRnr(null);
      $scope.inputClass = "required";
      $scope.submitMessage = "";
      $scope.submitError = 'Please complete the highlighted fields on the R&R form before submitting';
      return false;
    }
    if (!formulaValid()) {
      $scope.saveRnr(null);
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return false;
    }
    return true;
  }

  function resetFlags() {
    $scope.submitError = "";
    $scope.inputClass = "";
    $rootScope.submitMessage = "";
  }

  // TODO: Push this method to rnr-line-item
  function formulaValid() {
    var valid = true;
    $($scope.rnr.lineItems).each(function (index, lineItem) {
      if (lineItem.arithmeticallyInvalid() || lineItem.stockInHand < 0 || lineItem.quantityDispensed < 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "lineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.lineItems, function (lineItem) {
      rnr.lineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }
}
