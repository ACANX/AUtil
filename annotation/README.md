# Annotation 模块

自定义注解及注解处理器模块，提供编译期代码生成功能。

## 模块结构

```
annotation/
├── annotation-api/                  # 注解定义模块
│   └── src/main/java/
│       └── com/acanx/util/incubator/annotation/
│           └── Copier.java          # 对象拷贝注解
├── annotation-processor/            # 注解处理器模块（ASM 模式）
│   └── src/main/java/
│       └── com/acanx/module/annotation/processor/copier/
│           └── CopierProcessor.java # 对象拷贝注解处理器
├── annotation-processor-ast/        # 注解处理器模块（BYTECODE 模式）
│   └── src/main/java/
│       └── com/acanx/module/annotation/processor/copier/ast/
│           └── CopierAstProcessor.java # AST 字节码增强处理器
├── annotation-bytebuddy/            # 字节码增强模块（备选方案）
└── annotation-test/                 # 测试验证模块
    └── src/main/java/
        └── com/acanx/test/
            ├── Person.java          # 测试数据类
            ├── Address.java         # 测试数据类
            └── Test.java            # 测试用例
```

## 实现思路

### 1. 注解处理器原理

注解处理器（Annotation Processor）是 Java 提供的一种在编译期扫描和处理注解的工具。它的工作流程如下：

```
源代码 (.java) 
    → 编译期 
    → 注解处理器扫描 @Copier 注解 
    → 分析被注解方法的参数 
    → 生成辅助类代码 
    → 编译器编译生成的代码 
    → 字节码 (.class)
```

### 2. CopierProcessor 核心流程

1. **扫描注解**：通过 `@SupportedAnnotationTypes` 声明处理的注解类型
2. **获取方法信息**：从 `RoundEnvironment` 获取被 `@Copier` 注解的方法
3. **分析参数**：提取源对象（src）和目标对象（dest）的类型信息
4. **收集字段**：递归获取类的所有字段（包括父类）
5. **过滤字段**：根据 `include`/`exclude` 配置过滤需要拷贝的字段
6. **生成代码**：使用 JavaPoet 生成辅助类，包含字段拷贝逻辑
7. **注册处理器**：通过 `META-INF/services/javax.annotation.processing.Processor` 注册

### 3. 代码生成策略

- **基本类型/String**：直接赋值
- **集合类型**：创建新集合并复制（ArrayList/HashSet/HashMap）
- **数组类型**：使用 `clone()` 方法
- **复杂对象**：
  - 浅拷贝：直接赋值引用
  - 深拷贝：创建新对象并递归拷贝（需要目标类有 `copyTo` 方法）

### 4. 关键技术点

- **JavaPoet**：用于生成类型安全的 Java 源代码
- **SPI 机制**：通过 `META-INF/services` 自动注册注解处理器
- **类型处理**：使用 `Types` 和 `Elements` 工具类分析类型信息
- **代码生成位置**：生成的代码放在 `target/generated-sources/annotations/` 目录

---

## 两种生成模式切换

`@Copier` 注解提供了 `generationMode` 参数，支持两种代码生成模式：

| 模式 | 说明 | 兼容性 | 推荐场景 |
|------|------|--------|----------|
| `GenerationMode.BYTECODE` (默认) | 直接修改方法字节码 | ⚠️ 仅 javac | 需要无感知增强 |
| `GenerationMode.ASM` | 生成独立辅助类 | ✅ 所有编译器 | 通用场景 |

### 模式一：BYTECODE 字节码增强模式（默认，类似 Lombok）

**原理**：在编译期直接操作 AST（抽象语法树），修改原有方法的实现。

**配置方式**：
```java
@Copier  // 默认就是 BYTECODE 模式
public void copy(Person src, Person dest) { }
```

**调用方式**：
```java
// 直接调用原方法，无感知增强
copy(src, dest);
```

