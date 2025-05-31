#!/bin/bash

# 사용법: ./scripts/create-pr.sh <issue_number> [추가_설명]
# 예시: ./scripts/create-pr.sh 123 "add user authentication"

ISSUE_NUMBER=$1
ADDITIONAL_DESC=$2

if [ -z "$ISSUE_NUMBER" ]; then
    echo "사용법: $0 <issue_number> [추가_설명]"
    exit 1
fi

# 이슈 정보 가져오기
ISSUE_TITLE=$(gh issue view $ISSUE_NUMBER --json title --jq '.title')
ISSUE_BODY=$(gh issue view $ISSUE_NUMBER --json body --jq '.body')

# PR 타이틀 자동 생성
if [ -n "$ADDITIONAL_DESC" ]; then
    PR_TITLE="$ISSUE_TITLE #$ISSUE_NUMBER - $ADDITIONAL_DESC"
else
    PR_TITLE="$ISSUE_TITLE #$ISSUE_NUMBER"
fi

# PR 생성
gh pr create \
    --title "$PR_TITLE" \
    --body "$ISSUE_BODY

---

<!-- 여기에 추가 설명을 작성하세요 -->"

echo "PR이 생성되었습니다!"
echo "제목: $PR_TITLE" 