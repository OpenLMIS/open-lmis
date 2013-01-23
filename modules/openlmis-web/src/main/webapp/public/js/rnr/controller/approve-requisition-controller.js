function ApproveRnrController($scope, RequisitionById, ProgramRnRColumnList, $routeParams) {
  RequisitionById.get({id:$routeParams.id},
    function (data) {
      $scope.rnr = data;
      populateRnrLineItems($scope.rnr);
    }, { }
  );

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    if (data.rnrColumnList.length > 0) {
      $scope.programRnRColumnList = data.rnrColumnList;
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
    }
  }, {});


  $scope.gridOptions = { data:'rnrLineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    columnDefs:[
      {field:'programName', displayName:'Program' },
      {field:'facilityCode', displayName:'Facility Code'},
      {field:'facilityName', displayName:"Facility Name"},
      {field:'periodStartDate', displayName:"Period Start Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'periodEndDate', displayName:"Period End Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'submittedDate', displayName:"Date Submitted", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'modifiedDate', displayName:"Date Modified", cellFilter:"date:'dd/MM/yyyy'"},
      {displayName:"View", cellTemplate:linkCellTemplate()}
    ]
  };

  function linkCellTemplate() {
    var divElement = '<button ng-click="openRnr(row.entity.id)">View</button>';
    return divElement;
  }

  ;

  function populateRnrLineItems(rnr) {
    $(rnr.lineItems).each(function (i, lineItem) {
      lineItem.cost = parseFloat((lineItem.packsToShip * lineItem.price).toFixed(2));
    });
  }

}
