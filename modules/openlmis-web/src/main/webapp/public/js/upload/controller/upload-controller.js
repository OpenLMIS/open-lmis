function UploadController($scope, $routeParams, SupportedUploads) {
  $scope.model = ($routeParams.model) ? $routeParams.model : "";
  $scope.errorMsg = ($routeParams.error) ? $routeParams.error : "";
  $scope.successMsg = ($routeParams.success) ? $routeParams.success : "";

  SupportedUploads.get({}, function (data) {
    $scope.supportedUploads = data.supportedUploads;
  }, {});

}
