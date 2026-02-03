#!/bin/bash

# Git Push Script for r2mo-rapid
# This script commits the r2mo-spec submodule first, then commits the current project
# Usage: ./git-push.sh "commit message"

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if commit message is provided
if [ -z "$1" ]; then
    echo -e "${RED}Error: Commit message is required${NC}"
    echo "Usage: $0 \"commit message\""
    exit 1
fi

# Check if commit message is not empty (not just whitespace)
if [[ ! "$1" =~ [^[:space:]] ]]; then
    echo -e "${RED}Error: Commit message cannot be empty or contain only whitespace${NC}"
    echo "Usage: $0 \"commit message\""
    exit 1
fi

COMMIT_MSG="$1"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Starting Git Push Process${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "Commit message: ${YELLOW}${COMMIT_MSG}${NC}"
echo ""

# Step 1: Commit r2mo-spec submodule
echo -e "${GREEN}[1/4] Processing submodule: r2mo-spec${NC}"
cd r2mo-spec

# Check if there are changes in r2mo-spec
if [ -n "$(git status --porcelain)" ]; then
    echo "  - Adding all changes..."
    git add .

    echo "  - Committing changes..."
    git commit -m "$COMMIT_MSG"

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  ✓ r2mo-spec committed successfully${NC}"

        echo "  - Pushing to remote..."
        git push

        if [ $? -eq 0 ]; then
            echo -e "${GREEN}  ✓ r2mo-spec pushed successfully${NC}"
        else
            echo -e "${RED}  ✗ Failed to push r2mo-spec${NC}"
            cd ..
            exit 1
        fi
    else
        echo -e "${RED}  ✗ Failed to commit r2mo-spec${NC}"
        cd ..
        exit 1
    fi
else
    echo -e "${YELLOW}  ⚠ No changes to commit in r2mo-spec${NC}"
fi

cd ..

# Step 2: Add the submodule reference change to parent
echo ""
echo -e "${GREEN}[2/4] Updating submodule reference in parent${NC}"
git add r2mo-spec

# Step 3: Commit current project (r2mo-rapid)
echo ""
echo -e "${GREEN}[3/4] Processing current project: r2mo-rapid${NC}"

# Check if there are changes in current project
if [ -n "$(git status --porcelain)" ]; then
    echo "  - Adding all changes..."
    git add .

    echo "  - Committing changes..."
    git commit -m "$COMMIT_MSG"

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  ✓ r2mo-rapid committed successfully${NC}"
    else
        echo -e "${RED}  ✗ Failed to commit r2mo-rapid${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}  ⚠ No changes to commit in r2mo-rapid${NC}"
fi

# Step 4: Push current project
echo ""
echo -e "${GREEN}[4/4] Pushing r2mo-rapid to remote${NC}"
git push

if [ $? -eq 0 ]; then
    echo -e "${GREEN}  ✓ r2mo-rapid pushed successfully${NC}"
else
    echo -e "${RED}  ✗ Failed to push r2mo-rapid${NC}"
    exit 1
fi

# Success message
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ All operations completed successfully${NC}"
echo -e "${GREEN}========================================${NC}"
