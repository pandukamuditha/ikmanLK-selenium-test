package seleniumtests;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class SeleniumTest {

    ChromeDriver driver;
    int noOfAds;

    @Test(priority = 0)
    public void setup() {
        // Setting up Chrome WebDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Panduka\\Desktop\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Test(priority = 1)
    public void getAds() {
        driver.get("https://ikman.lk/");

        // Select Properties
        driver.findElement(By.cssSelector("body > div.app-content > div.home-top > div > div.home-focus > div > div:nth-child(1) > div:nth-child(2) > a > span.ui-sprite.categories-36.property")).click();

        // Select Houses
        driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-3.lg-filter-area > div > div > form > div > div:nth-child(3) > div > ul > li > ul:nth-child(2) > li > ul > li.ui-link-tree-item.cat-411 > a > span")).click();

        // Select District: Colombo
        driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-3.lg-filter-area > div > div > form > div > div:nth-child(4) > div > ul > li > ul > li.ui-link-tree-item.loc-1506 > a > span")).click();

        // Applying price filter
        driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-3.lg-filter-area > div > div > form > div > div.ui-accordion-item.filter-price > a > span")).click();
        driver.findElement(By.id("filters[0][minimum]")).sendKeys("5000000");
        driver.findElement(By.id("filters[0][maximum]")).sendKeys("7500000");
        driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-3.lg-filter-area > div > div > form > div > div.ui-accordion-item.filter-price.is-open > div > div:nth-child(6) > div > div > button")).click();

        // Select 3 beds
        driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-3.lg-filter-area > div > div > form > div > div.ui-accordion-item.filter-enum.filter-bedrooms > a > span")).click();
        driver.findElement(By.id("filters2values-3")).click();

        // Get the # of ads from the summary text
        String noOfAdsTxt = driver.findElement(By.cssSelector("body > div.app-content > div > div.serp-listing > div.ui-panel.is-rounded.serp-panel > div.ui-panel-content.ui-panel-block > div:nth-child(1) > div.col-12.lg-9 > div > div > div:nth-child(1) > div > div > div > span")).getText();
        String[] noOfAdsSplitted = noOfAdsTxt.split(" ");
        noOfAds = Integer.parseInt(noOfAdsSplitted[3]);
        System.out.printf("No of Ads found: %d\n", noOfAds);
    }

    @Test(priority = 2)
    public void validatePriceAndBeds() {
        int current = 0;
        while (current < noOfAds) {
            // Get all ads on the page except sponsored ones
            List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@class, 'ui-item') and not(contains(@class, 'is-top'))]"));

            
            // Iterating through the ads list
            for (WebElement element : elements) {
                current++;

                // Extracting price and parsing into Int
                String priceTxt = element.findElement(By.className("item-info")).getText();
                String[] priceTxtSplitted = priceTxt.split(" ");
                String priceTxtClean = priceTxtSplitted[1].replace(",", "");
                int price = Integer.parseInt(priceTxtClean);

                System.out.printf("Ad Number %d Price is : %d\n", current, price);
                
                // Check price range
                boolean isPriceValid = ((price <= 7500000) && (price >= 5000000)) ? true : false;
                Assert.assertEquals(isPriceValid, true);

                // Extracting # of beds and parsing into Int
                String bedsTxt = element.findElement(By.cssSelector(".item-meta > span:nth-child(1)")).getText();
                String[] bedsTxtSplitted = bedsTxt.split(" ");
                int beds = Integer.parseInt(bedsTxtSplitted[1]);

                // Check # of beds
                boolean isBedsValid = (beds == 3) ? true : false;
                Assert.assertEquals(isBedsValid, true);
            }

            // When ads on the page is processed. Directed to the next page
            if (current < noOfAds) {
                driver.findElement(By.className("pag-next")).click();
            }
        }
    }
}
