# ProtoBuf-Replication-Prototype

## used Framworks/Tools
- AWS SDK
- JaxB
- ProtoBuf
- SpringBoot
- Eclipse >Juno
- AWS SDK-Eclipse-Plugin



## install intructions
- checkout the git project
- import the 3 folders into eclipse as eclipse-projects

## testinstructions
local testing:
- run the 3 prepared eclipse-launches(java servers on different ports)
- run "deploy local"
- change "address" in Client.java to "localhost" and run local test

online testing:
- run "build server"
- upload the jar to the ec2-instances
- configure your AWS-Eclipse-Plugin
- configure your xml in deploy-project(i.e. regions and replication-pathes)
- run "deploy" for a strategy
- change "address" in Client.java dns of your ec2-instance and run the test