**优点**：
- ✅ 真正的编译期增强
- ✅ 调用方无感知，直接使用原方法
- ✅ 代码更简洁
- ✅ 自动添加参数判空检查
- ✅ 自动添加字段判空检查
- ✅ 集合类型自动深拷贝

**缺点**：
- ⚠️ 仅支持 javac 编译器，不兼容 ECJ（Eclipse Compiler）
- ⚠️ 使用非官方 API (`com.sun.tools.javac.*`)，可能在不同 JDK 版本间不兼容
- ⚠️ 需要配置 Maven 模块开放参数

---

### 模式二：ASM 辅助类模式

**原理**：生成独立的辅助类（如 `TestCopier`），包含所有拷贝逻辑。

**配置方式**：
```java
@Copier(generationMode = GenerationMode.ASM)  // 显式指定 ASM 模式
public void copy(Person src, Person dest) { }
```

**调用方式**：
```java
// 需要调用生成的辅助类方法
TestCopier.copy(src, dest);
```

**优点**：
- ✅ 兼容所有 Java 编译器（javac、ECJ 等）
- ✅ 使用官方 API，稳定可靠
- ✅ 生成的代码可见，便于调试
- ✅ Maven 配置简单

**缺点**：
- ⚠️ 需要调用辅助类，不是直接调用原方法
- ⚠️ 代码调用方式不够简洁

---

### BYTECODE 模式 Maven 配置（默认模式）

```xml
<dependencies>
    <!-- 注解 API -->
    <dependency>
        <groupId>com.acanx.module.annotation</groupId>
        <artifactId>annotation-api</artifactId>
        <version>0.4.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>21</source>
                <target>21</target>
                <encoding>UTF-8</encoding>
                <fork>true</fork>
                <annotationProcessorPaths>
                    <!-- ASM 模式处理器（必需） -->
                    <path>
                        <groupId>com.acanx.module.annotation</groupId>
                        <artifactId>annotation-processor</artifactId>
                        <version>0.4.1</version>
                    </path>
                    <!-- BYTECODE 模式处理器（仅当使用 BYTECODE 模式时需要） -->
                    <path>
                        <groupId>com.acanx.module.annotation</groupId>
                        <artifactId>annotation-processor-ast</artifactId>
                        <version>0.4.1</version>
                    </path>
                </annotationProcessorPaths>
                <!-- 开放 javac 内部模块访问（BYTECODE 模式必需） -->
                <compilerArgs>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**注意**：使用 BYTECODE 模式时，还需要在 Maven 启动时设置模块开放参数：

```bash
# Windows (CMD)
set MAVEN_OPTS=--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
mvn clean compile

# Linux/Mac
export MAVEN_OPTS="--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED"
mvn clean compile
```

**示例代码位置**：`annotation-processor-ast` 模块

```java
// 使用 javac 内部 API 修改 AST
@SupportedAnnotationTypes("com.acanx.util.annotation.Copier")
public class CopierAstProcessor extends AbstractProcessor {
    private TreeMaker treeMaker;
    private Names names;
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacProcessingEnvironment javacEnv = (JavacProcessingEnvironment) processingEnv;
        this.treeMaker = TreeMaker.instance(javacEnv.getContext());
        this.names = Names.instance(javacEnv.getContext());
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 获取方法 AST 并修改方法体
        for (Element element : roundEnv.getElementsAnnotatedWith(Copier.class)) {
            Copier copier = element.getAnnotation(Copier.class);
            if (copier.generationMode() != GenerationMode.BYTECODE) {
                continue; // 仅处理 BYTECODE 模式
            }
            
            // 使用 Trees API 获取 AST
            TreePath path = trees.getPath(element);
            JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) path.getLeaf();
            
