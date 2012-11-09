function UploadController($scope, $http) {



    $scope.uploadFile = function () {
        var xhr = new XMLHttpRequest();
        var fd = document.getElementById('uploadForm');
        xhr.addEventListener("load", $scope.uploadComplete, false);
        xhr.open("POST", "/resources/pages/admin/upload.json");
        xhr.send(new FormData(fd));
    }

    $scope.uploadComplete =   function(evt) {
      //  $scope.response=  evt.target.responseText;
        alert(evt.target.responseText.replace(/\\\"/g, '\"'));
    }


}
