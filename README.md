License Terms
---------------------------

This program is part of the OpenLMIS logistics management information system platform software.
Copyright © 2013 VillageReach


This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.


This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 



System Requirement
---------------------------

- JDK 7
- Gradle 1.6

  ### For Linux users
   Download the source binary directly from the gradle website.
   Copy the downloaded folder to /usr/bin. Add the path to gradle bin folder to your /etc/profile file
   export PATH="$PATH:/usr/bin/gradle-1.6/bin"

  ### For Mac users
   * Install HomeBrew
   * Run ```brew install gradle```
- Git
- Postgresql 9


Source code 
------------------
 1. Get the source code using ``git clone http://github.com/OpenLMIS/open-lmis/``
 2. Also resolve dependencies on submodule using command ``git submodule init`` and then ``git submodule update``
 


IntelliJ IDEA Setup
-------------------
1. Run ```gradle idea``` to create the intellij project files
2. Open the open-lmis.ipr file

 
Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
3. Setup _postgres_ user with password as configured in _gradle.properties_ file.

3. You can use ```gradle clean setupdb setupExtensions seed build testseed run``` to start the app.
 
 There are bunch of gradle tasks that you can see by running ```gradle tasks```

 - ```build``` is to build the app.
 - ```setupdb``` is to recreate the database and schema.
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


