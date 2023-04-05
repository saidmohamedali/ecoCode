package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class CookieCheckTest {

    /**
     * @formatter:off
     */
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/CookieTestFile.java")
                .withCheck(new Coookie2Check())
//                .withCheck(new CookieWithoutExpirationRule())
                .verifyIssues();
    }

}