---
description: 自动创建 GitHub Pull Request，包含 PR 描述和目标分支推断
---

## 背景

这个命令用于自动创建 GitHub Pull Request。它会：
1. 从当前分支和 git 历史自动推断 PR 目标分支
2. 分析未提交的更改，生成 PR 描述
3. 尝试使用 gh CLI 创建 PR，或输出手动创建的指令

## 执行步骤

### 1. 检查 Git 状态和分支信息

首先执行以下命令获取必要信息：
- `git branch --show-current` - 获取当前分支
- `git branch -r --list 'origin/main' 'origin/master' 'origin/develop' 'origin/dev'` - 检查远程分支
- `git log --oneline -10` - 获取最近提交历史
- `git diff --stat HEAD` - 获取未提交更改统计

### 2. 推断目标分支

根据以下规则确定目标分支：
- 如果当前分支名称包含 `feature/`、`feat/` → 目标分支通常是 `main`
- 如果当前分支名称包含 `fix/`、`bugfix/` → 目标分支通常是 `main`
- 如果当前分支名称包含 `hotfix/` → 目标分支通常是 `main`
- 如果当前分支名称是 `develop` 或 `dev` → 目标分支是 `main`
- 默认目标分支：`main`（如果不存在则尝试 `master`）

### 3. 生成 PR 描述

分析 git 历史和 diff 生成描述：

**格式**：
```
## Summary
- {简短描述变更内容}

## Changes
{文件变更统计}

## Test plan
[ ] 本地编译通过
[ ] 现有测试通过
[ ] 手动测试相关功能
```

### 4. 创建 PR

**如果 gh CLI 可用**：
使用以下命令创建 PR：
```bash
gh pr create --title "{PR标题}" --body "{PR描述}" --base {目标分支}
```

**如果 gh CLI 不可用**：
输出以下信息让用户手动创建：
- 仓库 URL
- 目标分支
- 源分支
- PR 标题建议
- PR 描述内容

## User Input

```text
$ARGUMENTS
```

如果用户提供了 PR 标题，使用它；否则自动生成。

## 输出

1. 目标分支
2. 当前分支
3. PR 标题（如果是中文项目，用中文描述）
4. PR 描述内容
5. 创建命令或手动创建指令
6. PR 创建后的 URL（如果成功）

## 错误处理

- 如果当前分支是 main/master 且没有上游分支，提示用户
- 如果有未提交的更改，提醒用户先提交
- 如果仓库没有 remote，提示配置 remote