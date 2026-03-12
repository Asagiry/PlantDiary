param(
    [ValidateSet("Build", "Run", "BuildAndRun")]
    [string]$Mode = "BuildAndRun"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$script:ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$script:PackageName = "com.asagiry.plantdiary"
$script:AvdName = "PlantDiary_API_35"
$script:ApkRenamePath = Join-Path $script:ProjectRoot "app\build\renamed-apk\PlantDiary-debug.apk"
$script:ApkFallbackPath = Join-Path $script:ProjectRoot "app\build\outputs\apk\debug\app-debug.apk"

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message"
}

function Get-FirstExistingPath {
    param([string[]]$Candidates)

    foreach ($candidate in $Candidates) {
        if ($candidate -and (Test-Path $candidate)) {
            return $candidate
        }
    }

    return $null
}

function Initialize-Environment {
    $javaHome = Get-FirstExistingPath @(
        $env:JAVA_HOME,
        "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
    )

    if (-not $javaHome) {
        throw "Java was not found. Set JAVA_HOME or install JDK 21."
    }

    $sdkRoot = Get-FirstExistingPath @(
        $env:ANDROID_SDK_ROOT,
        $env:ANDROID_HOME,
        "C:\Users\Asagiry\AppData\Local\Android\Sdk"
    )

    if (-not $sdkRoot) {
        throw "Android SDK was not found. Set ANDROID_SDK_ROOT or ANDROID_HOME."
    }

    $env:JAVA_HOME = $javaHome
    $env:ANDROID_HOME = $sdkRoot
    $env:ANDROID_SDK_ROOT = $sdkRoot

    $toolPaths = @(
        (Join-Path $javaHome "bin"),
        (Join-Path $sdkRoot "platform-tools"),
        (Join-Path $sdkRoot "emulator"),
        (Join-Path $sdkRoot "cmdline-tools\latest\bin")
    )

    $currentPathParts = @()
    if ($env:PATH) {
        $currentPathParts = $env:PATH -split ";" | Where-Object { $_ }
    }

    foreach ($toolPath in $toolPaths) {
        if ((Test-Path $toolPath) -and ($currentPathParts -notcontains $toolPath)) {
            $currentPathParts = @($toolPath) + $currentPathParts
        }
    }

    $env:PATH = ($currentPathParts | Select-Object -Unique) -join ";"

    $script:GradleWrapper = Join-Path $script:ProjectRoot "gradlew.bat"
    $script:Adb = Join-Path $sdkRoot "platform-tools\adb.exe"
    $script:Emulator = Join-Path $sdkRoot "emulator\emulator.exe"
    $script:AvdManager = Join-Path $sdkRoot "cmdline-tools\latest\bin\avdmanager.bat"

    if (-not (Test-Path $script:GradleWrapper)) {
        throw "gradlew.bat was not found in the project root."
    }
    if (-not (Test-Path $script:Adb)) {
        throw "adb.exe was not found in the Android SDK."
    }
    if (-not (Test-Path $script:Emulator)) {
        throw "emulator.exe was not found in the Android SDK."
    }
    if (-not (Test-Path $script:AvdManager)) {
        throw "avdmanager.bat was not found in the Android SDK."
    }
}

function Invoke-GradleDebugBuild {
    Write-Info "Building debug APK..."
    Push-Location $script:ProjectRoot
    try {
        & $script:GradleWrapper ":app:assembleDebug"
        if ($LASTEXITCODE -ne 0) {
            throw "Gradle build failed with exit code $LASTEXITCODE."
        }
    }
    finally {
        Pop-Location
    }
}

function Get-DebugApkPath {
    $apkPath = Get-FirstExistingPath @(
        $script:ApkRenamePath,
        $script:ApkFallbackPath
    )

    if (-not $apkPath) {
        throw "Debug APK was not found. Build the project first."
    }

    return $apkPath
}

function Get-RunningEmulatorSerial {
    $deviceLines = & $script:Adb devices

    foreach ($line in $deviceLines) {
        if ($line -match '^(emulator-\d+)\s+device$') {
            return $Matches[1]
        }
    }

    return $null
}

function Test-AvdExists {
    $avdList = & $script:AvdManager list avd
    foreach ($line in $avdList) {
        if ($line -match "^\s*Name:\s+$([regex]::Escape($script:AvdName))$") {
            return $true
        }
    }

    return $false
}

function Wait-For-EmulatorBoot {
    param([int]$MaxAttempts = 120)

    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        $serial = Get-RunningEmulatorSerial
        if ($serial) {
            $bootState = ((& $script:Adb -s $serial shell getprop sys.boot_completed 2>$null) | Out-String).Trim()
            if ($bootState -eq "1") {
                Write-Info "Emulator is ready: $serial"
                return $serial
            }
        }

        Start-Sleep -Seconds 5
    }

    throw "Timed out while waiting for the emulator to boot."
}

function Start-Or-ReuseEmulator {
    $serial = Get-RunningEmulatorSerial
    if ($serial) {
        Write-Info "Using running emulator: $serial"
        return (Wait-For-EmulatorBoot -MaxAttempts 24)
    }

    if (-not (Test-AvdExists)) {
        throw "AVD '$script:AvdName' was not found."
    }

    Write-Info "Starting AVD '$script:AvdName'..."
    Start-Process -FilePath $script:Emulator -ArgumentList @(
        "-avd",
        $script:AvdName,
        "-netdelay",
        "none",
        "-netspeed",
        "full"
    ) | Out-Null

    return (Wait-For-EmulatorBoot)
}

function Install-And-LaunchApk {
    $serial = Start-Or-ReuseEmulator
    $apkPath = Get-DebugApkPath

    Write-Info "Installing APK: $apkPath"
    & $script:Adb -s $serial install -r $apkPath
    if ($LASTEXITCODE -ne 0) {
        throw "adb install failed with exit code $LASTEXITCODE."
    }

    Write-Info "Launching $script:PackageName..."
    & $script:Adb -s $serial shell monkey -p $script:PackageName -c android.intent.category.LAUNCHER 1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to launch the application."
    }
}

Initialize-Environment

switch ($Mode) {
    "Build" {
        Invoke-GradleDebugBuild
    }
    "Run" {
        Install-And-LaunchApk
    }
    "BuildAndRun" {
        Invoke-GradleDebugBuild
        Install-And-LaunchApk
    }
}

Write-Info "Done."
