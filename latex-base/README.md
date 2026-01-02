# LaTeX Base 模块

## 概述
基础层模块，提供通用日志接口，不包含任何具体实现。

## 模块结构

```
latex-base/
├── build.gradle.kts
└── src/commonMain/kotlin/com/hrm/latex/base/
    └── log/
        ├── ILogger.kt      # 日志接口定义
        └── HLog.kt         # 日志门面（单例）
```

## 核心组件

### ILogger 接口
日志接口定义，需要外部在SDK初始化时提供实现：

```kotlin
interface ILogger {
    fun verbose(tag: String, message: String)
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}
```

### HLog 单例
日志门面，统一的日志访问入口：

```kotlin
object HLog {
    fun v(tag: String, message: String)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
```

## 使用方式

在应用中使用：
```kotlin
HLog.d("MyTag", "Debug message")
HLog.e("MyTag", "Error occurred", exception)
```

在SDK初始化时注入实现：
```kotlin
// 参见 latex-sdk 模块
LatexSDK.initialize(
    LatexSDK.Config(
        logger = MyLoggerImpl()  // 提供自定义日志实现
    )
)
```

## 设计原则
- ✅ 无任何依赖（零依赖）
- ✅ 只提供接口，不提供实现
- ✅ 通过依赖注入实现解耦
- ✅ 支持纯 JVM 平台（无 Android/iOS 等平台特定代码）
