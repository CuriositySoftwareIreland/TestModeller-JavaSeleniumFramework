package pages.Authentication;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.apache.commons.codec.binary.Base32;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;

public class AuthenticateMFAActions extends BasePage {
    @TestModellerIgnore
    public AuthenticateMFAActions(WebDriver driver) {
        super(driver);
    }

    /**
     * Generate an MFA
     * @name Generate MFA Code
     */
    public String getMFACode(String secretKey) {
        // The secret key (in Base32).
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);

        // Authenticator apps typically use a duration of 30 seconds.
        Duration duration = Duration.ofSeconds(30);

        // Create a generator.
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(duration);

        // Generate a code.
        String code = null;
        try {
            code = String.valueOf(totp.generateOneTimePassword(new SecretKeySpec(bytes, "HmacSHA1"), Instant.now()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();

            failStep("Failed to generate MFA key", e.getMessage());
        }

        passStep("Generated MFA code of " + code);

        return code;
    }
}
