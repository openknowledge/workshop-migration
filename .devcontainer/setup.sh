#!/bin/bash
set -e

if [ -n "$CODESPACE_NAME" ]; then
  DOMAIN="${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN:-app.github.dev}"
  cat > "$(dirname "$0")/../.env" << ENVEOF
ONLINE_SHOP_EXTERNAL_URL=https://${CODESPACE_NAME}-80.${DOMAIN}
CHECKOUT_EXTERNAL_URL=https://${CODESPACE_NAME}-4001.${DOMAIN}
ENVEOF
  echo "Codespaces URLs configured:"
  echo "  Online Shop:       https://${CODESPACE_NAME}-80.${DOMAIN}"
  echo "  Checkout Service:  https://${CODESPACE_NAME}-4001.${DOMAIN}"
fi
