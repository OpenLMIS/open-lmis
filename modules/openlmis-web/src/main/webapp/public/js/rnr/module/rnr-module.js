var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid', 'ui.bootstrap.modal','ui.bootstrap.pagination']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html'}).
    when('/create-rnr/:facility/:program/:period/:supplyType', {controller:CreateRequisitionController, templateUrl:'partials/create.html'}).
    when('/rnr-for-approval', {controller:ApproveRnrListController, templateUrl:'partials/list.html', resolve:ApproveRnrListController.resolve}).
    when('/requisitions-for-convert-to-order', {controller:ConvertToOrderListController, templateUrl:'partials/convert-to-order-list.html', resolve:ConvertToOrderListController.resolve}).
    when('/view-requisitions', {controller:ViewRnrListController, templateUrl:'partials/view-rnr.html', resolve: ViewRnrListController.resolve}).
    when('/rnr-for-approval/:rnr/:facility/:program/:supplyType', {controller:ApproveRnrController, templateUrl:'partials/approve.html'}).
    when('/requisition/:id/program/:programId/:supplyType', {controller:ViewRnrController, templateUrl:'partials/view.html'}).
    otherwise({redirectTo:'/init-rnr'});
}]).directive('rnrValidator',function () {
    return {
      require:'?ngModel',
      link:function (scope, element, attrs, ctrl) {
        var validationFunction = rnrModule[attrs.rnrValidator];

        element.bind('blur', function() {
          var viewValue = ctrl.$viewValue;
          validationFunction(viewValue, element.attr('name'));
        });
        ctrl.$parsers.unshift(function (viewValue) {
          if (validationFunction(viewValue, element.attr('name'))) {
            if (viewValue == "")  viewValue = undefined;
            return viewValue;
          } else {
            ctrl.$viewValue = undefined;
            ctrl.$render();
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
  var valid = (value == undefined) ? true : INTEGER_REGEXP.test(value);

  if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder);

  return valid;
};

function parseIntWithBaseTen(number) {
  return parseInt(number, 10);
}

