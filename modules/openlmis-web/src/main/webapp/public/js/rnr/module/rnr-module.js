var rnrModule = angular.module('rnr', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html'}).
        when('/supervised/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/supervised-init.html'}).
        when('/create-rnr/:facility/:program/:period', {templateUrl:'partials/create.html'}).
        when('/approve/list', {templateUrl:'partials/approve.html'}).
        otherwise({redirectTo:'/init-rnr'});
}]).directive('rnrValidator', function () {
        return {
            require:'?ngModel',
            link:function (scope, element, attrs, ctrl) {
                var validationFunction = rnrModule[attrs.rnrValidator];
                ctrl.$parsers.unshift(function (viewValue) {
                    if (validationFunction(viewValue, element.attr('name'))) {
                        ctrl.$setValidity(element.attr('name'), true);
                        ctrl.$setValidity('rnrError', true);
                        if (viewValue == "") viewValue = undefined;
                        return viewValue;
                    } else {
                        ctrl.$setValidity(element.attr('name'), false);
                        if(!attrs['preventRnrError']) ctrl.$setValidity('rnrError', false);
                        return undefined;
                    }
                });
            }
        };
    });

rnrModule.positiveInteger = function (value, errorHolder) {
    var toggleErrorMessageDisplay = function (valid, errorHolder) {
        if (valid) {
            document.getElementById(errorHolder).style.display = 'none';
        } else {
            document.getElementById(errorHolder).style.display = 'block';
        }
    };

    var INTEGER_REGEXP = /^\d*$/;
    var valid = INTEGER_REGEXP.test(value);

    if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder)

    return valid;
};

