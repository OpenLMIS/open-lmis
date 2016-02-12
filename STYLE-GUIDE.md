# OpenLMIS Style Guide
The OpenLMIS style guide is intended to assist in the design and development of OpenLMIS by introducing:

1. good design & programming rules that illuminate the OpenLMIS principles.
2. the automated source code checking mechanisms and associated rule-sets.

The OpenLMIS style guide aims to help contributors to the project create quality contributions and also as an 
automated check to ensure that the review process can proceed efficiently.  To that end, this guide has two main sections: 
the first is a set of rules that every contribution may be tested against and mechanisms for tracking the 
project's compliance automatically. The second expands upon some of the intentions behind the OpenLMIS principles 
to influence better design and implementation of OpenLMIS features.

Often the state of the code-base is far from the outlined ideals.  This presents us with the opportunity to improve 
upon the current state of the project while recording lessons learned here and through the developer forums.  In 
that vein, this guide is in no way meant to be exhaustive and further discussion is welcomed on the developer forums.


## Principles

### <a name="open_source"></a>Open Source
> OpenLMIS is offered under an open source license, which means that everyone has the right to use and modify the 
> software without paying a license fee. Changes and additions are made available to the community under the terms of 
> the license via our code contribution process.

OpenLMIS is built and licensed under an Open Source [license](LICENSE.txt).  In addition to the project being Open 
Source, OpenLMIS strives to always be available to develop on, build, deploy, use and generally contribute to using 
similarly licensed technologies.  In practice this means that strong preference is given to contributions and their 
dependant technologies that are licensed similarly.  Contributions should aspire to contribute:

* Code and other IP licensed in a compatible license as OpenLMIS.  Strong preference is given to the OpenLMIS license
 for simplicity.
* Dependencies on third-party libraries / tools should also be open source and freely distributable.

### <a name="appropriate"></a>Appropriate
> OpenLMIS is designed with a focus on users in low resource and capacity environments.  Representatives from these 
> environments are welcomed and valued members of the community and their insights help shape the software.

OpenLMIS is built and used by those in low-resource settings:

* Internet is often slow and intermittent.  Features should be designed with these limitations in mind.  For example, most 
work-flows should be optimized for slow internet and even work-flows with periods of non-connectivity.  
Administrative screens however can often take shortcuts and assume that their users will have better internet 
connectivity.
* Processes not only vary and need to be configurable by program and implementation, they oftentimes are  
used in parallel or supplement traditional paper processes.  Data collection and forms should strive to 
be configurable to match the official paper form and be able to restore it historically.
* Screens are often older and come with lower resolutions than the latest and greatest.  800x600 px screens are not uncommon.  Additionally, many work-flows that would be used by someone at the last mile will be used by someone with a
smaller tablet or even a phone.
* Scalability for OpenLMIS is the capability of use in large hospitals to community health 
workers nation wide.  The workflow from data collection, processing through to report delivery should be designed and implemented for thousands of users with thousands of physical facilities.
* Security is important for OpenLMIS to be trusted to run nation-wide government supply chains to NGO initiatives.  
A role-based security system contains users to see and do only what is required for their role.  Care should be given
in designing features and running implementations to keep OpenLMIS secure.

### <a name="configurable"></a>Configurable
> OpenLMIS flexibly supports the varied needs of low-resource health supply chains. OpenLMIS strives to be 
> designed so that countries can configure and use the software with minimal training and technical capacity.

Supply chains vary.  Reporting requirements, process differences, language, and even the look and feel need to be as 
configurable as is reasonable for OpenLMIS to continue to deliver on its mission.  In order to accomplish this, OpenLMIS
contributions need to at a minimum continue to deliver:

