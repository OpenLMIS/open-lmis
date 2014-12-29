/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var OpenLmisDialog = {
  newDialog: function (overrideOpts, callback, $dialog) {
    var defaults = {
      id: "",
      header: "Header",
      body: "Body",
      ok: {label: "button.ok", value: true},
      cancel: {label: "button.cancel", value: false}
    };

    var opts = {
      templateUrl: '/public/pages/template/dialog/dialogbox.html',
      controller: function ($scope, dialog) {

        $scope.dialogClose = function (result) {
          dialog.close(result);
        };

        $scope.dialogOptions = _.extend(defaults, overrideOpts);
      }
    };
    var closeCallback = function (result) {
      var tabbables = olDialog.modalEl.find(":tabbable");
      tabbables.last().unbind("keydown");
      tabbables.first().unbind("keydown");

      callback(result);
    };

    var olDialog = $dialog.dialog(opts);
    olDialog.open().then(closeCallback);


    var autoFocus = function () {
      if (olDialog.isOpen()) {
        var tabbables = olDialog.modalEl.find(":tabbable");
        tabbables.first().focus();
        tabbables.last().bind("keydown", function (e) {
          if (e.which == 9 && !e.shiftKey) {
            tabbables.first().focus();
            e.preventDefault();
          }

        });
        tabbables.first().bind("keydown", function (e) {
          if (e.which == 9 && e.shiftKey) {
            tabbables.last().focus();
            e.preventDefault();
          }
        });
      }
      else {
        setTimeout(function () {
          autoFocus();
        }, 10);
      }
    };
    autoFocus();
  }
};
