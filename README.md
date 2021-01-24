# WBank
A manager to work with trainings points from JRMCore.

## Dependencies
  * PowerNBT 0.6.2;
  * Spigot v1.7.10.

## Wiki 
Look at [here](https://github.com/luiz-otavio/WBank/tree/master/src/main/java/com/rededark/wbank/event).

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
