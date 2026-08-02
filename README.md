# 하루한컷 (Dayframe)

사진 한 장으로 하루를 기록하고, 달력과 피드로 다시 보는 Android 사진 일기 앱입니다. 참고 이미지는 정보 구조와 사용 흐름만 참고했으며, 로고·문구·그래픽은 독자적으로 구성했습니다.

## 현재 구현 범위

- Compose + Material 3 기반 단일 Activity 앱
- 온보딩 3단계와 DataStore 완료 상태 저장
- Room 기반 영구 저장(`DiaryEntry`, 사진, 음악 메타데이터)
- 월간 달력과 기록 진행률, 오늘/이전/다음 달 이동
- Android Photo Picker로 실제 사진 선택 및 URI 영구 보관
- 제목·본문(최대 500자)·기분·음악 정보 저장/편집
- 기록 상세, 즐겨찾기, 공유, 휴지통 이동(소프트 삭제)
- 월간 피드, 검색, 최신/오래된 순 정렬
- 연속 기록·최장 기록·기분 분포 통계
- 보관함(즐겨찾기), 설정(시스템/라이트/다크 테마)
- Room/Hilt 코드 생성과 월간 진행률·연속 기록 단위 테스트

사진 선택과 Room 저장이 MVP의 실제 동작 경로입니다. CameraX 촬영, Media3 로컬 재생, 알림, ZIP 백업/복원, 휴지통 목록 UI는 다음 단계에서 화면과 연결할 수 있도록 의존성과 데이터 필드를 먼저 마련했습니다.

## 필요한 프로그램

1. Android Studio 최신 안정 버전
2. Android SDK Platform 36 및 Build Tools 36.x
3. JDK 17 이상(Android Studio 내장 JDK 사용 가능)
4. Windows에서는 Git과 PowerShell

프로젝트는 Gradle Wrapper 9.1.0을 포함하므로 별도 Gradle 설치가 필요하지 않습니다.

## 프로젝트 열기

1. Android Studio에서 `dayframe-android` 폴더를 엽니다.
2. Gradle Sync가 끝날 때까지 기다립니다.
3. `app` 실행 구성을 선택합니다.
4. API 29 이상 에뮬레이터 또는 USB 디버깅을 켠 Android 휴대폰을 선택합니다.

PowerShell에서 SDK를 직접 지정해야 한다면:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug
```

## 에뮬레이터 만들기

Android Studio의 Device Manager에서 API 35 이상 시스템 이미지를 내려받고 새 가상 기기를 만듭니다. 처음 실행하면 온보딩에서 `시작하기`를 누르고, 달력의 `+` 또는 하단 `추가`에서 사진을 선택해 기록을 저장할 수 있습니다.

## 실제 기기에서 실행

1. 휴대폰에서 개발자 옵션과 USB 디버깅을 켭니다.
2. USB로 연결하고 RSA 디버깅 허용을 승인합니다.
3. Android Studio에서 기기를 선택하고 Run을 누릅니다.

명령줄 설치는 다음과 같습니다.

```powershell
.\gradlew.bat :app:assembleDebug
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
```

## APK 만들기

```powershell
.\gradlew.bat :app:assembleDebug
```

결과 파일: `app/build/outputs/apk/debug/app-debug.apk`

릴리스 서명은 Android Studio의 `Generate Signed Bundle / APK`에서 키스토어를 지정합니다. 키스토어와 비밀번호는 저장소에 올리지 마세요.

## 앱 이름·패키지명·아이콘 바꾸기

- 앱 이름: `app/src/main/res/values/strings.xml`의 `app_name`
- 패키지/namespace: `app/build.gradle.kts`의 `namespace`, `applicationId`와 Kotlin 소스 경로
- 아이콘: `app/src/main/res/drawable/ic_launcher_foreground.xml` 및 Manifest의 `android:icon`

패키지명을 바꿀 때는 Android Studio의 Refactor > Rename을 사용하고 Room/Hilt 생성 코드가 새 패키지를 가리키는지 확인하세요.

## 검증 명령

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

현재 검증된 도구 조합은 AGP 9.0.1, Gradle 9.1.0, Kotlin 2.4.10, Compose BOM 2026.06.00, Room 2.8.4입니다. AGP 9.0.1/R8가 Kotlin 2.4 메타데이터를 읽을 때 경고를 출력할 수 있지만 Debug APK 생성에는 영향을 주지 않습니다.

## 자주 발생하는 오류

- `SDK location not found`: `ANDROID_HOME`을 Android SDK 경로로 설정하거나 Android Studio에서 SDK를 설치합니다.
- `Unsupported class file major version`: Android Studio 내장 JDK 17 이상을 `JAVA_HOME`으로 지정합니다.
- 사진이 보이지 않음: Photo Picker 선택을 취소하지 않았는지 확인하고, URI 접근 권한을 다시 허용해 기록을 편집합니다.
- Room 스키마 오류: 앱을 삭제 후 재설치하면 개발 중인 로컬 DB가 초기화됩니다. 릴리스 전에는 destructive migration을 버전별 migration으로 교체합니다.

## 라이선스와 개인정보

자세한 내용은 [PRIVACY.md](PRIVACY.md)를 확인하세요. 기본 기록은 서버로 전송하지 않고 기기 내부 Room DB에만 저장합니다.
