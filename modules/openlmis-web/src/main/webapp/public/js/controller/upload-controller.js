function UploadController($scope, $http) {

  $scope.uploadFile = function () {
    if(document.getElementById('csvFile').value == "" ){
          $scope.error = "Please select a file to upload.";
          $scope.message = "";
          return;
    }
    $scope.error = "";
    var xhr = new XMLHttpRequest();
    var fd = document.getElementById('uploadForm');
    xhr.addEventListener("load", $scope.uploadComplete, false);
    xhr.open("POST", "/admin/upload.json");
    xhr.send(new FormData(fd));
  };

  $scope.uploadComplete = function (evt) {
    alert(evt.target.responseText.replace(/\\\"/g, '\"'));
  };


}
