function ApproveRnrController($scope, RequisitionsForApproval) {

  RequisitionsForApproval.get({}, function(data) {
    $scope.requisitions = data.rnr_list;
  }, function (data) {

  });

}
