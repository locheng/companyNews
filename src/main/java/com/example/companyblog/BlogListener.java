package com.example.companyblog;

import java.io.IOException;
import java.io.File;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.System;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import com.example.companyblog.model.Blog;

import static com.example.companyblog.AppConstants.*;

public class BlogListener implements ServletContextListener {

    Prevayler prevayler;
    private String dataDir;

    public void setupPrevayleyDirectory() throws IOException {
        final String DBDATADIR = System.getenv("DBDATADIR");
        final String USER_HOME = System.getProperty("user.home");
        String DBPATH = null;

        if (DBDATADIR.isEmpty()) {
            DBPATH = new String(USER_HOME + File.separatorChar + "companynews/data");
        } else {
            DBPATH = new String(DBDATADIR + File.separatorChar + "companynews/data");
        }
        System.out.println("Setting up the path to " + DBPATH);
        File DBFILE = new File(DBPATH);
        if (!DBFILE.exists()) {
            DBFILE.mkdir();
        }
        this.dataDir = new String(DBFILE.getCanonicalPath());
    }

    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Setting up Prevayler.");
        try {
            setupPrevayleyDirectory();
            final String PREVAYLENCE_HOME = new String(this.dataDir);
            prevayler = PrevaylerFactory.createPrevayler(new Blog(), PREVAYLENCE_HOME);
            event.getServletContext().setAttribute(PREVAYLER_KEY, prevayler);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Tearing down Prevayler.");
        try {
            prevayler.takeSnapshot();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