* Language - Language tags allow messages/UI/email/API/etc to be translated into many different languages and allows 
the user to switch the language displayed easily.  OpenLMIS has standardized development in English for consistency
and supports translation projects as the opportunity arises.
* Dates - Date formatting also varies by locality.  As such any date or time printed should allow for custom 
formatting.
* **Programs** allow for OpenLMIS to configure the vertical supply chains present in many low- and middle-income 
countries independently.  e.g. a Malaria program may collect different data by different people than an HIV/AIDS 
program.
* Schedules allow for a Program to define regular or even planned irregularity for timing of program related events.  
Monthly and quarterly are typical examples, however a schedule may have periods where a monthly schedule may have to 
be extended to a couple months when seasonal monsoons slow transportation networks.
* Variable and often Program-segregated administrative hierarchies are needed to ensure programs can operate 
independently and reflect the common situation of programs not sharing staff.
* A singular geographic hierarchy is currently in use.  Unlike many features of OpenLMIS, this definition is not 
segregated by Program and is meant to reflect that physical facilities often are part of one official geographic 
hierarchy.  In the future this may need to be Program-segregated.  For now utilizing administrative hierarchies can 
be used instead.
* Replenishment cycles also vary by Program in an implementation.  The two standard processes, distribution (push) 
and allocation (pull), are present and in use in OpenLMIS.  These two different types of processes differ by who 
starts them, their cycle, and also how re-supply calculations/projections are made.

### <a name="interoperable"></a>Interoperable
> OpenLMIS strives to be interoperable with other systems in a larger health information ecosystem.

Achieving interoperability requires a balance between allowing for flexibility and controlling for consistency.  
OpenLMIS aims to achieve this by:

* designing for and implementing customizable data storage, processing and reporting that's accessible through 
published APIs & formats.
* encouraging expansion and customization through modularity.
* maintaining a consistent and robust data-model and reporting interfaces so that a field/column/report means the 
same thing from implementation to implementation. 
* maintaining a consistent look & feel so that using OpenLMIS anywhere always looks and behaves in a predictable manner.

### <a name="collaborative"></a>Collaborative
> OpenLMIS users benefit from the diversity of perspectives and resources that community members bring to the table, 
> which results in a more flexible and powerful system than what any one organization could create. The community 
> acknowledges that successful country implementation requires close collaboration among partners and stakeholders to 
> ensure success.

* documentation is needed to communicate how to use a contribution and the intention behind it.  This can take many 
different forms and it's left to the contributor to determine and provide appropriate levels of documentation.  The 
community strongly discourages contributions that are light on documentation.  It's suggested that documentation is 
prioritized for: published APIs, designs and code contracts.  Additionally documenting the why over the how is 
oftentimes more useful over a longer period of time.
* sharing code often comes with mis-matched expectations and undesired consequences, so it's not unexpected that 
development often occurs behind closed-doors until "it's ready".  The OpenLMIS project however aims to be *open* so all 
code that is part of the OpenLMIS project is found in the OpenLMIS [repository](https://github.com/OpenLMIS/open-lmis).
The recommended approach to [collaborating](CONTRIBUTING.md) with OpenLMIS is to publicly host and link 
your project to OpenLMIS utilizing GitHub's 
[Fork and Pull Request](https://help.github.com/categories/collaborating-on-projects-using-pull-requests/) model.  
[Contribution](CONTRIBUTING.md) to OpenLMIS **requires** this pull request method.
* sharing ideas, work items, roadmaps, feature requests, knowledge bases, etc. is vital to know where the project is 
going and encourage participation.  To that end OpenLMIS encourages all participants to utilize 
the public forums, chat, project management, and wiki spaces to collaborate.  An active list is found in the 
[README](README.md).
* automated testing ensures functionality from developer to developer and implementation to implementation is 
behaving as expected over time.  OpenLMIS doesn't currently define code-coverage targets, however, the project expects 
that appropriate test coverage is provided with every contribution and highly scrutinizes existing tests.  Since 
testing is so important, calling out the kinds of testing done and not done and *why* can greatly help the review 
process for contributions.


### <a name="supportive"></a>Supportive
> The community acts as stewards for the implementation, configuration, training on, operation, and sustainment 
> of OpenLMIS. The community strives to be knowledge experts on the problems that OpenLMIS attempts to solve.


## Sonar & Quality Gate
OpenLMIS utilizes Sonar for tracking OpenLMIS quality over time.  This tool provides an on-going review
mechanism to help evaluate contributions and project metrics to ensure that key metrics aren't changing negatively.  

