var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html'}).
        when('/supervised/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/supervised-init.html'}).
        when('/create-rnr/:facility/:program/:period', {controller: RequisitionController, templateUrl:'partials/create.html'}).
        when('/rnr-for-approval', {controller: ApproveRnrListController, templateUrl:'partials/list.html', resolve: ApproveRnrListController.resolve}).
        when('/rnr-for-approval/:rnr/:facility/:program', {controller:ApproveRnrController, templateUrl:'partials/approve.html', resolve: ApproveRnrController.resolve}).
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

