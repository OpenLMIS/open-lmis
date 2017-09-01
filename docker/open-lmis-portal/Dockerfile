FROM tomcat:7.0.75-jre7
MAINTAINER ThoughtWorks
ARG ENV

# add the build time scripts, and run them
COPY scripts /scripts
RUN chmod -R +x /scripts
RUN /scripts/build/install_packages.sh

#break cache to force docker build to redownload apps every time
#but keep the packages if cached
#ARG CACHE_DATE=not_a_date

RUN /scripts/build/download_apps.sh

# remove after built, they are not needed for run time
RUN rm -rf /scripts/build
#
# #add the run time scripts, they will run by default when you do 'docker run'
RUN chmod -R +x /scripts/run
CMD ["/scripts/run/start.sh"]
