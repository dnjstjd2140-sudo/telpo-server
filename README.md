# telpo-server

여행 계획 지도 서비스의 백엔드 (Spring Boot 4.1.1 + Java 21). REST API 전용이며 화면 렌더링은
없다. 프론트(`telpo-client`)와 함께 `../여행계획_지도서비스_명세서.md` 명세를 따른다.

## 시작하기

```bash
cp .env.example .env   # 아래 "환경변수"에서 키 채우기
docker compose up -d   # 로컬 PostgreSQL
./gradlew bootRun       # http://localhost:8080
```

- Java 21 가상 스레드 활성화 (`spring.threads.virtual.enabled=true`) — 외부 API I/O 대기에 유리
- `spring.jpa.hibernate.ddl-auto=update`로 로컬 개발 중 스키마 자동 반영 (운영 전환 시 재검토 필요)

## 환경변수 (`.env`)

| 변수 | 설명 |
|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | PostgreSQL 계정 (docker-compose와 동일 값 사용) |
| `KAKAO_REST_API_KEY` | 카카오 로컬 API 키 — 장소 검색(`/api/places/search`)에 사용 |
| `TMAP_APP_KEY` | Tmap Open API 키 — 경로 계산(`/api/routes`)에 사용. 없으면 경로 조회가 500/502로 실패 |

REST API 키는 서버에만 두고 프론트 코드에는 절대 포함하지 않는다 (명세 10장).

## 지도/경로 프로바이더

- **장소 검색**: 카카오 로컬 REST API (`KakaoMapProvider`)
- **경로 계산**: Tmap Open API (`TmapRouteProvider`) — **자동차/도보/대중교통 3종 모두 지원**
  - 자동차: `POST /tmap/routes` (다중 경유지)
  - 도보: `POST /tmap/routes/pedestrian`
  - 대중교통: `POST /transit/routes` — 출발/도착이 너무 가까워 경로가 없으면(상태코드 11) 도보로 자동 대체
  - 응답에는 이동수단별 부가 정보도 포함: 자차는 `taxiFare`(택시 예상 요금)/`tollFare`(통행료),
    대중교통은 `transitFare`(총 요금)와 `legs`(도보/버스/지하철 구간, 노선명·색·정류장·소요시간).
    `legs[].pathStart/pathEnd`는 `path` 안의 좌표 범위라 클라이언트가 도보 구간만 점선으로 그릴 수 있음
  - 같은 `(경유지 좌표 목록, 이동수단)` 조합은 30분 TTL·최대 500건 메모리 캐시로 재응답 (무료 쿼터 소진 방지).
    서버 재시작 시 초기화됨 — 클라이언트→서버 네트워크 요청 자체를 막아주진 않음(그건 프론트 쪽 캐시가 담당)
- 외부 API 호출이 실패하면(`RestClientException`) 상세 원인은 로그로만 남기고, 클라이언트에는
  `502 Bad Gateway` + 일반 메시지로 응답 (`GlobalExceptionHandler`)
- 추후 해외 확장 시 `MapProvider`/`RouteProvider` 인터페이스를 Google 등 다른 구현체로 교체/분기할 수 있게
  분리되어 있음

## 데이터 모델 메모

- `TripStop.travelMode` — **이전 정류지에서 이 정류지로 이동할 때의 수단** (`CAR`/`WALK`/`TRANSIT`).
  그날의 첫 정류지는 의미 없고 기본값(`CAR`)만 채워짐. 프론트는 정류지 쌍마다 이 값을 받아
  구간별로 다른 색의 경로선을 그린다.

## API

| 메서드 | 경로 | 설명 |
|---|---|---|
| `POST` | `/api/trips` | 여행 생성 → `editToken`/`shareToken` 발급 |
| `GET` | `/api/trips/edit/{editToken}` | 편집용 조회 (응답에 공유 링크용 `shareToken` 포함) |
| `GET` | `/api/trips/share/{shareToken}` | 읽기 전용 조회 (`readOnly: true`). 토큰을 아는 사람은 누구나 조회 가능 |
| `PUT` | `/api/trips/edit/{editToken}` | 여행 정보/일정 저장 |
| `GET` | `/api/places/search?q=&lat=&lng=` | 장소 검색 (카카오 로컬 API 호출) |
| `POST` | `/api/routes` | 경유지 목록 + 이동수단 → 경로(선, 거리, 시간, 택시/통행료/대중교통 요금, 대중교통 구간) |
| `POST` | `/api/routes/optimize` | 장소 목록 → 추천 방문 순서 (TSP 휴리스틱) |

## 폴더 구조

```
src/main/java/com/telpo/server/
  trip/        Trip/TripStop 엔티티, 리포지토리, TripController/TripService, 편집·공유 토큰
  place/       Place 엔티티, PlaceController/PlaceService (카카오 검색 결과 저장/재사용)
  route/       RouteController/RouteService, RouteOptimizer(TSP 휴리스틱)
  map/         MapProvider/RouteProvider 추상화
    kakao/     KakaoMapProvider — 장소 검색
    tmap/      TmapRouteProvider — 자동차/도보/대중교통 경로 계산 + 캐시
  common/      LatLng, 전역 예외 처리(GlobalExceptionHandler)
```

## 스크립트

- `./gradlew bootRun` — 로컬 실행
- `./gradlew build` — 빌드 + 테스트
- `docker compose up -d` — 로컬 PostgreSQL 컨테이너 기동
