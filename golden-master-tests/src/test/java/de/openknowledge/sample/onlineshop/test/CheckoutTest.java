/*
 * Copyright (C) open knowledge GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.onlineshop.test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class CheckoutTest {
    
    private static final int ONLINE_SHOP_PORT = 8080;
    private static ComposeContainer environment =
            new ComposeContainer(new File("../docker-compose.yaml"))
            .withExposedService("online-shop-1", ONLINE_SHOP_PORT, Wait.forHttp("/index.html").forStatusCode(200));
    private static String onlineshopUrl;
    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        environment.start();
        onlineshopUrl = environment.getServiceHost("online-shop_1", ONLINE_SHOP_PORT) + ":" + environment.getServicePort("online-shop-1", ONLINE_SHOP_PORT);
        playwright = Playwright.create();
	    browser = playwright.chromium().launch();
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
        environment.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void shouldCheckout() {
        page.navigate(onlineshopUrl + "/index.html");
        Locator caption = page.getByText("Onlineshop");
        assertThat(caption).isVisible();
        
        Locator addToCartButton = page.getByText("In den Warenkorb");
        assertThat(addToCartButton.first()).isVisible();
        addToCartButton.first().click();

        page.waitForCondition(() -> page.getByText("Zur Kasse gehen").isVisible());
        Locator checkoutButton = page.getByText("Zur Kasse gehen");
        checkoutButton.click();

        page.waitForCondition(() -> page.getByText("Email-Bezahlung").isVisible());
        Locator payByEmailButton = page.getByText("Email-Bezahlung");
        payByEmailButton.click();
        Locator emailInput = page.locator("#email");
        page.waitForCondition(() -> emailInput.isVisible());
        emailInput.fill("max.mustermann@openknowledge.de");
        page.getByText("Auswählen").last().click();

        page.waitForCondition(() -> page.getByText("Rechnungsadresse").isVisible());
        page.getByText("Auswählen").first().click();

        page.waitForCondition(() -> page.getByText("Lieferadresse").isVisible());
        page.getByText("Aus Rechnungsadresse übernehmen").click();

        page.waitForCondition(() -> page.getByText("Kostenpflichtig Bestellen").isVisible());
        page.getByText("Kostenpflichtig Bestellen").click();

        page.waitForCondition(() -> page.getByText("Bestellung erfolgreich abgeschlossen.").isVisible());
    }
}
