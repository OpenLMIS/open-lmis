'use strict';

/* App Module */
angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives'],function ($routeProvider, $locationProvider, $httpProvider) {
  var interceptor = ['$rootScope', '$q', function (scope, $q) {
    function success(response) {
      angular.element('#loader').hide();
      return response;
    }

    function error(response) {
      angular.element('#loader').hide();
      switch (response.status) {
        case 403:
          window.location = "/public/pages/access-denied.html";
          break;
        case 401:
          scope.modalShown = true;
          break;
        default:
          break;
      }
      return $q.reject(response);
    }

    return function (promise) {
      return promise.then(success, error);
    };
  }];
  $httpProvider.responseInterceptors.push(interceptor);
}).config(function ($httpProvider) {
    var spinnerFunction = function (data) {
      angular.element('#loader').show();
      return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerFunction);
  })
  .directive('uiNav',function () {
    return {
      restrict:'A',

      link:function (scope, element, attrs) {
        //Identify all the menu lists
        var lists = $(".navigation ul");

        //Sort the lists based their nesting, innermost to outermost
        lists.sort(function (a, b) {
          return $(b).parents("ul").length - $(a).parents("ul").length;
        });

        setTimeout(function () {

          lists.each(function () {
            var display = false;

            //Check if all the child items are hidden
            $(this).children("li:not(.beak)").each(function () {
              if ($(this).css('display') != 'none') {
                display = true;
                return false;
              }
            });
            //Hide the list and its containing li in case all the children are hidden
            if (!display) {
              $(this).parent().hide();
              $(this).parent().parent().hide();
            }
          });

          $(".navigation li > a").on("click", function () {
            $(this).next(".submenu").show();
          });
        });
      }
    };
  }).directive('openlmisMessage', function (messageService) {
    return {
      restrict:'A',
      link:function (scope, element, attrs) {
        scope.$watch(attrs.openlmisMessage, function () {
          var displayMessage = messageService.get(scope[attrs.openlmisMessage]);
          if (displayMessage)
            element.html(displayMessage);
          else
            element.html(scope[attrs.openlmisMessage]);
        });
      }
    }
  })
  .directive('formToolbar',function () {
    return {
      restrict:'A',
      link:function (scope, element, attrs) {
        
        function fixToolbarWidth() {
          var toolbarWidth = $(document).width() - 26;
          angular.element("#action_buttons").css("width", toolbarWidth + "px");
        };

        fixToolbarWidth();
        $(window).on('resize', fixToolbarWidth);
      }
    };
  }).directive('placeholder',function () {
    return {
      restrict:'A',
      require:'ngModel',
      link:function (scope, element, attr, ctrl) {
        var value;

        if (!jQuery.support.placeholder) {
          var placeholder = function () {
            ctrl.$modelValue = undefined;
            ctrl.$viewValue = attr.placeholder;
            ctrl.$render();
            element.css("color", "#a2a2a2");
          };
          var unPlaceholder = function () {
            ctrl.$viewValue = undefined;
            element.css("color", "");
            ctrl.$render();
          };

          scope.$watch(attr.ngModel, function (val) {
            if (val == attr.placeholder)   val = '';
            value = val || '';
          });

          element.bind('focus', function () {
            if (value == '') unPlaceholder();
          });

          element.bind('blur', function () {
            if (element.val() == '') {
              placeholder();
            }
          });

          ctrl.$formatters.unshift(function (val) {
            if (!val || (val == attr.placeholder)) {
              placeholder();
              value = '';
              return attr.placeholder;
            }
            return val;
          });
        }
      }
    };
  }).run(function ($rootScope) {
    $rootScope.$on('$routeChangeStart', function () {
      angular.element('#ui-datepicker-div').hide();
      angular.element('body > .modal-backdrop').hide();
    });
  });

function isUndefined(value) {
  return (value == null || value == undefined || value.toString().trim().length == 0);
}
jQuery.support.placeholder = !!function () {
  return "placeholder" in document.createElement("input");
}();
