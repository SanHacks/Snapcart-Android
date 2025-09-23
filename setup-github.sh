#!/bin/bash

# GitHub Repository Setup Script for Snap Cart
# Usage: ./setup-github.sh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}    Snap Cart GitHub Setup${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""

# Repository details
REPO_NAME="Snapcart-Android"
USERNAME="SanHacks"
DESCRIPTION="🛒 Snap Cart - Modern Android WebView shopping app with camera, location, and file upload support"

print_status "Repository Configuration:"
echo "  Name: $REPO_NAME"
echo "  Owner: $USERNAME"
echo "  Description: $DESCRIPTION"
echo ""

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    print_error "Not in a Git repository. Please run this from the project root."
    exit 1
fi

# Check if we have commits
if ! git rev-parse HEAD >/dev/null 2>&1; then
    print_error "No commits found. Please commit your changes first."
    exit 1
fi

print_status "Setting up GitHub remote repository..."

# Add GitHub remote
if git remote get-url origin >/dev/null 2>&1; then
    print_warning "Remote 'origin' already exists:"
    git remote get-url origin
    read -p "Do you want to update it? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git remote set-url origin git@github.com:$USERNAME/$REPO_NAME.git
        print_success "Remote origin updated"
    fi
else
    git remote add origin git@github.com:$USERNAME/$REPO_NAME.git
    print_success "Remote origin added: git@github.com:$USERNAME/$REPO_NAME.git"
fi

echo ""
print_warning "IMPORTANT: Before pushing, you need to create the repository on GitHub!"
echo ""
echo "Steps to create the repository:"
echo "1. Go to: https://github.com/new"
echo "2. Repository name: $REPO_NAME"
echo "3. Description: $DESCRIPTION"
echo "4. Set to Public (recommended for portfolio)"
echo "5. DO NOT initialize with README, .gitignore, or license"
echo "6. Click 'Create repository'"
echo ""
read -p "Have you created the repository on GitHub? (y/N): " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_status "Pushing to GitHub..."
    
    # Push to GitHub
    git push -u origin main
    
    if [ $? -eq 0 ]; then
        print_success "Successfully pushed Snap Cart to GitHub!"
        echo ""
        echo "🛒 Your Snap Cart repository is now available at:"
        echo "   https://github.com/$USERNAME/$REPO_NAME"
        echo ""
        echo "📋 Next steps:"
        echo "   • Add repository description and topics on GitHub"
        echo "   • Consider adding GitHub Actions for automated builds"
        echo "   • Update README with live demo links"
        echo "   • Set up branch protection rules if needed"
        echo ""
    else
        print_error "Failed to push to GitHub. Please check your SSH keys and repository settings."
        exit 1
    fi
else
    print_warning "Repository not created yet. Run this script again after creating the repository."
    echo ""
    echo "Quick commands when ready:"
    echo "  git push -u origin main"
fi
