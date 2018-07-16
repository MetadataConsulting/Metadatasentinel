import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

environments {

    // run via “./gradlew -Dgeb.env=chrome iT”
    chrome {
        driver = {
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>()
            chromePrefs.put("profile.default_content_settings.popups", 0)
            if ( System.getProperty('download.folder') ) {
                chromePrefs.put("download.default_directory", System.getProperty('download.folder'))
            }
            ChromeOptions options = new ChromeOptions()
            options.setExperimentalOption("prefs", chromePrefs)
            DesiredCapabilities cap = DesiredCapabilities.chrome()
            cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
            cap.setCapability(ChromeOptions.CAPABILITY, options)
            new ChromeDriver(options)
        }
    }

    // run via “./gradlew -Dgeb.env=chromeHeadless iT”
    chromeHeadless {
        driver = {
            ChromeOptions options = new ChromeOptions()
            options.addArguments('headless')
            new ChromeDriver(options)
        }
    }
    
    // run via “./gradlew -Dgeb.env=firefoxHeadless iT”
    firefoxHeadless {
        driver = {
            FirefoxOptions o = new FirefoxOptions()
            o.addArguments('-headless')
            new FirefoxDriver(o)
        }
    }

    // run via “./gradlew -Dgeb.env=firefox iT”
    firefox {
        driver = { new FirefoxDriver() }
    }
}
