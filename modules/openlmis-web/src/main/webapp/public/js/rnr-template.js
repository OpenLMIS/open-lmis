'use strict';

function RnRTemplateCtrl($scope, $http) {
    $http.get('../../../../json/rnr/programs.json').success(function (data) {
        $scope.programs = data;
    });
}
