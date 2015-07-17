License Terms
---------------------------
This program is part of the OpenLMIS logistics management information system platform software. Copyright © 2013, 2014, 2015 VillageReach, JSI, and ThoughtWorks.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the [GNU Affero General Public License](LICENSE) along with this program. If not, see http://www.gnu.org/licenses. For additional information contact info@OpenLMIS.org.

System Requirements
---------------------------
- JDK 7
- Postgresql 9
- Git
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
  * Install project-specific grunt dependencies by navigating to `modules/openlmis-web` from project root directory and run
    `> npm install` (one-time activity)
  * Grunt tasks available can be found in `modules/openlmis-web/Gruntfile.js`

Source code
------------------
1. Get the source code using `git clone https://github.com/openlmis/open-lmis.git`.
2. For now, all work should be pushed to the 2.0 branch, not master. After cloning, you can do `git checkout 2.0` to get into the 2.0 branch.
3. Set up dependencies on submodules using
    ```bash
    > git submodule init
    > git submodule update
    ```

IntelliJ IDEA Setup
-------------------
1. Run `gradle idea` to create the IntelliJ project files (may take some time downloading dependencies).
2. Open the open-lmis.ipr file (may take some time indexing files, first time only).
3. Install Lombok plugin according to the IntelliJ version.
4. To run individual tests in IntelliJ, configure your IntelliJ preferences to enable "annotation processing"

VM setup (To improve...)
-------------------
1. Make sure you have vagrant and VirtualBox installed (TODO: add install steps for these)
2. Please obtain the vagrant box LMIS-2.0.box and related project setup from Danni or Ming (TODO: check this in somewhere)
3. In the project directory, run "vagrant box add openlmis-box LMIS-2.0.box"
4. Run "vagrant up" to start the vm
5. Run "vagrant ssh" to log onto the box
6. Run "cd projects/open-lmis" to go to your home directory
7. You can run gradle from there
8. Your openlmis 2.0 directory is set up as as shared folder in VM and local machine in projects/open-lmis

Jasmine Tests
-------------------
1. Install xvfb: apt-get install xvfb firefox
2. To run jasmine tests headlessly: gradle karmaRun (default task in build, for VM and CI)
3. To run jasmine tests with browser: gradle karmaRunWithBrowser

Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
2. Setup _postgres_ user with password as configured in `gradle.properties` file.
3. You can use `gradle clean setupdb setupExtensions seed build testseed run` to start the app.
4. You can use `gradle clean setupdb setupExtensions seed build` to just run all of the tests.
5. There are bunch of gradle tasks that you can see by running `gradle tasks`:
  - `build` is to build the app.
  - `setupdb` is to recreate the database and schema.
  - `setupExtensions` is to apply the database schema extensions added.
  - `seed` is to seed in the reference data.
  - `testseed` puts in some test data which can be used to browse through basic functionality in the system.
  - `run` is to start the embedded jetty server.

Once the system is running, you can access the home page at `http://localhost:9091/`. You can log into the default instance with: user: `Admin123`, pass: `Admin123`

## Issues
1. You may encounter a `java.lang.OutOfMemoryError: PermGen space`. This is a result of not enough memory for the Jetty JVM. One way to fix this is to export the following (or include in `$HOME/.bash_profile` or `$HOME/.profile` or `$HOME/.bashrc` or `$HOME/.zshrc`, depending on your shell).

    ```bash
    export JAVA_OPTS="-XX:MaxPermSize=512m"
    export JAVA_TOOL_OPTIONS="-Xmx1024m -XX:MaxPermSize=512m -Xms512m"
    ```
2. If a few integration tests fail, like this:
`org.openlmis.core.repository.mapper.FacilityMapperIT > shouldUpdateFacilityWithSuppliedModifiedTime FAILED java.lang.AssertionError at FacilityMapperIT.java:292`
This can be caused by the timezone in `postgresql.conf` being different than your operating system timezone. To fix, stop the postgresql server, and edit the following line: `timezone = 'US/Pacific'` to match your current operating system timezone, then restart the postgresql server.

Tech Stack
---------------------------------
 - Java 1.7
 - Gradle 2.3
 - Postgres 9
 - Spring
 - Mybatis
 - Angularjs
 - Jasmine
 - Node.js
 - Grunt.js
