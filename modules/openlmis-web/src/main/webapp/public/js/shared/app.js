'use strict';

/* App Module */
angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives'], function ($routeProvider, $locationProvider, $httpProvider) {
  var interceptor = ['$rootScope', '$q', function (scope, $q) {
    function success(response) {
      return response;
    }

    function error(response) {
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
})
  .directive('uiNav', function () {
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
  })
  .directive('formToolbar', function () {
    return {
      restrict:'A',
      link:function (scope, element, attrs) {
        var toolbarWidth = window.innerWidth - 26;
        angular.element("#action_buttons").css("width", toolbarWidth + "px");
      }
    };
  }).run(function($rootScope) {
      $rootScope.$on('$routeChangeStart', function() {
        angular.element('body > .modal-backdrop').remove();
      });
  });

function isUndefined(value) {
  return (value == null || value == undefined);
}
