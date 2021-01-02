#!/usr/bin/env bash

openssl enc -aes-256-cbc -md sha512 -pbkdf2 -iter 100000 -salt -in myapps.jks.enc -out myapps.jks -k "$keystore_d" -d