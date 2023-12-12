package utilities.testmodeller;

import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.testng.ITestClass;
import org.testng.ITestResult;

import java.lang.reflect.Method;

public class TestModellerMethodExtractor {
    public static TestModellerPath getTestModellerPath(ITestResult testResult) {
        Method testMethod = testResult.getMethod().getConstructorOrMethod().getMethod();

        return getTestModellerPath(testMethod);
    }

    public static TestModellerPath getTestModellerPath(Method testMethod) {
        if (testMethod != null && testMethod.isAnnotationPresent(TestModellerPath.class)) {
            TestModellerPath path = testMethod.getAnnotation(TestModellerPath.class);
            System.out.println("Test Modeller Path GUID = " + path.guid());
            return path;
        } else {
            return null;
        }
    }

    public static TestModellerSuite getTestModellerSuite(ITestClass testClass) {
        Class<?> realClass = testClass.getRealClass();
        if(realClass != null && realClass.isAnnotationPresent(TestModellerSuite.class)) {
            TestModellerSuite suite = realClass.getAnnotation(TestModellerSuite.class);
            System.out.println("Test Modeller Suite ID = " + suite.id());
            return suite;
        } else {
            return null;
        }
    }
}
