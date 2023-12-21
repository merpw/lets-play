#!/bin/bash

echo "ADMIN_EMAIL=admin@localhost
ADMIN_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
SERVER_NAME=localhost
SSL_CERT=\"\"
SSL_KEY=\"\"
" > .env