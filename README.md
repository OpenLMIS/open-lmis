This site contains code and related material necessary to implement a configuration of the OpenLMIS logistics management information system platform.  See https://github.com/OpenLMIS/open-lmis/ for details of OpenLMIS.

This site contains free software: you can redistribute it and/or modify it under the terms of the appropriate license.  As this site contains code developed by more than one organization and licensed under different terms you should refer to the license terms stated in each component for details.

The programs and documents on this site are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the applicable License for more details.

You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/

You should also have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.

For additional information contact AskDeliver@jsi.com



System Requirement
---------------------------

- JDK 7
- Postgresql 9
- Git
- Gradle 1.6
  ### For Linux users
   * Download the source binary directly from the gradle website.
   Copy the downloaded folder to /usr/bin. 
   * Add the path to gradle bin folder to your /etc/profile file
   export PATH="$PATH:/usr/bin/gradle-1.6/bin"
   
  ### For Mac users
   * Install HomeBrew
   * Run ```brew install gradle```         

- Node.js

  ### For Linux users
   * Install Nodejs as described [here](https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager#rhelcentosscientific-linux-6) based on your Linux flavour.

  ### For Mac users
  * Install Node.js [directly](http://nodejs.org/) or using homebrew using `brew install nodejs`
  * Those who install Node.js using Homebrew should export the following (or include in ```$HOME/.bash_profile``` or ```$HOME/.profile``` or ```$HOME/.bashrc``` or ```$HOME/.zshrc```, depending on your shell.
      - **export NODE_PATH="/usr/local/bin/node"**
      - **export PATH="/usr/local/share/npm/bin:$PATH"**  
  
- NPM dependencies (used for linting JS, LESS files, minifying JS files & running jasmine specs etc.)
  * Install Grunt command-line runner by running (after installing Node.js as mentioned in aformentioned step) ```npm install -g grunt-cli```
  * Install project-specific grunt dependencies by navigating to ```modules/openlmis-web``` from project root directory and run ```npm install``` (one-time activity)
  * Install karma test runner with karma coverage by running ```npm install -g karma karma-coverage```
  * Grunt tasks available can be found in ```modules/openlmis-web/Gruntfile.js``` 

Source code 
------------------
 1. Get the source code using ``git clone https://github.com/USAID-DELIVER-PROJECT/elmis.git``
 2. Also resolve dependencies on submodule using command ``git submodule init`` and then ``git submodule update``
 


IntelliJ IDEA Setup
-------------------
1. Run ```gradle idea``` to create the IntelliJ project files (may take some time downloading dependencies)
2. Open the open-lmis.ipr file (may take some time indexing files, first time only)
3. Install Lombok plugin according to the IntelliJ version.

 
Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
3. Setup _postgres_ user with password as configured in _gradle.properties_ file.

3. You can use ```gradle clean setupdb setupExtensions seed build testseed run``` to start the app.
 
 There are bunch of gradle tasks that you can see by running ```gradle tasks```

 - ```build``` is to build the app.
 - ```setupdb``` is to recreate the database and schema.
 - ```setupExtensions``` is to apply the database schema exentions added.
 - ```seed``` is to seed in the reference data.
 - ```testseed``` puts in some test data which can be used to browse through basic functionality in the system.
 - ```run``` is to start the embedded jetty server.

Once run, you can access the home page at http://localhost:9091/

Tech Stack
---------------------------------

 - Java 1.7
 - Gradle
 - Postgres
 - Spring
 - Mybatis
 - Angularjs
 - Jasmine
 - Node.js
 - Grunt.js


