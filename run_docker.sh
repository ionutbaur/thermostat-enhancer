#!/bin/bash

if docker compose version &>/dev/null 2>&1; then
  DC="docker compose"
else
  DC="docker-compose"
fi

$DC -f docker-compose.yml up -d