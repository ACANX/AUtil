# AGENTS.md

面向 AI 编码助手的项目约定。

## 语言约定

未特别说明时，**一律使用简体中文**，适用范围包括：

- 对话回复
- 文档编写（Markdown、README、注释性说明等）
- 代码注释与 Javadoc
- 图表中的标注、图例、轴标签

例外：代码标识符（类名、方法名、变量名）、提交信息的 type 前缀（`feat:`/`chore:` 等）、日志与异常中面向机器解析的部分，保持既有英文习惯。

## Java 测试方法名约定（强制）

**Java 单元测试方法名禁止使用简体中文汉字，必须使用英文。**

- ✅ 正确：`serializePrettyIndent2()`、`prettyFormatLineEndingsAlwaysLf()`
- ❌ 错误：`serializePretty缩进2()`、`toJSONStringPrettyFormat换行符固定LF()`
- JUnit 方法名无外部引用，一律使用英文（小驼峰），即使测试场景描述用中文也仅体现在方法体内注释
- 历史教训：2026-08-24 跨平台差异修复中再次出现中文测试方法名，违反此前约定；存量违规（27 个，7 个测试文件）需逐步清理

需要用其他语言时会明确说明。

## 文件命名约定

**新创建**的 Markdown 文档、配图文件及文档相关文件，文件名默认采用**大驼峰（PascalCase）**：

```
QuickStart.md          ArchitectureDiagram.svg
CopierUsage.md         ModuleDependency.png
```

例外（沿用生态既有惯例，不改写）：

- 工具链或平台按固定名识别的文件：`README.md`、`README_CN.md`、`AGENTS.md`、`LICENSE`、`CHANGELOG.md`、`CONTRIBUTING.md`、`.github/`、`.gitee/` 下的模板文件等
- 仓库中已存在的文件保持原名，本约定只作用于新建文件

## 文档目录约定

| 目录 | 存放内容 |
| --- | --- |
| `Docs/` | 项目文档统一存放位置 |
| `Docs/DevSpec/` | 开发规范 |
| `Docs/Asset/` | 文档相关资产（配图、图标、附件等） |

新建文档时按此归位，不要散落在各模块根目录或仓库根目录。

注意：本机为 Windows/NTFS，不区分大小写，`Docs/` 与 `docs/` 无法共存。原小写 `docs/` 已重命名为 `Docs/`，不要再新建小写形式。

## 远端约定

本仓库配置了两个远端：

| 远端 | 地址 | 定位 |
| --- | --- | --- |
| `github` | https://github.com/ACANX/AUtil.git | **主远端 / 上游**。默认目标 |
| `gitee` | https://gitee.com/ACANX/AUtil.git | 备份 / 传输加速 |

**规则：**

- 未特别说明时，“远端”“上游”“推送”一律指 **`github`**。
- 只有明确说了 “gitee” 才推送到 gitee。
- 二者都要时会明确说明；此时保持两端同一提交，避免长期分叉。

`push.default` 已配置为 `github`，与上述约定一致。

## 远端操作注意事项

### 不要使用 `git fetch --prune`

两个远端的 fetch refspec 都指向**同一个** `refs/remotes/origin/*` 命名空间：

```
remote.github.fetch = +refs/heads/*:refs/remotes/origin/*
remote.gitee.fetch  = +refs/heads/*:refs/remotes/origin/*
```

因此 `git fetch <remote> --prune` 会把**仅存在于另一个远端**的分支引用误删。请用不带 `--prune` 的 fetch。

同理，`origin/<branch>` 指向的是最后一次 fetch 的那个远端，并不稳定。需要确定某个远端的真实状态时，用 `git ls-remote <remote>` 直接查询，不要依赖 `origin/*`。

### 两端分支集合并不相同

分支未必两端都有（例如 `V1.3.x` 线只在 `github` 上）。推送或建分支前先用 `git ls-remote` 确认目标远端确实存在该分支。

## 推送安全

推送前确认是快进：

```sh
git merge-base --is-ancestor <remote-ref> HEAD
```

非快进时先说明情况，不要直接强推。

## 开发规范文档

`Docs/DevSpec/` 下的规范需遵照执行：

- [`GitCommitSpec.md`](Docs/DevSpec/GitCommitSpec.md) —— Git 提交规范。**涉及纯大小写改名时，必须借中转名拆成两个提交**，否则他人在大小写不敏感的文件系统上 clone/checkout 会遇到路径冲突警告。
