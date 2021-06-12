# following project contains the repository for sample travelWorld app


requirements - 
maven
java

## steps to build the project and run it:
```
- mvn clean install
- mvn exec:java -D exec.mainClass=./target/classes/com/travelworld/App.class
```
I have also pushed the compiled classes so that the project can be run directly using the last command without having to install

I have also tweaked cities.json a tiny bit so that it forms json array of cities, it helps me to deserialize the json easily

main code is in App.java
