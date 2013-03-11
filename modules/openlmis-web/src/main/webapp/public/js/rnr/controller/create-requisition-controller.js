function CreateRequisitionController($scope, requisition, currency, rnrColumns, $location, Requisitions, $routeParams, $rootScope) {

  $scope.showNonFullSupply = $routeParams.supplyType == 'non-full-supply';
  $scope.baseUrl = "/create-rnr/" + $routeParams.facility + '/' + $routeParams.program + '/' + $routeParams.period;
  $scope.fullSupplyLink = $scope.baseUrl + "?supplyType=full-supply&page=1";
  $scope.nonFullSupplyLink = $scope.baseUrl + "?supplyTpe=non-full-supply&page=1";
  $scope.fillPagedGridData = function () {
    var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize) ? Math.ceil(gridLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  };
  $scope.rnr = requisition;
  $scope.visibleColumns = _.where(rnrColumns, {'visible':true});
  $scope.programRnrColumnList = rnrColumns;
  $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name':'quantityRequested'});

  prepareRnr();

  $scope.currency = currency;


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

  $scope.$on('$routeUpdate', function () {
    $scope.fillPagedGridData();
    if ($scope.saveRnrForm.$dirty) $scope.saveRnr();
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


  $scope.saveRnr = function (preventMessage) {
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"}, rnr, function (data) {
      if(preventMessage) return;
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

  $scope.submitRnr = function () {
    resetFlags();
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      $scope.saveRnr(true);
      $scope.submitError = errorMessage;
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
      rnr, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$rootScope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  }
  ;

  $scope.authorizeRnr = function () {
    resetFlags();
    var errorMessage = validateAndSetErrorClass();
    if (errorMessage) {
      $scope.saveRnr(true);
      $scope.submitError = errorMessage;
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"authorize"}, rnr, function (data) {
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
    $scope.rnr = new Rnr(rnr, rnrColumns);

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

  function resetFlags() {
    $scope.submitError = "";
    $rootScope.submitMessage = "";
    $scope.error = "";
    $scope.message = "";
  }

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "fullSupplyLineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.fullSupplyLineItems, function (lineItem) {
      rnr.fullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }
}

CreateRequisitionController.resolve = {

  requisition:function ($q, $timeout, Requisition, $route, $rootScope) {
    var deferred = $q.defer();
    $timeout(function () {
      var rnr = $rootScope.rnr;
      if (rnr) {
        deferred.resolve(rnr);
        $rootScope.rnr = undefined;
        return;
      }
      Requisition.get({facilityId:$route.current.params.facility, programId:$route.current.params.program, periodId:$route.current.params.period}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  currency:function ($q, $timeout, ReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      ReferenceData.get({}, function (data) {
        deferred.resolve(data.currency);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

