function ApproveRnrController($scope, requisition, programRnRColumnList) {
  var columnDefinitions = [];
  $scope.lineItems = angular.copy(requisition.lineItems);
  populateRnrLineItemsCost();
  if (programRnRColumnList.length > 0) {
    $scope.programRnRColumnList = programRnRColumnList;
    $($scope.programRnRColumnList).each(function (i, column) {
      if (column.name == "quantityApproved") {
        columnDefinitions.push({field:"quantityApproved", displayName:column.label, cellTemplate:editableCellTemplate( 'row.entity.quantityApproved', 'row.entity.id')})
      } else {
        columnDefinitions.push({field:column.name, displayName:column.label});
      }
    });
  } else {
    $scope.$parent.error = "Please contact Admin to define R&R template for this program";
  }

  function editableCellTemplate(value, id) {
    var editableElement = "<div><form name='approvalForm'><input type='number' maxlength='8' min='0' ng-required='true' name='quantityApproved' ng-model="+ value +" /> " +
      "<span class='field-error' ng-show='approvalForm.quantityApproved.$error.number'>Please Enter Numeric value</span></form></div>";

    return editableElement;
  }

  $scope.gridOptions = { data:'lineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    columnDefs:columnDefinitions
  };

  function populateRnrLineItemsCost() {
    $($scope.lineItems).each(function (i, lineItem) {
      lineItem.cost = parseFloat((lineItem.packsToShip * lineItem.price).toFixed(2));
    });
  }
}

ApproveRnrController.resolve = {
  requisition:function ($q, $timeout, RequisitionById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionById.get({id:$route.current.params.id},
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
      ProgramRnRColumnList.get({programId:$route.current.params.programId}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};