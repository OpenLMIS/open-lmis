function UploadController($scope, $http) {

    $scope.uploadFile = function () {
        if (document.getElementById('csvFile').value == "") {
            $scope.error = "Please select a file to upload.";
            $scope.message = "";
            return;
        }
        $scope.error = "";
        $scope.message = "";
        var xhr = new XMLHttpRequest();
        var fd = document.getElementById('uploadForm');
        xhr.addEventListener("load", $scope.uploadComplete, false);
        xhr.open("POST", "/admin/upload.json");
        xhr.send(new FormData(fd));
    };

    $scope.uploadComplete = function (evt, data) {
        $scope.$apply(function () {
            if (evt.target.status == 200) {
                $scope.message = $.parseJSON(evt.target.responseText).success;
            } else {
                $scope.error = $.parseJSON(evt.target.responseText).error;
            }
        });
    };


}
