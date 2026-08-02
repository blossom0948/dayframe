# Cloudflare Pages 설정

이 저장소는 Android 앱과 브라우저용 Dayframe 웹 뷰어를 함께 포함합니다. GitHub 연동 Pages 프로젝트에서 아래 값으로 설정하면 `main`에 push할 때 정적 사이트가 자동 배포됩니다.

| 항목 | 값 |
| --- | --- |
| Framework preset | None |
| Root directory | `/` (비워 두기) |
| Build command | `npm run build` |
| Build output directory | `dist` |
| Node.js version | 20 이상 |

`npm run build`는 루트의 웹 파일을 `dist/`로 복사하는 의존성 없는 정적 빌드입니다. 별도 서버, 데이터베이스, 환경 변수는 필요하지 않습니다.

## Pages 오류가 계속될 때

1. Pages 프로젝트의 **Settings → Builds & deployments**에서 위 값이 저장됐는지 확인합니다.
2. **Deployments**에서 최신 `main` 커밋을 다시 배포합니다.
3. 예전 빌드가 `build/` 또는 `app/build/outputs`를 출력 폴더로 가리키고 있었다면 반드시 `dist`로 바꿉니다.

웹 기록은 브라우저 `localStorage`에 저장되며, 보관함에서 JSON 백업을 내보내고 불러올 수 있습니다. Android Room 데이터와는 별도 저장소입니다.