            // 直接替换方法体
            methodDecl.body = generateCopyBody(...);
        }
        return true;
    }
}
```

### 方案 B：使用字节码增强（推荐）

**原理**：在编译完成后，使用 ASM 或 ByteBuddy 修改 `.class` 文件的字节码。

**优点**：
- ✅ 兼容所有 Java 编译器（javac、ECJ 等）
- ✅ 使用官方支持的字节码操作库
- ✅ 实现相对简单，维护成本低

**缺点**：
- 编译后处理，非严格意义上的编译期
- 需要额外的 Maven/Gradle 插件配置

**示例配置**：`annotation-bytebuddy` 模块

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>net.bytebuddy</groupId>
        <artifactId>byte-buddy</artifactId>
        <version>1.14.12</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- ByteBuddy Maven 插件 - 在编译后修改字节码 -->
        <plugin>
            <groupId>net.bytebuddy</groupId>
            <artifactId>byte-buddy-maven-plugin</artifactId>
            <version>1.14.12</version>
            <executions>
                <execution>
                    <goals>
                        <goal>transform</goal>
                    </goals>
                    <phase>process-classes</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

```java
// 字节码增强实现
public class CopierTransformer implements BuilderListener {
    @Override
    public void onTransform(TypeDescription typeDescription, DynamicType dynamicType) {
        // 查找带有@Copier 注解的方法并修改其字节码
        for (MethodDescription method : typeDescription.getDeclaredMethods()) {
            if (method.getDeclaredAnnotations()
                    .isAnnotationPresent(Copier.class)) {
                // 使用 ByteBuddy 重新定义方法实现
            }
        }
    }
}
```

### 模式对比

| 特性 | ASM 辅助类模式 | BYTECODE 字节码增强模式 |
|------|---------------|------------------------|
| **编译器兼容性** | ✅ 所有编译器 | ❌ 仅 javac |
| **API 稳定性** | ✅ 官方 API | ⚠️ 非官方 API（javac 内部） |
| **实现复杂度** | ✅ 简单 | ⚠️ 复杂 |
| **调用方式** | ⚠️ 需调用辅助类 | ✅ 直接调用原方法 |
| **代码可见性** | ✅ 生成的代码可见 | ❌ 直接修改字节码 |
| **调试友好性** | ✅ 易于调试 | ⚠️ 调试较复杂 |
| **判空检查** | ✅ 支持 | ✅ 支持（自动添加） |
| **集合深拷贝** | ✅ 支持 | ✅ 支持（ArrayList） |
| **Maven 配置** | ✅ 简单 | ⚠️ 需要额外配置 |
| **推荐使用场景** | 通用场景/需要兼容 ECJ | 需要无感知增强 |

### 推荐使用策略

1. **通用场景**：使用 **BYTECODE 字节码增强模式**（默认，类似 Lombok，直接修改方法体）
2. **需要兼容所有编译器**：使用 **ASM 辅助类模式**（稳定、兼容性好）
3. **特殊需求**：仅在完全了解风险时使用 javac 内部 API

## 使用方法

### 1. 添加依赖

#### BYTECODE 模式（默认，推荐）

在项目的 `pom.xml` 中添加：

```xml
<dependencies>
    <!-- 注解 API -->
    <dependency>
        <groupId>com.acanx.module.annotation</groupId>
        <artifactId>annotation-api</artifactId>
        <version>0.4.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>21</source>
                <target>21</target>
                <encoding>UTF-8</encoding>
                <fork>true</fork>
                <annotationProcessorPaths>
                    <!-- BYTECODE 模式处理器（默认，必需） -->
                    <path>
                        <groupId>com.acanx.module.annotation</groupId>
                        <artifactId>annotation-processor-ast</artifactId>
                        <version>0.4.1</version>
                    </path>
                    <!-- ASM 模式处理器（仅当使用 ASM 模式时需要） -->
                    <path>
                        <groupId>com.acanx.module.annotation</groupId>
                        <artifactId>annotation-processor</artifactId>
                        <version>0.4.1</version>
                    </path>
                </annotationProcessorPaths>
                <compilerArgs>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**注意**：使用 BYTECODE 模式时，还需要配置模块开放参数。项目根目录已包含 `.mvn/jvm.config` 文件，IDEA 会自动识别。如需手动配置：

```bash
# Windows (CMD)
set MAVEN_OPTS=--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
```

