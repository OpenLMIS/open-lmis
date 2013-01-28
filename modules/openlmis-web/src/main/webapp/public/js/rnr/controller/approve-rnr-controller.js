function ApproveRnrController($scope, requisition, Requisitions, programRnRColumnList) {
  var columnDefinitions = [];
  $scope.requisition = requisition;
  $scope.lineItems = [];
  populateRnrLineItems(requisition);
  if (programRnRColumnList.length > 0) {
    $scope.programRnRColumnList = programRnRColumnList;
    $($scope.programRnRColumnList).each(function (i, column) {
      if (column.name == "quantityApproved") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')})
      } else if (column.name == "remarks") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')})
      } else {
        columnDefinitions.push({field:column.name, displayName:column.label});
      }
    });
  } else {
    $scope.$parent.error = "Please contact Admin to define R&R template for this program";
  }
  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" name="' + field + '" ng-model=" + value + "/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"  > <input ui-event="{blur : \'row.entity.updateCostWithApprovedQuantity(row.entity)\'}" ng-class="{red: approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8" minLengh="1" name=' + field + ' ng-model=' + value + '  ng-change="validatePositiveInteger(positiveIntegerForm.' + field + '.$error,'+value+')" />' +
      '<span class="field-error" id=' + field + ' ng-show="positiveIntegerForm.' + field + '.$error.pattern" ng-class="{red: approvedQuantityNumberFlag && positiveIntegerForm.' + field + '.$error.pattern}">Please Enter Numeric value</span></ng-form></div>';
  }

  $scope.validatePositiveInteger = function(error, value){
    if(value == undefined){ error.pattern=false;  return};

    error.pattern = !isPositiveNumber(value);
  }

  $scope.gridOptions = { data:'lineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    columnDefs:columnDefinitions
  };

  $scope.saveRnr = function () {
    $scope.approvedQuantityNumberFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityNumberFlag = true;
        return false;
      }
      ;
    })
    if ($scope.approvedQuantityNumberFlag) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }
    Requisitions.update({id:$scope.requisition.id, operation:"save"},
      $scope.requisition, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  }

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || lineItem.quantityApproved == "" || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      };
    })
    if ($scope.approvedQuantityRequiredFlag) {
      $scope.error = "Please complete the highlighted fields on the R&R form before approving";
      $scope.message = "";
      return;
    }
    Requisitions.update({id:$scope.requisition.id, operation:"approve"},
      $scope.requisition, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  }

  function isPositiveNumber(value) {
    var INTEGER_REGEXP = /^\d*$/;
    return INTEGER_REGEXP.test(value);

  }

  function populateRnrLineItems(rnr) {
    $(rnr.lineItems).each(function (i, lineItem) {
      lineItem.cost = parseFloat((lineItem.packsToShip * lineItem.price).toFixed(2));
      if (lineItem.lossesAndAdjustments == undefined) lineItem.lossesAndAdjustments = [];
      var rnrLineItem = new RnrLineItem(lineItem);
      jQuery.extend(true, lineItem, rnrLineItem);
      $scope.lineItems.push(lineItem);
    });
  }

  ;

}

ApproveRnrController.resolve = {
  requisition:function ($q, $timeout, RequisitionById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionById.get({id:$route.current.params.rnr},
        function (data) {
          deferred.resolve(data.rnr);
        }, function () {
          console.log("error")
        });
    }, 100);
    return deferred.promise;
  },
  programRnRColumnList:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};