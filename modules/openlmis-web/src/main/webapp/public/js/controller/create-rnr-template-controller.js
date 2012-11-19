function CreateRnrTemplateController($scope, Program) {
  Program.get({}, function (data) {   //success
    $scope.programs = data.programList;
  }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {
  var id = ($scope.program? $scope.program.id: "");
  RnRColumnList.get({programId:id}, function (data) {   //success
    $scope.rnrColumnsList = data.rnrColumnList;
  }, function () {
    $location.path('select-program');
  });

  $scope.createProgramRnrTemplate = function () {
    $http.post('/admin/rnr/' + $scope.program.id + '/columns.json', $scope.rnrColumnsList);
    $scope.message = "saved successfully";
  }
}
