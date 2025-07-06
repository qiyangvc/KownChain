#!/bin/bash
echo "======================================="
echo "Initializing KnowChain Database..."
echo "======================================="
echo
echo "Please make sure MySQL is running on localhost:3306"
echo "Default root password will be used: 1234"
echo
read -p "Continue with database initialization? (Y/N): " confirm
if [[ "$confirm" != "Y" && "$confirm" != "y" ]]; then
    echo "Database initialization cancelled."
    exit 0
fi

echo
echo "Connecting to MySQL and creating database..."
mysql -u root -p123456 < "$(dirname "$0")/database/init.sql"

if [[ $? -eq 0 ]]; then
    echo
    echo "======================================="
    echo "Database initialization completed successfully!"
    echo "======================================="
    echo "Database: knowchain"
    echo "Test Users:"
    echo "  - admin / admin"
    echo "  - test / test"
    echo "======================================="
else
    echo
    echo "ERROR: Database initialization failed!"
    echo "Please check:"
    echo "1. MySQL server is running"
    echo "2. Root password is correct (123456)"
    echo "3. MySQL client is available in PATH"
fi