/**
 * Created by abebe on 12/2/2014.
 */
function StorageTypeEditController($scope, $location, $route, messageService,StorageTypeDetail, CreateStorageType, UpdateStorageType, StorageTypeList) {

    $scope.startStorageEdit = function (id) {

        StorageTypeDetail.get({id: id}, function (data) {
            $scope.storageType = data.storageType;
            alert(storageType.id);
        });
    };
    $scope.createStorageType = function () {

        $scope.error = "";
        if ($scope.storageTypeForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            StorageTypeList.get({}, function (data) {
                $scope.storageTypeList = data.storageTypeList;
            }, function (data) {
                $location.path($scope.$parent.sourceUrl);
            });
            $scope.$parent.message = 'New Vaccine Storage Type created successfully';

            $scope.storageType = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";
        if ($scope.storageType.id) {

            UpdateStorageType.save($scope.storageType, createSuccessCallback, errorCallback);
        }
        else {

            CreateStorageType.save($scope.storageType, createSuccessCallback, errorCallback);
        }
        $location.path('/storage-type');
    };
    $scope.cancelEdit=function(){
        $location.path('/storage-type');
    };
    $scope.startStorageEdit($route.current.params.id);
}