export default async function globalSetup() {
  process.env.ONLINE_SHOP_URL =
    process.env.ONLINE_SHOP_URL ?? "http://localhost";
}
