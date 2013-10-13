/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

window.Select2.class.multi.prototype.clearSearch = function() {
  var placeholder = this.getPlaceholder();

  if (placeholder !== undefined && this.search.hasClass("select2-focused") === false && !this.opened()) {
    this.search.val(placeholder).addClass("select2-default");
    // stretch the search box to full width of the container so as much of the placeholder is visible as possible
    this.resizeSearch();
  } else {
    // we set this to " " instead of "" and later clear it on focus() because there is a firefox bug
    // that does not properly render the caret when the field starts out blank
    this.search.val(" ").width(10);
  }
};