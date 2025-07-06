#!/bin/bash
cd "$(dirname "$0")/frontend" || exit 1

if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

echo "Starting frontend..."
npm run dev