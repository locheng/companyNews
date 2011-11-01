/**
 * 
 */
package com.example.companyblog.acceptance;


import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author otaeguis
 * 
 */

public class WebAcceptanceTest {

    private Log LOG = LogFactory.getLog(WebAcceptanceTest.class);

    private String baseUrl;

    @Before
    public void setupBaseUrl() {
        final String TomcatPort = System.getenv("TOMCATPORT");
        if (TomcatPort.isEmpty()) {
            this.baseUrl = new String("http://localhost:8080");
        } else {
            this.baseUrl = new String("http://localhost:" + TomcatPort);
        }
        LOG.info("Setting up baseUrl to " + this.baseUrl);
    }

    @Test
    public void shouldReturnAnHtmlPage() throws Exception {
        
        final String url = new String(this.baseUrl);
        final WebClient wc = new WebClient(BrowserVersion.FIREFOX_3_6);
        
        LOG.info("Executing shouldReturnAnHtmlPage with url " + url);
        HtmlPage page = wc.getPage(url);

        assertThat(page.asXml(), containsString("<html"));
    }

    @Test
    public void shouldPostANewItemToNews() throws Exception {
        
        final String now = new Date().toString();
        final String url = new String(this.baseUrl + "/Post.action");
        final WebClient wc = new WebClient(BrowserVersion.FIREFOX_3_6);
        
        
        LOG.info("Executing shouldPostANewItemToNews with url" + url);
        final HtmlPage page = wc.getPage(url);
        LOG.info("get form by name");
        final HtmlForm form = page.getFormByName("Post");
        LOG.info("form get title");
        final HtmlTextInput title = form.getInputByName("title");
        LOG.info("form get textarea");
        final HtmlTextArea textArea = form.getTextAreaByName("body");
        LOG.info("form get submit button");
        final HtmlSubmitInput submitButton = form.getInputByValue("Submit");
        LOG.info("form set title value");
        title.setValueAttribute("test: " + now);
        LOG.info("form set textarea");
        textArea.setText("Lorem Ipsum");
        LOG.info("form click submit");
        final HtmlPage result = submitButton.click();
        
        assertThat(result.asXml(), containsString("test: " + now));

    }
}
