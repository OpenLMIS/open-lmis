Development Environment Setup
-----------------------------

1. Clone the project repository using git.
2. Install HomeBrew (Mac users only).
3. Run _brew install gradle_ for Mac. 

   For Linux users
   Download the source binary directly from the gradle website.
   Copy the downloaded folder to /usr/bin. Add the path to gradle bin folder to your /etc/profile file
   export PATH="$PATH:/usr/bin/gradle-1.1/bin"

4. Install PostgreSQL.
5. Setup _postgres_ user with password as configured in _gradle.properties_ file.
6. Create _open_lmis_ database.
	create database open_lmis;
7. Run _gradle build_ to run all tests and build a WAR.

IntelliJ IDEA Setup
-------------------
1. Run "gradle idea" to create the project files
2. Open the open-lmis.ipr file