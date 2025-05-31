#!/bin/bash

# 사용법: ./scripts/create-branch.sh <issue_number> "<간단한 설명>"
# 예시: ./scripts/create-branch.sh 123 "add-new-feature"

ISSUE_NUMBER=$1
DESCRIPTION=$2

if [ -z "$ISSUE_NUMBER" ] || [ -z "$DESCRIPTION" ]; then
    echo "사용법: $0 <issue_number> \"<간단한 설명>\""
    echo "예시: $0 123 \"add-new-feature\""
    exit 1
fi

# 브랜치명 생성 (#포함)
BRANCH_NAME="feature/#${ISSUE_NUMBER}-${DESCRIPTION}"

# 브랜치 생성 및 체크아웃
git checkout -b "$BRANCH_NAME"

echo "브랜치 '$BRANCH_NAME' 생성 완료!"
echo "GitHub에서 PR 생성 시 제목에 자동으로 #${ISSUE_NUMBER}이 포함됩니다." 