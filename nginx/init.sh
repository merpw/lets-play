#!/bin/bash

# Configure nginx to use HTTPS if SSL_CERT and SSL_KEY are set, otherwise use HTTP

if [ -z "$SSL_CERT" ] || [ -z "$SSL_KEY" ]; then
  mv /etc/nginx/http.conf /etc/nginx/nginx.conf
  exit 0
fi

mkdir -p /etc/nginx/ssl

echo "$SSL_CERT" > /etc/nginx/ssl/cert.pem
echo "$SSL_KEY" > /etc/nginx/ssl/key.pem

mv /etc/nginx/https.conf /etc/nginx/nginx.conf