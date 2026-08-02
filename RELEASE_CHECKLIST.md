# 출시 전 점검표

## 빌드·테스트

- [x] `:app:testDebugUnitTest`
- [x] `:app:assembleDebug`
- [ ] 서명된 Release APK/AAB 빌드
- [ ] 릴리스 빌드에서 R8/ProGuard 확인

## 핵심 흐름

- [ ] 최초 실행 온보딩과 `나중에 설정`
- [ ] Photo Picker 선택 취소/권한 거부
- [ ] 사진 + 본문 저장
- [ ] 앱 강제 종료 후 기록 유지
- [ ] 편집·즐겨찾기·삭제·공유
- [ ] 달력 날짜 선택과 월 이동
- [ ] 피드 검색 및 정렬
- [ ] 다크 모드와 큰 글꼴

## 기기·접근성

- [ ] API 29 실기기
- [ ] 최신 Android 실기기
- [ ] 가로 모드와 폴더블/태블릿
- [ ] TalkBack, 48dp 터치 영역, 대비
- [ ] 제스처/3버튼 내비게이션과 시스템 바 inset

## 다음 구현 단계

- [ ] CameraX 촬영 UI와 이미지 자르기/회전
- [ ] Media3 로컬 오디오 선택·재생
- [ ] 알림 예약과 Android 13+ 알림 권한
- [ ] 휴지통 목록·복원·30일 WorkManager 정리
- [ ] ZIP 백업/복원 및 중복 날짜 정책
- [ ] Room destructive migration 제거 및 schema 버전 관리
