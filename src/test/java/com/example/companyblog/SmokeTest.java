/**
 * 
 */
package com.example.companyblog;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author otaeguis
 * 
 */
public class SmokeTest {

    private String baseUrl;

    @Before
    public void setupBaseUrl() {
        String TomcatPort = System.getenv("TOMCATPORT");
        if (TomcatPort.isEmpty()) {
            fail("Environment variable 'TOMCATPORT' does not exist");
        }
        this.baseUrl = new String("http://localhost:" + TomcatPort);
    }

    @Test
    public void appComesUpAndCanReadHomePageTest() throws Exception {
        WebClient browser = new WebClient();
        HtmlPage mainPage = browser.getPage(this.baseUrl);
        String FirstH1Occurrence = mainPage.getBody().getElementsByTagName("h1").get(0)
                .getTextContent();

        assertEquals(FirstH1Occurrence, "Welcome to Company!");
    }

}
