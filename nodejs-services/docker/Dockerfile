FROM node:11-slim

RUN apt-get -y update && \
    apt-get -y install vim && apt-get clean all && \
    echo 'alias ll="ls -la"' >> /etc/bash.bashrc && /bin/bash -c "source /etc/bash.bashrc" && \
    apt-get clean all

RUN  mkdir /logs

COPY src/package.json /src/package.json
RUN cd /src; npm install

COPY src/index.js /src/index.js

# CMD ["node", "/src/index.js"]

EXPOSE 8001

EXPOSE 8002

EXPOSE 8003

EXPOSE 8004

EXPOSE 8005

EXPOSE 8006

CMD export UNIQUE_HOST_ID=$(sed -rn '1s#.*/##; 1s/(.{12}).*/\1/p' /proc/self/cgroup) && node /src/index.js