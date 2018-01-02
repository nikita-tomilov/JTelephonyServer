#!/bin/bash
# Oracle Java Setup
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
# Postgres Installation
sudo apt-get install postgresql postgresql-contrib
# Postgres Setup
echo Enter "superadmin" and "yes"
sudo -u postgres createuser --interactive
sudo -u postgres psql postgres -c "ALTER USER superadmin WITH PASSWORD '123456';"
