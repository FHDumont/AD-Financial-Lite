FROM alpine:3.7

RUN mkdir -p /sharedFiles/AppServerAgent
RUN mkdir -p /sharedFiles/MachineAgent

COPY AppServerAgent /sharedFiles/AppServerAgent
COPY MachineAgent /sharedFiles/MachineAgent

CMD ["tail", "-f", "/dev/null"]