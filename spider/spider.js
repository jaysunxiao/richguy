const main = require('puppeteer');


const url = process.argv[2];
(async () => {
    let browser = null;

    let page = null;
    try {
        browser = await main.launch({
            executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
            headless: false
        });
        page = await browser.newPage();

        await page.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
        await page.evaluateOnNewDocument(() => {
            delete navigator.__proto__.webdriver;
            Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});
            Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});
            Object.defineProperty(navigator, 'platform', {
                get: () => "Linux armxxxxx",
                configurable: true
            });
            window.chrome = {
                app: {},
                runtime: {},
                getUserMedia: {}
            };
        });
        await page.setViewport({
            width: 1366,
            height: 1024,
            deviceScaleFactor: 1
        });

        for (let i = 0; i < 1000; i++) {
            await page.goto(url, {waitUntil: 'networkidle0'});
            page.deleteCookie()

            const cookie = await page.evaluate(() => document.cookie);
            if (cookie === null || cookie === undefined) {
                continue;
            }
            await page.waitForTimeout(1000000);
            console.log(cookie);
            break;
        }
    } catch (error) {
        console.log('zfoo_error', error);
    } finally {
        if (page != null) {
            await page.close();
        }
        if (browser != null) {
            await browser.close();
        }
    }
})();
