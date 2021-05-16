#!/usr/bin/env bash

openssl aes-256-cbc -d -a -in myapps.jks.enc -out myapps.jks -k "$keystore_d"