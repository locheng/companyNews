Build and Release Management solution
=====================================

I attended the camp devops conference a couple of weeks ago, I watched Jez Humble's presentation on continuous delivery 
and also the presentation of Tyler Croy on "100% continuous with jenkins and gerrit" and both talked about the use of build 
pipelines, agile development and operations, continuous delivery.
Whatching those presentation inspired me (I stole the ideas) to try to do something similar.

Basically it works this way:

1. The developers work in topic/feature branches (these are build continuously by the CI server).
2. Upon completion of the coding and testing of those branches the code is merged into `master`.
3. The [CI server](http://ci-companynews.feniix-hq.net) builds the integration branch and emails the QA team.
4. The QA team can then decide to deploy to the [QA app server](http://qa-companynews.feniix-hq.net) by going into the [Build Pipeline View](http://ci-companynews.feniix-hq.net/view/Build_Pipeline/) and pressing trigger button.
5. After reviewing and approving the build the QA team and email the operations team and they can press the trigger button to release to production, what will happen then is that the ci server will build the application, release to maven(tagging the release in the scm) and promoto to the production server if all the steps are completed successfully.

I started by doing an [ant build](https://github.com/feniix/companyNews/tree/antbuild) and although I had never had to create an ant build from scratch before it was not too difficult, but it started growing larger than I expected so I decided to switch to maven and using nexus as artifact repository, I chose to use nexus and not artifactory because I like the simplicity of installation of nexus.
I modified the code to be able to chose the location of the database storage because it had the developer's home directory hardcoded, that helped a bit with the deployment in different environments.
I envision having more build pipelines doing continuous deployment and running automated ui testing using selenium-rc and grid in several development environments. I additionally imagine some sort of database migrations process but I do not know enough programming to do that. Likewise I would recommend to configure the security settings to the CI server to enforce that not anybody can launch any job.
I did not have time to setup the prod and qa server to start on boot (as a service) so if the machine is rebooted the instances have to be manually restarted.
Another thing I had to leave out is the use or arquillian and shrinkwrap to do "in-container" unit testing. 


Summary Information
-------------------
* code repo: https://github.com/feniix/companyNews 
* ci server: http://ci-companynews.feniix-hq.net ids: admin/admin qauser1/qauser1 devuser1/devuser1 opuser1/opuser1
* artifact repository: http://repo-companynews.feniix-hq.net ids: admin/admin1
* qa server: http://qa-companynews.feniix-hq.net (if it is stopped: log in as brummie, sudo su - companynewsqa, ./tomcat/bin/startup.sh)
* prod server: http://companynews.feniix-hq.net (if it is stopped: log in as brummie, sudo su - companynews, ./tomcat/bin/startup.sh)



Code repositories
-----------------
* https://github.com/feniix/companyNews (this news repository)
* https://github.com/jenkinsci/envinject-plugin  (tag envinject-0.21) (jenkins plugin to 
    inject environment variables at build time)


Code changes done to the application
------------------------------------
* mavenized the build
* com/example/companyblog/BlogListener.java added posibility of changing the location of the database store
* com/example/companyblog/acceptance/WebAcceptanceTest.java added htmlunit test for Acceptance verification
* Added maven-cargo-plugin and the base distribution of tomcat to the code tree to ease the start of development


Installed a maven repository for the company
--------------------------------------------
* Create user "nexus" `useradd -c Nexus -s /bin/bash -m -d /var/lib/nexus nexus`
* Download `http://nexus.sonatype.org/downloads/nexus-oss-webapp-1.9.2.3-bundle.tar.gz`
* install nexus
* Deploy the prevayler library to the nexus release repository using the web upload 
    (2.3 is not available in maven central) with the following GAV com.prevayler:prevayler:2.3:jar
 
Install jenkins 1.411
---------------------
* install jenkins following the instructions in http://pkg.jenkins-ci.org/debian-stable/
* download `http://pkg.jenkins-ci.org/debian/binary/jenkins_1.411_all.deb` and install with `dpkg -i`
* edit /etc/default/jenkins (change the port top 8088)


Create the following entries on a dns server
--------------------------------------------
* ci-companynews 1800 IN CNAME REDACTED.compute-1.amazonaws.com.
* companynews 1800 IN CNAME REDACTED.compute-1.amazonaws.com.
* qa-companynews 1800 IN CNAME REDACTED.compute-1.amazonaws.com.
* repo-companynews 1800 IN CNAME REDACTED.compute-1.amazonaws.com.

Install nginix
--------------
* apt-get install nginx
* config files can be found in /etc/nginx/nginx.conf and /etc/nginx/sites-enabled/default in the box 
    (I just created virtual hosts with reverse proxies to be able to serve everything in the same port (80))

Create user "companynews" (production tomcat instance)
------------------------------------------------------
* useradd -c "Company News" -s /bin/bash -m -d /home/companynews companynews
* as the companynews user install a local copy of tomcat 5.5.33 with the admin console.
* then add the user and role manager with password manager in the file tomcat-users.xml
* modify the server.xml file, change the default http port to 8100

Create user "companynewsqa" (production tomcat instance)
-------------------------------------------------------
* useradd -c "Company News" -s /bin/bash -m -d /home/companynewsqa companynewsqa
* as the companynewsqa user install a local copy of tomcat 5.5.33 with the admin console.
* then add the user and role manager with password manager in the file tomcat-users.xml
* modify the server.xml file, change the default http port to 8090

Developer environment
---------------------
* All the developers must use:
    * git 1.7
    * maven 3.0.0 (to perform the local build)
    * in the local environment they have to setup a variable "TOMCATPORT" with a free port (that will be used to start the local 
     tomcat)
    * the settings.xml can be found in [settings.xml](https://github.com/feniix/companyNews/blob/master/developer-env/settings.xml) (this sets maven to use the right repository 
     to pull the dependencies)

Development workflow
--------------------
* There is a master branch where the code integration is made.
* The code is testing using junit and htmlunit(using cargo to test the deployment and the ui) for the acceptance testing.
* the developers work on topic or feature branches and the CI server continuosly builds all the branches
    that are published to the git repository
* The integration branch (master) is also built continuosly
* After each iteration release the developer of team of developers have to "git pull --rebase" master into their branches to
    get the changes


CI workflow
-----------
* There is a build pipeline setup that has the following steps: (http://ci-companynews.feniix-hq.net/view/Build%20Pipeline/)
    * Build_Integration_branch (builds the integration branch (master))
    * Deploy_App_to_QA (picks the latest stable build from the Build_Integration_branch and deploys to the QA environment)
    * Deploy_App_to_Prod (gets the latest code from the integrated branch and runs the maven-release-build, 
      deploys to the corporative maven repository and if it succeeds deploys to production)

