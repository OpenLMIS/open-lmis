| Stable        | Development   |
|:-------------:|:-------------:|
| [![Master Build Status](http://build.openlmis.org/job/OpenLMIS-Stable/badge/icon)](http://build.openlmis.org/job/OpenLMIS-Stable/)     | [![Dev Build Status](http://build.openlmis.org/job/OpenLMIS-dev-branch/badge/icon)](http://build.openlmis.org/job/OpenLMIS-dev-branch/)|

OpenLMIS (Open Logistics Management Information System) is software for a shared, open source solution for managing medical commodity distribution in low- and middle-income countries.  For more information, see http://openlmis.org/.

__Project Links:__
* [OpenLMIS.org](http://openlmis.org)
* [Wiki](https://openlmis.atlassian.net/wiki/display/OP)
* [Project Management](https://openlmis.atlassian.net/projects/OLMIS/issues/)
* [Slack](http://openlmis.slack.com)
* [Developer Forums](https://groups.google.com/forum/#!forum/openlmis-dev)
* [Product Committee Forums](https://groups.google.com/forum/#!forum/openlmis_product_committee)
* [Governance Committee Forums](https://groups.google.com/forum/#!forum/openlmis-governance)
* [Code Quality](http://sonar.openlmis.org)
* [Build](http://build.openlmis.org)

System Requirements
---------------------------
- JDK 7
- Postgresql 9.2+
- Git
- Firefox
- Gradle 2.3
  * **For Linux users**
    * Download the source binary directly from the gradle website.
    * Copy the downloaded folder to `/usr/bin`
    * Add the path to gradle bin folder to your `/etc/profile` file
    `export PATH="$PATH:/usr/bin/gradle-2.3/bin"`
  * **For Mac users**
    * Install HomeBrew
    * Run `brew install gradle`
- Node.js
  * **For Linux users**
    * Install Node.js as described [here](https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager#rhelcentosscientific-linux-6) based on your Linux flavour.
  * **For Mac users**
    * Install Node.js [directly](http://nodejs.org/) or using homebrew using `brew install nodejs`
    * Those who install Node.js using Homebrew should export the following (or include in `$HOME/.bash_profile` or `$HOME/.profile` or `$HOME/.bashrc` or `$HOME/.zshrc`, depending on your shell.

        ```bash
        export NODE_PATH="/usr/local/bin/node"
        export PATH="/usr/local/share/npm/bin:$PATH"
        ```
- NPM dependencies (used for linting JS, LESS files, minifying JS files & running jasmine specs etc.)
  * Install Grunt command-line runner by running (after installing Node.js)
    `> npm install -g grunt-cli`
  * Install karma test runner with karma coverage by running
    `> npm install -g karma karma-coverage`
  * Install karma command line with:
    `> npm install -g karma-cli`
  * You may need to install further karma dependencies for FireFox:
    `> npm install -g  karma-firefox-install`
  * And for jasmine:
    `> npm install -g  karma-jasmine`
  * Grunt tasks available can be found in `modules/openlmis-web/Gruntfile.js`

Source code
------------------
1. Get the source code using `git clone https://github.com/openlmis/open-lmis.git`.
2. By default you'll checkout the `master` branch.  This is the latest stable code.  For the latest development code
checkout the `dev` branch.
3. Set up dependencies on submodules & Grunt using:  
  
    ```shell  
    > cd open-lmis
    > git submodule init
    > git submodule update
    > cd modules/openlmis-web
    > npm install
    ```
  
### Contributing
If you're intending to contribute to the OpenLMIS project, please read through [CONTRIBUTING.md](CONTRIBUTING.md).  
**note** that new features should be placed in modules and should be managed using git-repo.

IntelliJ IDEA Setup
-------------------
1. Run `gradle idea` to create the IntelliJ project files (may take some time downloading dependencies).
2. Open the open-lmis.ipr file (may take some time indexing files, first time only).
3. Install Lombok plugin according to the IntelliJ version.
4. To run individual tests in IntelliJ, configure your IntelliJ preferences to enable "annotation processing"

Jasmine Tests
-------------------
1. To run jasmine tests headlessly: gradle karmaRun

Feature Toggle (for 2.0)
--------------------------------------------------
A number of country-specific features (eLMIS, VIMS, Moz) are integrated into the project. These are turned off by 
default. To turn them on, edit the `gradle.properties` file by setting `toggleOnCustom = true`.

See https://openlmis.atlassian.net/wiki/display/OP/2.0+Feature+Toggle+Mechanism for more details.

To add features to the toggling mechanism, see [Feature Toggle.md](docs/Feature%20Toggle.md) in the `docs` folder.

Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
2. Setup _postgres_ user with password as configured in `gradle.properties` file.
3. Add psql command to your PATH
4. You can use `gradle clean setupdb seed build testseed run` to start the app.
5. You can use `gradle clean setupdb seed build` to just run all of the tests.
6. There are bunch of gradle tasks that you can see by running `gradle tasks`:
  - `build` is to build the app.
  - `setupdb` is to recreate the database and schema.
  - `seed` is to seed in the reference data.
  - `testseed` puts in some test data which can be used to browse through basic functionality in the system.
  - `run` is to start the embedded jetty server.
7. If you wish to run the app using supplied demo data, you can do so by running the following two commands in succession:
  - `gradle clean setupdb seed build` to build the WAR.
  - `gradle setupdb baseseed demoseed run` to initialize the database and run the server.

Once the system is running, you can access the home page at `http://localhost:9091/`. You can log into the default instance with: user: `Admin123`, pass: `Admin123` (case sensitive)

## Code analysis
Analysis of Java and Javascript sources can be reported on and visualized using a SonarQube server and the included
`sonarRunner` task.

1. Install and run the SonarQube server.
2. Configure the Sonar properties in `gradle.properties` to point to your Sonar server and database.
3. Build the project and run the analysis:
  * Basic analysis:  `gradle build sonarRunner`
  * With coverage reports install the Sonar Cobertura plugin and run cobertura report before sonar analysis.
     e.g. `gradle build cobertura sonarRunner`

### Server setup
See [SonarQube.org](http://www.sonarqube.org/) for official documentation.  For more information on how the OpenLMIS
  project configures SonarQube see the
  [OpenLMIS sonar-configuration](https://github.com/OpenLMIS/sonar-configuration) repository.

## Issues
1. If a few integration tests fail, like this:
`org.openlmis.core.repository.mapper.FacilityMapperIT > shouldUpdateFacilityWithSuppliedModifiedTime FAILED java.lang.AssertionError at FacilityMapperIT.java:292`
This can be caused by the timezone in `postgresql.conf` being different than your operating system timezone. To fix, stop the postgresql server, and edit the following line: `timezone = 'US/Pacific'` to match your current operating system timezone, then restart the postgresql server.

Tech Stack
---------------------------------
 - Java 1.7
 - Gradle 2.3
 - Postgres 9.2
 - Spring
 - Mybatis
 - Angularjs
 - Jasmine
 - Node.js
 - Grunt.js

License Terms
---------------------------
This program is part of the OpenLMIS logistics management information system platform software. Copyright © 2013, 2014, 2015 VillageReach, JSI, and ThoughtWorks.

This site contains code and related material necessary to implement a configuration of the OpenLMIS logistics management information system platform.  See https://github.com/OpenLMIS/open-lmis/ for details of OpenLMIS.

This site contains free software: you can redistribute it and/or modify it under the terms of the appropriate license.  As this site contains code developed by more than one organization and licensed under different terms you should refer to the license terms stated in each component for details.

The programs and documents on this site are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the applicable License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.
