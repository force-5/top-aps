package com.force5solutions.care.aps.specs

//import grails.plugin.geb.GebSpec

import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import geb.spock.GebReportingSpec

class BaseGebSpec extends GebReportingSpec {

    def js(String script) {
        (driver as JavascriptExecutor).executeScript(script)
    }

    public static void takeAScreenShotOfTheApp() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenBounds = new Rectangle(0, 0, screenDim.width as int, screenDim.height as int);

        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenBounds);

        File screenshotFile = new File("/tmp/a.png");
        ImageIO.write(image, "png", screenshotFile);
    }


    def goTo(def page, boolean executeTo = true) {
        if (executeTo) {
            to(page)
            go(page.url)
            assert at(page)
        } else {
            go(page)
        }
    }

    WebDriver createDriver() {
        WebDriver driver
        if (System.getProperty("geb.driver") == null) {
            driver = new HtmlUnitDriver()
            driver.setJavascriptEnabled(true)
        } else {
            driver = super.createDriver()
        }
        return driver
    }

}


