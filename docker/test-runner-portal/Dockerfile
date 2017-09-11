FROM siglus/open-lmis-portal:latest
MAINTAINER ThoughtWorks

#update system and install git
RUN apt-get update && apt-get install -y git npm
#uninstall openjdk7
RUN apt-get purge openjdk* -y
#install oracle jdk
COPY installers/ /installers
RUN cd /installers && \
    tar -zxf jdk-7u80-linux-x64.tar.gz && \
    mkdir /opt/jdk/ && \
    mv jdk1.7.0_80/ /opt/jdk/ && \
    update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.7.0_80/bin/java 100 && \
    update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.7.0_80/bin/javac 100

ENV JAVA_HOME=/opt/jdk/jdk1.7.0_80

RUN git clone https://github.com/SIGLUS/open-lmis.git open-lmis
RUN cd open-lmis && git checkout 2.0-moz && ./gradlew downloadDependencies