#### ASM 模式（备选，兼容性好）

如果需要使用 ASM 模式（生成辅助类），配置如下：

```xml
<dependencies>
    <!-- 注解 API -->
    <dependency>
        <groupId>com.acanx.module.annotation</groupId>
        <artifactId>annotation-api</artifactId>
        <version>0.4.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <!-- ASM 模式处理器 -->
                    <path>
                        <groupId>com.acanx.module.annotation</groupId>
                        <artifactId>annotation-processor</artifactId>
                        <version>0.4.1</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. 定义数据类

```java
@Data
public class Address {
    private String street;
    private String city;
    private String zipCode;
}

@Data
public class Person {
    private String name;
    private Integer age;
    private Address address;
    private List<String> hobbies;
    private String email;
}
```

### 3. 使用 @Copier 注解

```java
import com.acanx.util.annotation.Copier;

public class PersonService {
    
    /**
     * 标准拷贝 - 所有字段直接赋值
     */
    @Copier
    public void copy(Person src, Person dest) {
        // 空方法，编译后自动生成拷贝代码
    }
    
    /**
     * 忽略空值拷贝 - 源对象为 null 的字段不会覆盖目标对象
     */
    @Copier(ignoreNull = true)
    public void copyIgnoreNull(Person src, Person dest) { }
    
    /**
     * 排除字段拷贝 - 不拷贝 email 和 hobbies 字段
     */
    @Copier(exclude = {"email", "hobbies"})
    public void copyExclude(Person src, Person dest) { }
    
    /**
     * 仅包含字段拷贝 - 只拷贝 name 和 age 字段
     */
    @Copier(include = {"name", "age"})
    public void copyInclude(Person src, Person dest) { }
    
    /**
     * 深拷贝 - 对复杂对象创建新实例并递归拷贝
     */
    @Copier(strategy = Copier.CopyStrategy.DEEP)
    public void deepCopy(Person src, Person dest) { }
}
```

### 4. 生成的代码

#### ASM 模式

编译后，在 `target/generated-sources/annotations/` 目录下会生成辅助类：

**核心规则：**
- 同一个类的所有 `@Copier` 方法生成在**同一个辅助类**中
- 类名 = 原类名 + `Copier`
- 方法名 = 原方法名

**生成示例：**

| 原方法 | 生成类名   | 生成方法名 |
|--------|--------|------------|
| `Test.copy()` | `Test` | `copy()` |
| `Test.copyIgnoreNull()` | `Test` | `copyIgnoreNull()` |
| `Test.copyExclude()` | `Test` | `copyExclude()` |
| `Test.copyInclude()` | `Test` | `copyInclude()` |

**生成的代码示例：**

```java
// Test.class - 同一个类的所有@Copier 方法都在这个类中
package com.acanx.test;

import java.util.ArrayList;

public class Test {
    public void copy(Person src, Person dest) {
        if (null != src && null != dest) {
            if (null != src.getAddress()) {
                dest.setAddress(src.getAddress());
            }

            if (null != src.getHobbies()) {
                dest.setHobbies(src.getHobbies());
            }

            if (null != src.getName()) {
                dest.setName(src.getName());
            }

            if (null != src.getAge()) {
                dest.setAge(src.getAge());
            }

            if (null != src.getEmail()) {
                dest.setEmail(src.getEmail());
            }

        }
    }

    public void copyNonNull(Person src, Person dest) {
        if (null != src && null != dest) {
            if (null != src.getAddress()) {
                dest.setAddress(src.getAddress());
            }

            if (null != src.getHobbies()) {
                dest.setHobbies(src.getHobbies());
            }

            if (null != src.getName()) {
                dest.setName(src.getName());
            }

            if (null != src.getAge()) {
                dest.setAge(src.getAge());
            }

            if (null != src.getEmail()) {
                dest.setEmail(src.getEmail());
            }

        }
    }

