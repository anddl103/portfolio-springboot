# 프로젝트 실행 매뉴얼

---
### 프론트엔드
1. `.env[.{env}]` 의 내용을 채워주어야 한다.
  - `VUE_APP_BASE_URL`: 특별한 일이 없으면`/api`로 고정
  - `VUE_APP_API_KEY`: firebase 콘솔 > 프로젝트 설정 > 내 앱 > 앱 추가 후 `SDK 설정 및 구성 > npm`의 내용 참조
  - `VUE_APP_AUTH_DOMAIN`: 상동
  - `VUE_APP_PROJECT_ID`: 상동
  - `VUE_APP_STORAGE_BUCKET`: 상동
  - `VUE_APP_MESSAGING_SENDER_ID`:  상동
  - `VUE_APP_APP_ID`: 상동
  - `VUE_APP_S3_ACC_KEY`: aws console > iam > 사용자 > 보안 자격 증명 > 액세스 키
  - `VUE_APP_S3_SEC_KEY`: aws console > iam > 사용자 > 보안 자격 증명 > 액세스 키
  - `VUE_APP_AWS_REGION`: aws console > s3 > 버킷 > AWS 리전
  - `VUE_APP_S3_BUCKET_CLIENT`: backend 에서 사용하는 s3 bucket 명 (cloud front 적용)
  - `VUE_APP_S3_BUCKET`: frontend 에서 사용하는 s3 bucket 명 (cloud front 비적용)
  - `VUE_APP_S3_DOMAIN_CLIENT`: backend 에서 사용하는 s3 bucket 도메인
  - `VUE_APP_S3_DOMAIN`: frontend 에서 사용하는 s3 bucket 도메인
---
### 백엔드
1. `src/resources/firebase-conf-{env}.json` 의 내용을 채워주어야 한다.
   1. `firebase 콘솔 > 프로젝트 설정 > 서비스 계정 > Firebase Admin SDK` 에서 구성 스니펫을 `자바`로 변경한 후 새 비공개 키를 생성한다.
   2. 생성된 파일의 내용을 환경에 맞춰 `firebase-conf-{env}.json` 의 내용으로 넣어준다.
2. `src/resources/private_key.der` 파일을 생성하여 넣어준다.
   1. `openSSL` 로 private key를 생성
   2. 연동되는 `public key`는 `aws console > CloudFront > 퍼블릭 키 생성`에 사용
3. `src/resources/profiles/application-{env}.yml` 의 내용을 채워주어야 한다.
   - `spring.data.mongodb.url`
   - `spring.data.mongodb.database`
   - `cloud.aws.credentials.accessKey`: aws console > iam > 사용자 > 보안 자격 증명 > 액세스 키  
   - `cloud.aws.credentials.secretKey`: aws console > iam > 사용자 > 보안 자격 증명 > 액세스 키
   - `cloud.aws.s3.client-bucket`: backend 에서 사용하는 s3 bucket (cloud front 적용)
   - `cloud.aws.s3.bucket`: frontend 에서 사용하는 s3 bucket (cloud front 비적용)
   - `cloud.aws.cloud-front.domain`: aws console > CloudFront > 배포 > 도메인 이름
   - `cloud.aws.cloud-front.pairId`: aws console > CloudFront > 퍼블릭 키 > ID
---
### 최초 실행시
1. frontend > npm install 실행
2. gradle bootrun VM option에 `-Dspring.profiles.active={env}` 추가
3. backend 실행
4. frontend 실행
