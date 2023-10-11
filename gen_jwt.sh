#!/bin/bash

# Generate a secret and public JWT keys and store them in src/main/resources directory
# Save old keys in src/main/resources/old_keys directory

cd src/main/resources

mkdir old_keys

mv jwt.* old_keys

# Generate new keys
openssl genrsa -out jwt.key 2048
openssl rsa -in jwt.key -pubout -outform PEM -out jwt.key.pub