    public void copyIgnoreNull(Person src, Person dest) {
        if (null != src && null != dest) {
            if (null != src.getAddress()) {
                dest.setAddress(src.getAddress());
            }

            if (null != src.getHobbies()) {
                dest.setHobbies(src.getHobbies());
            }

            if (null != src.getName()) {
                dest.setName(src.getName());
            }

            if (null != src.getAge()) {
                dest.setAge(src.getAge());
            }

            if (null != src.getEmail()) {
                dest.setEmail(src.getEmail());
            }

        }
    }

    public void copyExclude(Person src, Person dest) {
        if (null != src && null != dest) {
            if (null != src.getAddress()) {
                dest.setAddress(src.getAddress());
            }

            if (null != src.getName()) {
                dest.setName(src.getName());
            }

            if (null != src.getAge()) {
                dest.setAge(src.getAge());
            }

        }
    }

    public void copyInclude(Person src, Person dest) {
        if (null != src && null != dest) {
            if (null != src.getName()) {
                dest.setName(src.getName());
            }

            if (null != src.getAge()) {
                dest.setAge(src.getAge());
            }

        }
    }
}
```

#### BYTECODE 模式

编译后，原方法的字节码会被直接修改，不会生成辅助类。使用 `javap -c` 查看编译后的 class 文件：

```bash
javap -c target/classes/com/acanx/test/Test.class
```

**编译前（源代码）：**
```java
@Copier
public void copy(Person src, Person dest) {
    // 空方法
}
```

**编译后（字节码增强）：**

```java
public void copy(Person src, Person dest) {
    // 1. 参数判空检查
    if (null == src || null == dest) return;
    
    // 2. 引用类型字段拷贝（带判空和大括号）
    if (null != src.getAddress()) {
        dest.setAddress(src.getAddress());
    }
    if (null != src.getHobbies()) {
        dest.setHobbies(new java.util.ArrayList<>(src.getHobbies()));
    }
    if (null != src.getName()) {
        dest.setName(src.getName());
    }
    if (null != src.getAge()) {
        dest.setAge(src.getAge());
    }
    if (null != src.getEmail()) {
        dest.setEmail(src.getEmail());
    }
}
```

**生成的代码特点：**
1. **参数判空**：方法开始处添加 `if (null == src || null == dest) return;`
2. **字段判空**：每个引用类型字段都有 `if (null != src.getXxx())` 判空检查
3. **集合深拷贝**：集合类型会创建新的 `ArrayList` 实例
4. **大括号包裹**：所有赋值语句都用大括号包裹
5. **null 前置**：判空条件中 `null` 放在前面，避免空指针异常

### 5. 调用生成的方法

#### ASM 模式
```java
PersonService service = new PersonService();
Person src = new Person("张三", 25, null, null, "zhangsan@example.com");
Person dest = new Person();

// 调用生成的辅助类方法
TestCopier.copy(src, dest);
```

#### BYTECODE 模式
```java
PersonService service = new PersonService();
Person src = new Person("张三", 25, null, null, "zhangsan@example.com");
Person dest = new Person();

// 直接调用原方法，无感知增强
service.copy(src, dest);
```

## 注解参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `strategy` | `CopyStrategy` | `SHALLOW` | 拷贝策略：`SHALLOW`(浅拷贝) / `DEEP`(深拷贝) |
| `ignoreNull` | `boolean` | `false` | 是否忽略 null 值，为 true 时源对象为 null 的字段不会覆盖目标对象 |
| `exclude` | `String[]` | `{}` | 需要排除的字段名列表 |
| `include` | `String[]` | `{}` | 仅包含的字段名列表，设置后只拷贝这些字段 |
| `useAccessors` | `boolean` | `true` | 是否使用 getter/setter 方法，为 false 时直接访问字段 |

## 注意事项

1. **方法签名**：被 `@Copier` 注解的方法必须至少有两个参数（源对象，目标对象）
2. **字段兼容性**：源类和目标类必须有相同名称且类型兼容的字段
3. **深拷贝限制**：使用 `DEEP` 策略时，复杂对象类型需要有 `copyTo()` 方法
4. **集合处理**：集合类型会创建新的实例，避免引用共享
5. **访问修饰符**：静态字段和 transient 字段会被自动跳过

## 扩展开发

### 添加新的注解处理器

1. 在 `annotation-api` 模块中定义新的注解
2. 在 `annotation-processor` 模块中创建对应的处理器类
3. 在 `META-INF/services/javax.annotation.processing.Processor` 中注册

### 示例：添加 @Builder 注解

```java
// 定义注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder { }

