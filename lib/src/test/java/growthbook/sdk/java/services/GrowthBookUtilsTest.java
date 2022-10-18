package growthbook.sdk.java.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import growthbook.sdk.java.TestHelpers.TestCasesJsonHelper;
import growthbook.sdk.java.models.BucketRange;
import growthbook.sdk.java.models.Namespace;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrowthBookUtilsTest {
    TestCasesJsonHelper helper = TestCasesJsonHelper.getInstance();

    @Test
    void canHashUsingFowlerNollVoAlgo() {
        JsonArray hnvCases = helper.getHNVTestCases();

        hnvCases.forEach(jsonElement -> {
            JsonArray kv = (JsonArray) jsonElement;

            String input = kv.get(0).getAsString();
            Float expected = kv.get(1).getAsFloat();

            assertEquals(expected, GrowthBookUtils.hash(input));
        });
    }

    @Test
    void canVerifyAUserIsInANamespace() {
        JsonArray testCases = helper.getInNamespaceTestCases();

        testCases.forEach(jsonElement -> {
            JsonArray testCase = (JsonArray) jsonElement;

            String testDescription = testCase.get(0).getAsString();
            String userId = testCase.get(1).getAsString();

            Namespace namespace = GrowthBookJsonUtils.getInstance()
                    .gson.fromJson(testCase.get(2).getAsJsonArray(), Namespace.class);
            Boolean expected = testCase.get(3).getAsBoolean();

            assertEquals(
                    expected,
                    GrowthBookUtils.inNameSpace(userId, namespace),
                    String.format("Namespace test case failure: %s", testDescription)
            );
        });
    }

    @Test
    void providesTestCaseData() {
        System.out.printf("Providing test cases: %s", helper.getTestCases());
        assertNotNull(helper.getTestCases());
    }

    @Test
    void canChooseVariation() {
        JsonArray testCases = helper.getChooseVariationTestCases();

        testCases.forEach(jsonElement -> {
            JsonArray testCase = (JsonArray) jsonElement;

            String testDescription = testCase.get(0).getAsString();

            // Float input (1st arg)
            Float input = testCase.get(1).getAsFloat();

            // Bucket Range input (2nd arg)
            JsonArray bucketRangeTuples = testCase.get(2).getAsJsonArray();
            Type bucketRangeListType = new TypeToken<ArrayList<BucketRange>>() {}.getType();
            ArrayList<BucketRange> bucketRanges = GrowthBookJsonUtils.getInstance().gson.fromJson(bucketRangeTuples, bucketRangeListType);

            Integer expected = testCase.get(3).getAsInt();

            assertEquals(
                    expected,
                    GrowthBookUtils.chooseVariation(input, bucketRanges),
                    String.format("canChooseVariation %s", testDescription)
            );
        });
    }
}
