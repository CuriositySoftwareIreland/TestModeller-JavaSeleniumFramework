# Java Selenium Framework for use with TestModeller.io
Open source repository of Java based selenium tests. For use with [TestModeller.io](http://www.testmodeller.io)

Follow along with this [Tutorial](https://testmodeller.io/tutorials/selenium-java/)

**NOTE:** Targets Java 8 only out of the box.

# Getting started
1. Clone this repository.

2. Install the Java Development Kit
3. Install ChromeDriver (this needs to match the version of chrome you will be executing your tests on) and make sure you add it to your system PATH.
4. Install Maven - this is used to manage the build process of our automation framework.

5. Register the code templates to Test Modeller following this [tutorial](https://curiositysoftware.ie/Resources/tutorials/ConfigureTMFramework.mp4)
6. Copy generated `page objects` into the `Framework\src\main\java\pages` directory. 
7. Copy generated `tests` into the `Framework\src\test\java\tests` directory.

8. To execute all tests navigate to the Framework directory and run `mvn test`. To execute a specific test run `mvn -Dtest=[filename] test` where [filename] is the name of the test file to execute.