// 实现处理器
@SupportedAnnotationTypes("com.acanx.util.incubator.annotation.Builder")
public class BuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 生成 Builder 模式代码
        return true;
    }
}
```

## 技术栈

- **Java 21**：目标运行环境
- **JavaPoet 1.13.0**：代码生成库（ASM 模式）
- **Maven**：构建工具
- **SPI**：服务提供者接口机制
- **javac Trees API**：AST 操作（BYTECODE 模式）

## 构建说明

### JDK 内部 API 使用声明

`annotation-processor-ast` 模块使用了 `com.sun.tools.javac.*` 包中的内部 API（如 `TreeMaker`、`JCTree`、`Names` 等），这些 API 属于 **javac 编译器的内部实现**，不是官方公共 API。

**影响范围：**
- ⚠️ 仅在使用 **BYTECODE 模式** 时需要
- ⚠️ 仅在使用 **javac 编译器** 时需要
- ✅ 使用 ASM 模式时不受影响

### Maven 构建配置

由于 JDK 9+ 的模块系统限制，需要在 `pom.xml` 中显式开放相关模块的访问权限：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <fork>true</fork>
                <compilerArgs>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
                    <arg>--add-exports</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED</arg>
                    <arg>--add-opens</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
                    <arg>--add-opens</arg>
                    <arg>jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
                </compilerArgs>
            </configuration>
        </plugin>
        <!-- Javadoc 生成也需要访问内部 API -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <configuration>
                <additionalJOptions>
                    <additionalJOption>--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</additionalJOption>
                    <additionalJOption>--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</additionalJOption>
                </additionalJOptions>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 运行时配置

项目根目录已包含 `.mvn/jvm.config` 文件，Maven 会自动读取。如需手动配置：

```bash
# Windows (CMD)
set MAVEN_OPTS=--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED

# Linux/Mac
export MAVEN_OPTS="--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED"
```

### 常见问题

**Q: 构建时出现 "package com.sun.tools.javac.code is not visible" 错误？**

A: 确保 `pom.xml` 中已配置 `--add-exports` 和 `--add-opens` 参数，并且 Maven 使用了正确的 JDK 版本（21+）。

**Q: IDE 中编译失败？**

A: 在 IDEA 中，需要设置：
1. `Settings → Build, Execution, Deployment → Compiler → Java Compiler`
2. 在 "Additional command line parameters" 中添加相同的 `--add-exports` 参数

**Q: 需要兼容 Eclipse 或其他非 javac 编译器？**

A: 请使用 **ASM 模式**（`GenerationMode.ASM`），该模式仅使用官方 API，兼容所有编译器。

### 兼容性说明

| JDK 版本 | BYTECODE 模式 | ASM 模式 |
|---------|--------------|----------|
| JDK 8   | ✅ 支持       | ✅ 支持   |
| JDK 11  | ✅ 支持       | ✅ 支持   |
| JDK 17  | ✅ 支持       | ✅ 支持   |
| JDK 21  | ✅ 支持       | ✅ 支持   |
| JDK 25+ | ⚠️ 需配置模块开放 | ✅ 支持   |
| ECJ     | ❌ 不支持     | ✅ 支持   |

## 参考资料

- [Java Annotation Processing](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/package-summary.html)
- [JavaPoet GitHub](https://github.com/square/javapoet)
- [Lombok 实现原理](https://projectlombok.org/)
- [javac Trees API](https://docs.oracle.com/javase/8/docs/api/com/sun/source/util/Trees.html)
