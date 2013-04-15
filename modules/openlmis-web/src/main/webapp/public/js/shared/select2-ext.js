/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

window.Select2.class.multi.prototype.clearSearch = function() {
  var placeholder = this.getPlaceholder();
  console.log(this.countSelectableResults());

  if (placeholder !== undefined && this.search.hasClass("select2-focused") === false && !this.opened()) {
    console.log(this.search.val());
    this.search.val(placeholder).addClass("select2-default");
    // stretch the search box to full width of the container so as much of the placeholder is visible as possible
    this.resizeSearch();
  } else {
    // we set this to " " instead of "" and later clear it on focus() because there is a firefox bug
    // that does not properly render the caret when the field starts out blank
    this.search.val(" ").width(10);
  }
}