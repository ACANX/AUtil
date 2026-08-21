# Git 提交规范

本文档记录本项目的 Git 提交规范，后续提交需遵照执行。

## 涉及文件/目录大小写改名的提交，必须拆成两个中转提交

### 规则

当改名**仅涉及大小写变化**时（如 `docs/` → `Docs/`、`readme.md` → `Readme.md`），**禁止在单个提交内完成**。必须借助一个中转名，拆成两个提交：

```sh
# 第 1/2 步：改为一个与原名、目标名都不同的中转名
git mv docs docs-rename-tmp
git commit -m "refactor: 重命名docs/为中转名docs-rename-tmp/(大小写改名第1/2步)"

# 第 2/2 步：中转名改为目标名
git mv docs-rename-tmp Docs
git commit -m "refactor: 中转名docs-rename-tmp/重命名为Docs/(大小写改名第2/2步)"
```

### 原因

单个提交里做纯大小写改名，别人在大小写不敏感的文件系统（Windows/NTFS、macOS/APFS 默认配置）上 clone 或 checkout 时，git 会因为同时看到 `docs/` 和 `Docs/` 两个路径而报冲突或警告。

拆成两个中转提交后，每个提交都是**不同名之间**的改名，任何单个提交都不含纯大小写变更，因此不会触发该问题。

### 反例

```sh
# 错误：单个提交内完成纯大小写改名
git mv docs docs-tmp && git mv docs-tmp Docs
git commit -m "refactor: 将docs/重命名为Docs/"
```

工作区分两步操作、最后压成**一个**提交，等于没拆 —— 问题出在提交的内容上，不在操作手法上。历史中必须保留两个提交才有效。

### 不要与其他变更合并

大小写改名的两个提交应保持纯净，不要夹带新增文件、内容修改等无关变更，便于出问题时单独回退。

### 验证

改完后逐提交检查，确认没有任何提交存在纯大小写改名：

```sh
for c in $(git rev-list <base>..HEAD); do
  echo "--- $(git log -1 --format='%h %s' $c) ---"
  git show --name-status --format="" $c
done
```

每条 `R` 记录的前后两个路径应当是**不同的名字**，而不是同一名字的大小写变体。相似度应为 `R100`，表示内容逐字节未变。

### 本项目实测案例

`docs/` → `Docs/` 的改名即按此规范执行：

| 提交 | 路径变化 |
| --- | --- |
| `2ce085b` | `docs/` → `docs-rename-tmp/` |
| `f35f953` | `docs-rename-tmp/` → `Docs/` |
