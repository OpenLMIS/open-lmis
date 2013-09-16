License Terms
---------------------------

Copyright © 2013 VillageReach.  All Rights Reserved.  All of the Source Code Form in this repository is subject to the terms of the Mozilla Public License, v. 2.0. 
If a copy of the MPL was not distributed with this file, you can obtain one at  http://mozilla.org/MPL/2.0/.



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
 Get the source code using ``git clone http://github.com/OpenLMIS/open-lmis/``
 Also resolve dependencies on submodule using command ``git submodule init`` and then ``git submodule update``
 


IntelliJ IDEA Setup
-------------------
1. Run ```gradle idea``` to create the intellij project files
2. Open the open-lmis.ipr file

 
Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
3. Setup _postgres_ user with password as configured in _gradle.properties_ file.

3. You can use ```gradle clean setupdb seed build testseed run``` to start the app.
 
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


