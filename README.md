# WBank
A bank manager to work with trainings points from JRMCore.

## Dependencies
  * PowerNBT 0.6.2;
  * Spigot v1.7.10-SNAPSHOT.

## Wiki 
You can found the wiki right [here](https://github.com/luiz-otavio/WBank/wiki).
If it doesn't work, take a look in [here](https://github.com/luiz-otavio/WBank/tree/master/src/main/java/com/rededark/wbank/event).
The events are the most useful API from the WBank.

## Setup
### Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
	    <groupId>com.github.luiz-otavio</groupId>
	    <artifactId>WBank</artifactId>
	    <version>master-SNAPSHOT</version>
       	    <scope>provided</scope> 
	</dependency>
```

### Gradle
```gradle
repositories {
  maven { url = 'jitpack.io' }
}

dependencies {
  compileOnly 'com.github.luiz-otavio:WBank:master-SNAPSHOT'
}
```
