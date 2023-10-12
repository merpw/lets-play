#!/bin/bash

echo "ADMIN_EMAIL=admin@mer.pw
ADMIN_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
PASSWORD_SALT=$(openssl rand -base64 32)" > .env