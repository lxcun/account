---
name: git-commit-helper
description: Commit all staged files to the current branch with an appropriate commit message. Use when the user asks to commit, submit, or save changes in Chinese ("帮我提交一下", "提交代码", "保存修改") or English ("commit", "submit changes").
---

# Git Commit Helper

Automatically commit all staged files to the current branch with a meaningful commit message.

**IMPORTANT**: All commits must include a JIRA issue prefix in the format `JIRA:YLHY120DD-{code}`.

## Instructions

When the user asks to commit changes, follow these steps:

### Step 1: Check current git status

Run `git status` to see:
- Staged files (green, ready to commit)
- Unstaged changes
- Current branch name
- Branch status (ahead, behind, diverged)

### Step 2: Ask for JIRA issue number

**Always ask the user for the JIRA issue code before proceeding.**

Use this prompt to collect the JIRA issue number:
```
请提供对应的 JIRA 问题编号（例如：423213）：

提交将使用前缀：JIRA:YLHY120DD-{你提供的编号}
```

Wait for the user to provide the JIRA issue code before continuing.

### Step 3: Run git diff to understand changes

Run the following commands in parallel:
```bash
git diff --staged
git log -5 --oneline
```

### Step 4: Analyze and draft commit message

Based on the changes, draft a commit message following this format:

```
JIRA:YLHY120DD-{code} <brief summary in Chinese>

<optional detailed explanation>
```

**Guidelines for commit message**:
- **MUST start with** `JIRA:YLHY120DD-{code}` where `{code}` is the user-provided issue number
- Use Chinese for summary (matches project style)
- Focus on "why" not "what"
- Keep summary (after JIRA prefix) under 50 characters
- Use imperative mood
- For bug fixes: "修复 xxx 问题"
- For features: "实现 xxx 功能"
- For refactoring: "重构 xxx 模块"

### Step 5: Create the commit

Choose the appropriate method based on your shell:

**For bash/zsh (most common):**
```bash
git commit -m "$(cat <<'EOF'
JIRA:YLHY120DD-{code} <commit message here>
EOF
)"
```

**For sh/POSIX shell (more portable):**
```bash
git commit -m "JIRA:YLHY120DD-{code} <commit message here>"
```

**Or use multiple -m flags (works everywhere):**
```bash
git commit -m "JIRA:YLHY120DD-{code} <commit message here>" -m "<optional detailed explanation>"
```

### Step 6: Verify success

Run `git status` to confirm:
- Files were committed
- Branch status updated

## Important Rules

1. **JIRA prefix is mandatory**: Always ask user for JIRA issue code and include `JIRA:YLHY120DD-{code}` prefix
2. **NEVER skip hooks**: Always use full `git commit` without `--no-verify`
3. **NEVER force push**: Never use `--force` unless explicitly requested
4. **Respect branch protection**: Warn if pushing to main/master
5. **Handle hook failures**: If commit fails due to hooks, fix issues and create a NEW commit
6. **No changes = no commit**: If nothing is staged, inform the user

## Edge Cases

**No staged changes**:
```
没有文件在暂存区。请先用 git add 添加要提交的文件。
```

**Commit rejected by hooks**:
1. Read the hook error message
2. Fix the issues
3. Create a new commit (do NOT use --amend)

**Pre-commit hook modified files**:
- The hook will auto-stage modified files
- Create a new commit to include them

**Main/master branch**:
```
⚠️ 警告: 当前在 main 分支。确定要直接提交吗？
```

## Examples

### Simple bug fix
```bash
git status  # Shows: Fix UserValidator.java
git diff --staged  # Shows: Added null check
git log -5 --oneline  # Shows recent style
# User provides JIRA code: 423213
```

Commit message:
```
JIRA:YLHY120DD-423213 修复 UserValidator 空指针检查

```

### Feature implementation
```
JIRA:YLHY120DD-445566 实现 WebSocket Agent API

新增 /agent/ws 端点，支持实时双向通信。

```

## Requirements

- git must be installed
- Must be in a git repository
- Files must be staged before committing