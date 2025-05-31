#!/bin/bash

# 사용법: ./scripts/create-pr.sh <issue_number> "<PR 타이틀>"
# 예시: ./scripts/create-pr.sh 123 "feat: add new feature #123"

ISSUE_NUMBER=$1
PR_TITLE=$2

if [ -z "$ISSUE_NUMBER" ] || [ -z "$PR_TITLE" ]; then
    echo "사용법: $0 <issue_number> \"<PR 타이틀>\""
    exit 1
fi

# 이슈 내용 가져오기
ISSUE_BODY=$(gh issue view $ISSUE_NUMBER --json body --jq '.body')

# PR 생성
gh pr create \
    --title "$PR_TITLE" \
    --body "$ISSUE_BODY

---

<!-- 여기에 추가 설명을 작성하세요 -->"

echo "PR이 생성되었습니다!" 