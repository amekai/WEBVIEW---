# CDSKP Dashboard TV - GitHub 自动编译版

## 这是什么？

一个 Android TV 应用壳，用 GitHub Actions 自动编译 APK，**不需要安装 Android Studio**。

## 三步搞定

### 第 1 步：Fork / 上传代码到 GitHub

1. 解压 `TVDashboard-GitHub.zip`
2. **修改网址**：打开 `app/src/main/java/com/example/tvdashboard/MainActivity.kt`，把 `DASHBOARD_URL` 改成你的服务器地址
3. 把修改后的整个文件夹 push 到一个新的 GitHub 仓库

```bash
git init
git add .
git commit -m "init"
git branch -M main
git remote add origin https://github.com/你的用户名/你的仓库名.git
git push -u origin main
```

> 或者更简单：在 GitHub 网页上直接上传文件（`Add file → Upload files`）

### 第 2 步：等 GitHub 自动编译

1. Push 代码后，GitHub 会自动触发 Actions 编译
2. 打开仓库页面 → `Actions` 标签 → 看到 `Build APK` 工作流在运行
3. 等 3-5 分钟，状态变绿（✅）表示编译成功

### 第 3 步：下载 APK

1. 点击最新的成功运行记录
2. 页面底部 `Artifacts` 区域 → 点击 `app-debug-apk`
3. 下载 zip，解压得到 `app-debug.apk`
4. 用 ADB 或 U 盘安装到小米电视

---

## 修改网址示例

打开 `app/src/main/java/com/example/tvdashboard/MainActivity.kt`，找到：

```kotlin
private val DASHBOARD_URL = "https://your-dashboard-server.com/store-dashboard-v2.0.html"
```

改成：

```kotlin
// 局域网测试
private val DASHBOARD_URL = "http://192.168.1.100:8080/store-dashboard-v2.0.html"

// 或公网
private val DASHBOARD_URL = "https://your-domain.com/store-dashboard-v2.0.html"
```

改完后 push 到 GitHub，Actions 会自动重新编译。

---

## 安装到电视

```bash
adb connect 192.168.1.xxx:5555
adb install app-debug.apk
```

---

## 项目结构

```
├── .github/workflows/build-apk.yml    # GitHub Actions 配置
├── gradlew / gradlew.bat              # Gradle 包装器脚本
├── gradle/wrapper/                    # Gradle 包装器配置
├── build.gradle                       # 项目级构建
├── settings.gradle
├── gradle.properties
└── app/
    ├── build.gradle
    ├── src/main/
    │   ├── AndroidManifest.xml
    │   ├── java/com/example/tvdashboard/MainActivity.kt
    │   └── res/...
    └── proguard-rules.pro
```
