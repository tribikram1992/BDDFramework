package com.qa.pages;

import com.google.common.base.Function;
import com.qa.runners.RunnerBase;
import com.qa.utils.DriverManager;
import com.qa.utils.GlobalParams;
import com.qa.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import static java.time.Duration.ofMillis;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class BasePage {
    protected WebDriver driver;
    protected TestUtils utils;
    protected TestData td;
    public static WebElement ele;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.utils = new TestUtils();
        this.td = new TestData();
    }

    public boolean switchAlert() {
        boolean flag = false;
        try {
            if (driver.switchTo().alert() != null) {
                driver.switchTo().alert().accept();
                flag = true;
            }
        } catch (NoAlertPresentException e) {
        }
        return flag;
    }

    public void doubleClick(WebElement element, String message) {
        String text = message.replaceAll("\\s+", "");
        String filePath = null;
        if (element != null && driver != null) {
            new Actions(driver).doubleClick(element).build().perform();
        }
        highlightElement(element);
    }

    public boolean waitForVisibility(WebElement ele) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        return wait.until(ExpectedConditions.visibilityOf(ele)) != null;
    }

    public void click(WebElement ele, String message) {
        String text = message.replaceAll("\\s+", "");
        String filePath = null;
        waitForVisibility(ele);
        highlightElement(ele);
        ele.click();
    }

    public void sendKeys(WebElement ele, String txt, String message) {
        waitForVisibility(ele);
        ele.clear();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        ele.sendKeys(txt);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        highlightElement(ele);
    }

    public String getAttribute(WebElement ele, String attribute, String message) {
        String text = message.replaceAll("\\s+", "");
        String filePath = null;
        highlightElement(ele);
        return ele.getAttribute(attribute);
    }

    public void selectDropDownValue(WebElement element, String value) {
        try {
            if (element != null) {
                Select selectBox = new Select(element);
                selectBox.selectByValue(value);
                Thread.sleep(3000);
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        highlightElement(ele);
    }

    public void switchWindow() {
        String parentWindow = driver.getWindowHandle();
        Set<String> s = driver.getWindowHandles();
        System.out.println("Total Windows::::::::::::" + s.size());
        Iterator<String> I1 = s.iterator();
        while (I1.hasNext()) {
            String child_window = I1.next();
            if (!parentWindow.equals(child_window)) {
                driver.switchTo().window(child_window);
            }
        }
        System.out.println(driver.getTitle());
    }

    public void switchToParentWindow(String parentWindow) {
        driver.switchTo().window(parentWindow);
    }

    public String getCurrentDateAndTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        return dtf.format(now);
    }

    public void waitUntilPageLoad() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoAlertPresentException.class);

        ExpectedCondition<Boolean> documentReady = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String state = (String) js.executeScript("return document.readyState;");
                return state.equals("complete");
            }
        };
        wait.until(documentReady);
    }

    public static WebElement waitForElementToBeDisplayed(WebDriver driver, WebElement element, int timeoutInSeconds, int pollingIntervalInSeconds) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofSeconds(pollingIntervalInSeconds))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoAlertPresentException.class);

        return wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                if (element.isDisplayed()) {
                    return element;
                } else {
                    return null;
                }
            }
        });
    }

    public void scrollTableForWebElementIntoView(By by, By e) {
        List<WebElement> elements = driver.findElements(by);
        int a = elements.size();
        WebElement ele = elements.get(a - 1).findElement(e);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", ele);
    }

    public String returnTodayDate() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        return formatDate.format(date);
    }

    public long randomNumber(int numberOfDigits) {
        Random random = new Random();
        long min = (long) Math.pow(10, numberOfDigits - 1);
        long max = (long) Math.pow(10, numberOfDigits) - 1;
        return min + (long) (random.nextDouble() * (max - min + 1));
    }

    public void selectElementFromDropDownByValue(WebElement ele, String value) {
        Select dropdown = new Select(ele);
        dropdown.selectByValue(value);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void selectElementFromDropDownByVisibleText(WebElement ele, String value) {
        Select dropdown = new Select(ele);
        dropdown.selectByVisibleText(value);
    }

    public String decryptString(String decryptValue) {
        String result = new String();
        char[] charArray = decryptValue.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result = result + ch;
        }
        return result;
    }

    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        while (randomString.length() <= length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        String saltStr = randomString.toString();
        return saltStr;
    }

    public void scrollToWebElement(WebElement ele) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", ele);
    }

    public void validateUsernameFormat(String name) {
        String input = "weohaidisuagdsigduyfuyday, uawhdasgdjgsgdkhsagdhgsgdgdjsa";
        String pattern = "^\\s*([a-zA-Z0-9,]*)\\s*,\\s*([a-zA-Z0-9,]*)\\s*$";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(input);
        Assert.assertTrue(matcher.matches(), "Username is not in the expected format" + name);
    }

    public void compareStatus(String expected, String actual) {
        Assert.assertTrue(actual.contains(expected), "Status didn't match");
    }

    public int generateRandomTwoDigitNumber() {
        Random random = new Random();
        int num = random.nextInt(90) + 10;
        return num;
    }
    
    public void highlightElement(WebElement element) {
    	ele = element;
        for (int i = 0; i < 2; i++) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='3px solid red'", element);
        }        
    }
    
    public void removeHighlight(WebElement element) {
    	JavascriptExecutor js = (JavascriptExecutor) driver;
    	js.executeScript("arguments[0].style.border='0px solid orange'", element);
    }
}
