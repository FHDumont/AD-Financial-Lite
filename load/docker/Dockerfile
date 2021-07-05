FROM node:6.14.2-slim

RUN apt-get -y update && \
    apt-get -y install vim && apt-get clean all && \
    echo 'alias ll="ls -la"' >> /etc/bash.bashrc && /bin/bash -c "source /etc/bash.bashrc" && \
    apt-get clean all

RUN npm install -g forever

RUN  mkdir /logs

COPY src/package.json /src/package.json

RUN cd /src; npm install

COPY src/index.js /src/index.js

COPY src/approval-load/* /src/approval-load/

COPY src/browser-load/* /src/browser-load/

COPY src/policy-load/* /src/policy-load/

CMD ["forever", "-l", "/logs/forever.log", "-o", "/logs/out.log", "-e", "/logs/err.log", "-vf", "/src/index.js"]