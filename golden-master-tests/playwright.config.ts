import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  globalSetup: "./global-setup",
  globalTeardown: "./global-teardown",
  use: {
    baseURL: process.env.ONLINE_SHOP_URL,
  },
});
