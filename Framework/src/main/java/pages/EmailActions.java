package pages;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import jakarta.mail.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

public class EmailActions extends BasePage {
    private Store store;

    @TestModellerIgnore
    public EmailActions(WebDriver driver) {
        super(driver);
    }

    /**
     * Connect to an IMAP mail server with the given credentials
     * @name Connect to Mail Server
     */
    public void ConnectToMail(String username, String password, String server, int serverPort)
    {
        Properties props = System.getProperties();

        props.setProperty("mail.imap.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust","true");

        Session session = Session.getInstance(props);
        try {
            store = session.getStore("imap");

            store.connect(server, serverPort, username, password);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            failStep(e.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            failStep(e.getMessage());
        }
    }

    /**
     * Get latest email in the Inbox
     * @name Get Latest Mail
     */
    public jakarta.mail.Message GetLatestMail()
    {
        try {
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);

            Message[] mails = inbox.getMessages();
            ArrayUtils.reverse(mails);

            logEmail(mails[0]);

            return mails[0];
        } catch (Exception e) {
            e.printStackTrace();

            failStep(e.getMessage());
        }

        return null;
    }

    /**
     * Get latest message in the Inbox from the specified sender
     * @name Get Latest Message From
     */
    public jakarta.mail.Message GetLatestMessageFrom(String fromMail)
    {
        try {
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);

            Message[] mails = inbox.getMessages();
            ArrayUtils.reverse(mails);

            Message foundMail = null;
            for(Message m : mails) {
                if (m.getFrom()[0].equals(fromMail)) {
                    foundMail = m;

                    break;
                }
            }

            if (foundMail == null)
                failStep("No message found from '" + fromMail + "'");

            logEmail(foundMail);

            return foundMail;

        } catch (Exception e) {
            e.printStackTrace();

            failStep(e.getMessage());
        }

        return null;
    }

    /**
     * Get latest message in the Inbox containing the specified subject
     * @name Get Latest Message Containing Subject
     */
    public jakarta.mail.Message GetLatestMessageContainingSubject(String subject)
    {
        try {
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);

            Message[] mails = inbox.getMessages();
            ArrayUtils.reverse(mails);

            Message foundMail = null;
            for(Message m : mails) {
                if (m.getSubject().contains(subject)) {
                    foundMail = m;

                    break;
                }
            }

            if (foundMail == null)
                failStep("No message found with subject '" + subject + "'");

            logEmail(foundMail);

            return foundMail;

        } catch (Exception e) {
            e.printStackTrace();

            failStep(e.getMessage());
        }

        return null;
    }

    /**
     * Assert the specified message subject contains the value
     * @name Assert Mail Subject Contains
     */
    public void AssertMailSubjectContentContains(jakarta.mail.Message message, String content)
    {
        try {
            if (!message.getSubject().contains(content)) {
                failStep("Message does not contain subject '" + content + "'. Found '" + message.getSubject() + "'");
            }

            passStep("Message contains subject '" + content + "'");
        } catch (MessagingException e) {
            e.printStackTrace();

            failStep(e.getMessage());
        }
    }

    /**
     * Assert the specified message content contains the value
     * @name Assert Mail Content Contains
     */
    public void AssertMailContentContains(jakarta.mail.Message message, String content)
    {
        try {
            String mailBody = getEmailBody(message);

            if (!mailBody.contains(content)) {
                failStep("Message does not contain content '" + content + "'. Found '" + mailBody + "'");
            }

            passStep("Message body contains '" + content + "'");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();

            failStep(e.getMessage());
        }
    }

    private void logEmail(Message mail)
    {
        String mailLogMessage = "";

        try {
            mailLogMessage += ("Subject: " + mail.getSubject()) + "\n";
            mailLogMessage += ("From: " + mail.getFrom()[0]) + "\n";
            mailLogMessage += ("To: " + mail.getAllRecipients()[0]) + "\n";
            mailLogMessage += ("Date: " + mail.getReceivedDate()) + "\n";
            mailLogMessage += ("Size: " + mail.getSize()) + "\n";
            mailLogMessage += ("Flags: " + mail.getFlags()) + "\n";
            mailLogMessage += ("ContentType: " + mail.getContentType()) + "\n";
            mailLogMessage += ("Body: " + getEmailBody(mail)) + "\n";

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        passStep("Found email - " + mailLogMessage);
    }

    private String getEmailBody(jakarta.mail.Message email) throws IOException, MessagingException {

        String line, emailContentEncoded;
        StringBuffer bufferEmailContentEncoded = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(email.getInputStream()));
        while ((line = reader.readLine()) != null) {
            bufferEmailContentEncoded.append(line);
        }

        emailContentEncoded = bufferEmailContentEncoded.toString();

        if (email.getContentType().toLowerCase().contains("multipart/related")) {

            emailContentEncoded = emailContentEncoded.substring(emailContentEncoded.indexOf("base64") + 6);
            emailContentEncoded = emailContentEncoded.substring(0, emailContentEncoded.indexOf("Content-Type") - 1);

            String emailContentDecoded = new String(new Base64().decode(emailContentEncoded.toString().getBytes()));
            return emailContentDecoded;
        }

        return emailContentEncoded;
    }

}
