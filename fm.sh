#!/bin/bash
# Format code
./mvnw spotless:apply
# Check style
./mvnw checkstyle:check
# Clean and compile
./mvnw clean compile test-compile