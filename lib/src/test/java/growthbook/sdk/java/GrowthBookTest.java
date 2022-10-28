/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package growthbook.sdk.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import growthbook.sdk.java.TestHelpers.TestCasesJsonHelper;
import growthbook.sdk.java.models.*;
import growthbook.sdk.java.services.GrowthBookJsonUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GrowthBookTest {

    TestCasesJsonHelper helper = TestCasesJsonHelper.getInstance();
    GrowthBookJsonUtils jsonUtils = GrowthBookJsonUtils.getInstance();

    @Test
    void test_evalFeature() {
        JsonArray testCases = helper.featureTestCases();

        ArrayList<String> passedTests = new ArrayList<>();
        ArrayList<String> failedTests = new ArrayList<>();
        ArrayList<Integer> failingIndexes = new ArrayList<>();

        for (int i = 0; i < testCases.size(); i++) {
//            if (i != 1) continue;

            JsonObject testCase = (JsonObject) testCases.get(i);
            String testDescription = testCase.get("name").getAsString();

            Type attributesType = new TypeToken<HashMap<String, String>>() {}.getType();
//            TestContext testContext = jsonUtils.gson.fromJson(testCase.get("context"), TestContext.class);

            String featuresJson = testCase.get("context").getAsJsonObject().get("features").getAsString();

            String attributesJson = testCase.get("context").getAsJsonObject().get("attributes").getAsString();
            HashMap<String, Object> attributes = jsonUtils.gson.fromJson(attributesJson, attributesType);

            Type forcedVariationsType = new TypeToken<HashMap<String, Integer>>() {}.getType();
            HashMap<String, Integer> forcedVariations = jsonUtils.gson.fromJson(testCase.get("context").getAsJsonObject().get("forcedVariations"), forcedVariationsType);

            System.out.println("\n\n--------------------------");
            System.out.printf("evalFeature test: %s (index = %s)", testDescription, i);
            System.out.printf("\nfeatures: %s", featuresJson);
            System.out.printf("\nattributesJson: %s", attributesJson);

            Context context = Context
                    .builder()
                    .featuresJson(featuresJson)
                    .attributes(attributes)
                    .forcedVariationsMap(forcedVariations)
                    .build();

            System.out.printf("\ncontext: %s", context);

            String featureKey = testCase.get("feature").getAsString();

            // TODO: Use this??
            String type = testCase.get("type").getAsString();

            GrowthBook subject = new GrowthBook(context);
            String expectedString = testCase.get("result").getAsString();
            FeatureResult expectedResult = jsonUtils.gson.fromJson(expectedString, FeatureResult.class);

            FeatureResult<Object> result = subject.evalFeature(featureKey);
//            System.out.printf("\n\n Eval Feature result: %s - JSON: %s", result, result.toJson());

            // TODO: why is the source wrong? (getting unknownFeature instead of defaultValue)
            // TODO: why are all FeatureResult values null??

            System.out.printf("\n\nExpected result = %s", expectedResult);
            System.out.printf("\n  Actual result = %s", result);

            boolean passes = expectedResult.equals(result);
//            boolean passes = expectedString.equals(result.toJson());

            if (passes) {
                passedTests.add(testDescription);
            } else {
                failedTests.add(testDescription);
                failingIndexes.add(i);
            }

//            JsonArray testCase = (JsonArray) jsonElement;
        }

        System.out.printf("\n\n✅ evalFeature - Passed tests: %s", passedTests);
        System.out.printf("\n\n\n❗️ evalFeature - Failed tests = %s / %s . Failing = %s", failedTests.size(), testCases.size(), failedTests);
        System.out.printf("\n\n\n evalFeature - Failing indexes = %s", failingIndexes);

        assertEquals(0, failedTests.size(), "There are failing tests");
    }

/*
    void test_evalFeature() {
        JsonArray testCases = helper.featureTestCases();

        ArrayList<String> passedTests = new ArrayList<>();
        ArrayList<String> failedTests = new ArrayList<>();

        ArrayList<Integer> failingIndexes = new ArrayList<>();

        // Failing results (response is wrong, not just data types)
        // [6, 9, 12, 13, 14, 15, 16, 17, 21, 23]

//        GrowthBook (15/24) - equality check fails
        // Failing indexes: [4, 5, 6, 9, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23]

        for (int i = 0; i < testCases.size(); i++) {
//            if (i != 6) continue;

            JsonElement jsonElement = testCases.get(i);
            JsonArray testCase = (JsonArray) jsonElement;

//            JsonObject jsonAttributes = testCase.get(1).getAsJsonObject().get("attributes").getAsJsonObject();

            String testDescription = testCase.get(0).getAsString();

            Type testContextType = new TypeToken<TestContext>() {}.getType();
            JsonElement testContextJson = testCase.get(1);
            TestContext testContext = jsonUtils.gson.fromJson(testContextJson, testContextType);

            HashMap<String, Integer> forcedVariations = new HashMap<>();

            // Build context
            System.out.println("\n\n--------------------------");
            System.out.printf("Building context for index %s named %s", i, testDescription);

            JsonObject contextJson = (JsonObject) testCase.get(1);
            JsonElement features = contextJson.get("features");
            String featuresJson = "{}";
            if (features != null) {
                featuresJson = features.toString();
            }
            Context context = new Context(
                    true,
                    testContext.getAttributes(),
                    null,
                    featuresJson,
                    forcedVariations,
                    false,
                    null
            );

            String featureKey = testCase.get(2).getAsString();

            GrowthBook subject = new GrowthBook(context);
            FeatureResult<Object> result = subject.evalFeature(featureKey);

            System.out.printf("\n\n Eval Feature result: %s", result);

            JsonObject expected = testCase.get(3).getAsJsonObject();

            // TODO: value
            // TODO: Fix unwrapping of numbers and strings. "1" != 1. We get string when we should get number.
            // TODO: Fix unwrapping of booleans and strings. "true" != true. We get string when we should get booleans.
            Object expectedValue = GrowthBookJsonUtils.unwrap(expected.get("value"));

            boolean expectedOn = expected.get("on").getAsBoolean();
            FeatureResultSource expectedSource = FeatureResultSource.fromString(expected.get("source").getAsString());

            Object unwrappedExpected = GrowthBookJsonUtils.unwrap(expectedValue);
            Object unwrappedResultValue = GrowthBookJsonUtils.unwrap(result.getValue());
            boolean valueMatches = Objects.equals(unwrappedResultValue, unwrappedExpected);

            boolean isPassing = expectedOn == result.isOn() &&
                    expectedSource == result.getSource() &&
                    valueMatches;

            System.out.printf("\n\n🚚 evalFeature - Expected value: %s == Result value: %s - Value matches %s", expectedValue, result.getValue(), valueMatches);

            if (isPassing) {
                passedTests.add(testDescription);
            } else {
                failingIndexes.add(i);
                failedTests.add(testDescription);
            }
        }

        System.out.printf("\n\n✅ evalFeature - Passed tests: %s", passedTests);
        System.out.printf("\n\n\n❗️ evalFeature - Failed tests = %s / %s . Failing = %s", failedTests.size(), testCases.size(), failedTests);
        System.out.printf("\n\n\n evalFeature - Failing indexes = %s", failingIndexes);

        assertEquals(0, failedTests.size(), "There are failing tests");
    }
*/

    @Test
    void run_executesExperimentResultCallbacks() {
        GrowthBook subject = new GrowthBook();
        ExperimentRunCallback mockCallback1 = mock(ExperimentRunCallback.class);
        ExperimentRunCallback mockCallback2 = mock(ExperimentRunCallback.class);
        Experiment<String> mockExperiment = Experiment.<String>builder().build();

        subject.subscribe(mockCallback1);
        subject.subscribe(mockCallback2);
        ExperimentResult<String> result = subject.run(mockExperiment);

        verify(mockCallback1).onRun(result);
        verify(mockCallback2).onRun(result);
    }

    @Test
    void test_runExperiment() {
        // TODO: runExperiment tests
    }
    /*
    @Test
    void test_runExperiment() {
        JsonArray testCases = helper.runTestCases();

        ArrayList<String> passedTests = new ArrayList<>();
        ArrayList<String> failedTests = new ArrayList<>();
        ArrayList<Integer> failingIndexes = new ArrayList<>();

        for (int i = 0; i < testCases.size(); i++) {
//            if (i != 0) continue;

            JsonElement jsonElement = testCases.get(i);
            JsonArray testCase = (JsonArray) jsonElement;
            String testDescription = testCase.get(0).getAsString();
            JsonObject experimentJson = testCase.get(2).getAsJsonObject();

//            Type testContextType = new TypeToken<TestContext>() {}.getType();
            JsonElement testContextJson = testCase.get(1);
//            TestContext testContext = jsonUtils.gson.fromJson(testContextJson, testContextType);
            TestContext testContext = jsonUtils.gson.fromJson(testContextJson, TestContext.class);
//            Context realContext = jsonUtils.gson.fromJson(testContextJson, Context.class);

//            System.out.printf("\n\nDeserialized context: %s", realContext);
            Type experimentType = new TypeToken<Experiment<JsonElement>>() {}.getType();
            Experiment<JsonElement> testExperiment = jsonUtils.gson.fromJson(experimentJson, experimentType);
            Experiment<JsonElement> experiment = Experiment
                    .<JsonElement>builder()
                    .key(testExperiment.getKey())
                    .variations(testExperiment.getVariations())
                    .build();

//            Context context = new Context(
//                    true,
//                    testContext.getAttributes(),
//                    null,
//                    "{}",
//                    new HashMap<>(),
//                    false,
//                    null
//            );
            Context context = Context
                    .builder()
                    .enabled(true)
                    .attributes(testContext.getAttributes())
                    .build();

            GrowthBook subject = new GrowthBook(context);

            System.out.printf("\n\nTest: %s - Attributes: %s - Experiment: %s", testDescription, testContext.getAttributes(), experiment);

            ExperimentResult<JsonElement> result = subject.run(experiment);
            System.out.printf("\n\nExperiment result: %s", result);

            JsonElement expectedValue = testCase.get(3);
            String inExperimentValue = String.valueOf(testCase.get(4).getAsBoolean());

            boolean expectedHashUsed = testCase.get(5).getAsBoolean();

            // 3rd boolean is if the hash was used
            boolean passes = expectedValue.equals(result.getValue().toString()) &&
                    inExperimentValue.equals(result.getInExperiment().toString()) &&
                    expectedHashUsed == result.getHashUsed();

            System.out.printf("\n\n run - Comparisons: Expected values %s == %s , In experiment %s == %s, Hash used %s == %s", expectedValue, result.getValue(), inExperimentValue, result.getInExperiment(), expectedHashUsed, result.getHashUsed());

            if (passes) {
                passedTests.add(testDescription);
            } else {
                failedTests.add(testDescription);
                failingIndexes.add(i);
            }
        }

        System.out.printf("\n\n✅ run - Passed tests: %s", passedTests);
        System.out.printf("\n\n\n❗️ run - Failed tests = %s / %s . Failing = %s", failedTests.size(), testCases.size(), failedTests);
        System.out.printf("\n\n\n run - Failing indexes = %s", failingIndexes);

        assertEquals(0, failedTests.size(), "There are failing tests");
    }*/
}
