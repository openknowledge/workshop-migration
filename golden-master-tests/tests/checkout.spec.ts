import { test, expect } from "@playwright/test";

test("should set default billing address in customer care", async ({
  page,
}) => {
  await page.goto("/index.html");

  const personIcon = page.locator('a[href*="/customers/0815/addresses"]');
  await expect(personIcon).toBeVisible();
  await personIcon.click();

  await page
    .getByText("Standard-Rechnungsadresse")
    .waitFor({ state: "visible" });

  const newAddressSummary = page.getByText("Neue Adresse eingeben").first();
  await newAddressSummary.click();

  const streetInput = page.locator("details").first().locator("#street");
  await streetInput.waitFor({ state: "visible" });
  await streetInput.fill("Am Rundtoern");

  await page.locator("details").first().locator("#houseNumber").fill("30");
  await page.locator("details").first().locator("#zipCode").fill("26235");
  await page.locator("details").first().locator("#city").fill("Oldenburg");

  await page.locator("details").first().locator('input[type="submit"]').click();

  await page
    .getByText("Standard-Rechnungsadresse")
    .waitFor({ state: "visible" });
});

test("should checkout", async ({ page }) => {
  await page.goto("/index.html");

  const caption = page.getByText("Onlineshop");
  await expect(caption).toBeVisible();

  const addToCartButton = page.getByText("In den Warenkorb");
  await expect(addToCartButton.first()).toBeVisible();
  await addToCartButton.first().click();

  const checkoutButton = page.getByText("Zur Kasse gehen");
  await checkoutButton.waitFor({ state: "visible" });
  await checkoutButton.click();

  const payByEmailButton = page.getByText("Email-Bezahlung");
  await payByEmailButton.waitFor({ state: "visible" });
  await payByEmailButton.click();

  const emailInput = page.locator("#email");
  await emailInput.waitFor({ state: "visible" });
  await emailInput.fill("max.mustermann@openknowledge.de");
  await page.getByText("Auswählen").last().click();

  await page.getByText("Rechnungsadresse").waitFor({ state: "visible" });
  await page.getByText("Auswählen").first().click();

  await page.getByText("Lieferadresse").waitFor({ state: "visible" });
  await page.getByText("Aus Rechnungsadresse übernehmen").click();

  await page
    .getByText("Kostenpflichtig Bestellen")
    .waitFor({ state: "visible" });
  await page.getByText("Kostenpflichtig Bestellen").click();

  await page
    .getByText("Bestellung erfolgreich abgeschlossen.")
    .waitFor({ state: "visible" });
});
