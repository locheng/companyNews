/**
 * 
 */
package com.example.companyblog;

import org.junit.Test;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * @author otaeguis
 *
 */
public class HtmlUnitTest {
    
    @Test
    public void testBla() throws Exception {
        WebClient browser = new WebClient();
        HtmlPage mainPage = browser.getPage("http://localhost:8888/");
        
    }

}
