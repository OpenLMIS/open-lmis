/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.web;

import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NullRequestCache implements RequestCache {
  @Override
  public void saveRequest(HttpServletRequest request, HttpServletResponse response) {

  }

  @Override
  public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  @Override
  public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  @Override
  public void removeRequest(HttpServletRequest request, HttpServletResponse response) {

  }
}
