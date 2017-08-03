**Before every Commit & Push. ** 
==================================

The command to deliver a new feature or fix,  please follow the standard for our commits, it has this structure:

```sh
$ git commit -m 'CODE#CARD - Initials1/Initials2 - Message of the new feature provided'
```

| Color         					 		| Codes       		| Description  |
| :----------------------------------------:|:-----------------:|:------------:|
| <font color='silver'>WHITE</font>   		| **US** 			| User history |
| <font color='GOLDENROD'>SPIKE</font>  	| **SP**      		|   Spike      |
| <font color='turquoise'>TECH TASK</font> 	| **TT**     		|    Tech Task |
| <font color='salmon'>BUG/DEFECT</font> 	| **DE**     		| Bug/Defect   |

This document is a easy way to remember all the things that you have to do before make a **Commit** in **PSM Moz** project
The **back-end** project has **two** parts: one in ***Java*** and the other one in ***Javascript*** with **AngularJS**. Also we have another repository for **mobile** application.

**Openlmis**
----------
Whenever you have changes to push on the project run all Unit Test in **IntellijIDEA**

-or-

Run this command in your terminal, to execute all unit test and styling checks. **Remember** this would empty your database.
```sh
$ gradle clean setupdb seed build
```

----------
**Openlmis-web**
----------
Whenever you have changes to push on the project remember check the following commands in order to NOT break the build.


This command runs **Unit Tests** in Javascript
```sh
$ gradle karmaRun
```

This command checks **Code Style**, remember **bad** code style will **break** the build. 
```sh
$ gradle jshint
```
If you edit or create a LESS file you can check the code style with the following command:
```sh
$ gradle lesslint
```

After that, you can check the project pipelines [here][1]

----------

**Openlmis-moz-mobile**
-------------

When you have to add new features in Android code always run all unit tests in **Android Studio**, refer to the **README** file of the project to understand which commands you should run before push new changes. 

In order to check the unit test, run this command:
```sh
$ ./gradlew testLocalDebug
```
To run functional tests, make sure to have Genymotion started and execute:
```sh
$ ./gradlew assembleLocalDebug
$ ./gradlew functionalTests
```

----------
**Style format for JS files**
-------------
 - Identation: 2 spaces
 - Only spaces in tab character.


After that, you can check the project pipelines [here][1]

  [1]: https://52.69.16.156:8080
