# Contributing to OpenLMIS
Contributing to OpenLMIS is what delivers a shared, open source solution for managing medical commodity distribution
in low- and middle-income countries.  Your contributions are needed!  Before you get started, take a moment to read
this quick guide, (get to know the community) and (get in touch with us) so we can coordinate.

## Reporting Bugs
The OpenLMIS community is currently using a public JIRA for tracking bugs.  This system helps track current and 
historical bugs, what work has been done, and so on.  Reporting a bug with this tool is the best way to get the bug 
fixed quickly and in the best manner.

### Before you report a bug:
- Search to see if the same bug or a similar one has already been reported.  If one already exists it saves
you time in reporting it again and the community from investigating it twice.
- If the bug exists but has been closed, check to see which version of OpenLMIS it was fixed on and which version you
are using.  If it is fixed in a newer version you may want to upgrade.  If you can't upgrade you may need to ask on
the technical forums.
- If the bug doesn't appear to be fixed, you may ask to re-open the bug report or file a new one.

### Reporting a new bug:
Fixing bugs is a time-intensive process.  To speed things along and assist in fixing the bug, it greatly helps to send
in a complete, accurate and well thought out bug report.  These steps can help that along:

- First make sure you search for the bug!  It takes a lot of work to report and investigate bug reports so please do
this first. See above.
- A clear and concise description of what you saw, as well as a description of what you thought you should see from
the feature.
- Detailed steps that someone unfamiliar with the bug can use to recreate it.  Make sure this bug occurs on different
personal computers, servers, web-browsers, etc.
- The web-browser (e.g. FireFox), version (e.g. v40), OpenLMIS version, as well as any custom modifications made.
- Your priority in fixing this bug!
- If applicable the output of any stacktrace, or logging output
- If possible and relevant, a sample or view of the database - though don't post sensitive information in public

### Example Bug Report

```
R&R not being saved
OpenLMIS v2.0, Postgres 9.2, FireFox v40, Windows 10

When attempting to save my in progress R&R for the Essential Medicines program for the reporting period of Jan 2015,
I get an error at the bottom of the screen that says "Whoops something went wrong"

Steps:

1. login

2. goto Requistions->Create/Authorize

3. Select My Facility (Facility F3020A - Steinbach Hospital)

4. Select Essential Medicines Program

5. Select Regular type

6. Click Create for the Jan 2015 period

7. Fill in some basic requested items, or not, it makes no difference in the error

8. Click the Save button in the bottom of the screen

9. See the error in red at the bottom.

I expected this to save my R&R, regardless of completion, so that I may resume it later.

Please see attached screenshots, log output and database snapshot.
```

## Feature Roadmap
Todo:  where to find and describe work priortization, link to jira issues

## Contributing Code
The OpenLMIS community welcomes code contributions and we encourage you to fix a bug or implement a new feature.  
However we also need to make sure that contributions keep in mind existing implementations and the needs of a 
Logistics Management Information System that works in many different localities and programs.  To enable OpenLMIS to 
keep a rapid pace of development while allowing for country custimizations we are in the process of splitting the 
software into two products:

* Core - is a collection of the most widely followed functionality and therefore special attention is paid to 
maintaining backwards compatibility.  You should not fork this!  If you need to change functionality here, be sure to
ask on the developer forums and if apporpriate will rapidly help build an extension point in core for you to utilize
without forking.

* Reference Distribution - is what utilizes Core to provide a useable and extendable application.  The Reference 
Distribution (RD) is what implementor's deploy to run their logistics system and what developers can utilize to 
develop custom modules for.

### Extension Point Fast Track
Extension points are interfaces within Core that allow Core to be extended via a Module and allows the functionality 
within Core to change in predictable ways without necessitating Core to fork.  Keeping Core from forking is a high 
priority for the OpenLMIS community.  To enable this we've fast tracked the building of extension points.

An example of a need for an extension point might be the calculation of a re-supply quantity on the R&R form.  Lets 
say that the R&R currently supports calculating resupply amount utilizing the current consumption of the Product and
that in your logisitics system you need to calculate this resupply amount using the current population.  Core 
defines the need for a resupply amount and the consumption based calculation, but not currently any way to calculate
based off of population.  To avoid forking Core, and to maintain the integrity of the community, we will fast 
track building an extension point in Core that allows you to plugin your desired calculation service.

To get this started, let us know what you are doing and your plans on the [developer forums](https://groups.google
.com/forum/#!forum/openlmis-dev) and we'll be sure to help you by building extension points.

### Developing A New Module

TODO:  describe how a module is built and where it's contributed.  Reference Module needed as a pattern?

### What's not accepted

* Code that breaks the build or disables / removes needed tests to pass

* Code that doesn't pass our Quality Gate - TODO: link to style guide

* Code that belongs in a Module but was added to Core or vice versa

* Code that might break existing implementations - sometimes we might have to leave an implementation behind, but the
 community needs to know about it first!

* Changes to some specific style preference - if you can't change it all at once or someone disagrees with your 
whitespace preferences, it won't be accepted.  It's better to just stick with what's already there.

## Git, Branching & Pull Requests
The OpenLMIS community employs a couple code-management techniques to help develop the software, enable contributions,
discuss & review and pull the community together.  The first is that OpenLMIS code is managed using Git and is always 
publicly hosted on [GitHub](http://github.com/OpenLMIS/open-lmis) and we encourage everyone working on the code-base to 
take advantage of GitHub's fork and pull-request model to track what's going on. Second we follow, as needed, a git 
branching strategy based on Vincent Driessen's 
[A Successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/)
which we encourage contributors to follow as well to ease the task of reviewing and accepting contribution 
submissions.

A short example of contributing would look like this:

1. *Communicate* using JIRA, the wiki, or the developer forums!

2. *Fork* the OpenLMIS project on GitHub

3. *Branch* from the `dev` branch to do your work

4. *Commit* early and often to your branch

5. *Re-base* your branch *often* from OpenLMIS `dev` branch

6. Issue a *Pull Request* back to the `dev` branch - explain what you did and keep it brief to speed review!

While developing your code, be sure you follow our style guide <todo: link> and keep your contribution specific to
doing **one** thing.

### Step by Step

TODO:  need this but with details on module creation & repository locations

## CI, CD and Demo Systems
TODO:  add in links

## Language Translations & Localized Implementations
TODO:  insert links for Transifex and how we can support "global" translations as opposed to project based localizations

## Licensing
OpenLMIS code is licensed under an open source license to enable everyone contributing to the code-base and the
community to benefit collectively.  As such all contributions have to be licensed using the OpenLMIS license to be
accepted; no exceptions.  Licensing code appropriately is simple:

### Modifying existing code in a file
- Add your name or your organization's name to the license header. e.g. if it reads `copyright VillageReach`, update it
to `copyright VillageReach, <insert name here>`
- Update the copyright year to a range.  e.g. if it was 2014, update it to read 2014-2015

### Adding new code in a new file
- Copy the license file header template to the top of the new file.
- Add your name or your organization's name to the license header. e.g. if it reads `copyright VillageReach`, update it
to `copyright VillageReach, <insert name here>`
- Add the copyright year

For complete licensing details be sure to reference the LICENSE file that comes with this project.

## Referenced Links

* GitHub - [https://github.com/OpenLMIS/open-lmis](https://github.com/OpenLMIS/open-lmis)

* JIRA Issue & Bug Tracking - 

* Wiki - 

* Developer Forums - [https://groups.google.com/forum/#!forum/openlmis-dev]
(https://groups.google.com/forum/#!forum/openlmis-dev)

* OpenLMIS Website - [http://openlmis.org](http://openlmis.org)

