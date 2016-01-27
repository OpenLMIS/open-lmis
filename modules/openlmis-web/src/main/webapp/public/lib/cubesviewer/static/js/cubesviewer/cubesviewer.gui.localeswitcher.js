/*
 * CubesViewer
 * Copyright (c) 2012-2015 Jose Juan Montes, see AUTHORS for more details
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * If your version of the Software supports interaction with it remotely through
 * a computer network, the above copyright notice and this permission notice
 * shall be accessible to all users.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 * Locale switcher button.
 */
function cubesviewerGuiLocaleSwitcher() { 

	this.cubesviewer = cubesviewer; 
	
	/*
	 * Draw GUI options 
	 */
	this.onGuiDraw = function(event, gui) {
		
		$(gui.options.container).find('.cv-gui-tools').prepend(
				'<div style="margin-bottom: 4px;">' +
				'Language: ' +
				'<select name="cv-gui-localeswitcher">' +
				'<option value="">Default</option>' +
				'</select>' +
				'</div>'
		    );
		
		$('[name=cv-gui-localeswitcher]', $(cubesviewer.gui.options.container)).change(function() {
			cubesviewer.gui.localeswitcher.selectLocale(gui, $(this).val());
		});
		
	}	
	
	/*
	 * Draw languages from model
	 */
	this.onCubesviewerRefresh = function(event, data) {
		
		var gui = event.data.gui;
		var cubesviewer = gui.cubesviewer;

		// Add locales (clean list first)
		$('[name=cv-gui-localeswitcher]', $(cubesviewer.gui.options.container)).empty();
		$('[name=cv-gui-localeswitcher]', $(cubesviewer.gui.options.container)).append(
				'<option value="">Default</option>'
		);
		$(cubesviewer.model["locales"]).each(
			function(idx, locale) {
				$('[name=cv-gui-localeswitcher]', $(cubesviewer.gui.options.container)).append(
						'<option value="' + locale + '">' + locale + '</option>'
				);
			}
		);
		
		// Selected language
		$('[name=cv-gui-localeswitcher]', $(cubesviewer.gui.options.container)).val(gui.cubesviewer.options.cubesLang);
		
	}
	

	/*
	 * Select cubes locale (at the time just changes .
	 */
	this.selectLocale = function(gui, locale) {
		
		gui.cubesviewer.changeCubesLang (locale);
		
	};	
	
};

/*
 * Create object.
 */
cubesviewer.gui.localeswitcher = new cubesviewerGuiLocaleSwitcher();

/*
 * Bind events.
 */
$(document).bind("cubesviewerGuiDraw", { }, cubesviewer.gui.localeswitcher.onGuiDraw);
$(document).bind("cubesviewerRefresh", { "gui": cubesviewer.gui }, cubesviewer.gui.localeswitcher.onCubesviewerRefresh);
