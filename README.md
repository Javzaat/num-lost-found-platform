# NUM Lost & Found Service Platform

## 1. Төслийн товч танилцуулга

**NUM Lost & Found Service Platform** нь МУИС-ийн орчинд алдагдсан болон олдсон эд зүйлсийг бүртгэх, зураг хавсаргах, боломжит тохирлыг автоматаар санал болгох, буцаан авах хүсэлтийг удирдах зориулалттай service-oriented / microservice суурьтай веб систем юм.

Энэхүү систем нь frontend application, API Gateway болон тусдаа үүрэгтэй backend service-үүдээс бүрдэнэ. Хэрэглэгч frontend-ээр дамжуулан системтэй харилцах бөгөөд frontend нь бүх backend service рүү шууд хандахгүй, зөвхөн API Gateway рүү request илгээдэг.

---

## 2. Төслийн зорилго

Энэхүү төслийн гол зорилго нь алдагдсан болон олдсон эд зүйлсийн бүртгэл, тохирол, буцаан авах хүсэлтийг нэг системээр удирдах боломжтой платформ боловсруулах явдал юм.

Үндсэн зорилтууд:

- Хэрэглэгч бүртгүүлэх болон нэвтрэх боломжтой байх
- Алдагдсан болон олдсон эд зүйл бүртгэх
- Эд зүйл бүрт зураг хавсаргах
- Алдагдсан эд зүйлд тохирох боломжит олдсон эд зүйлсийг санал болгох
- Буцаан авах claim request үүсгэх
- Claim request-ийг батлах эсвэл татгалзах
- PostgreSQL database service ашиглан өгөгдлийг хадгалах
- Docker Compose ашиглан бүх service-ийг нэг дор ажиллуулах

---

## 3. Ашигласан технологи

### Backend

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Cloud Gateway
- Maven

### Frontend

- React
- Vite
- Axios
- CSS

### Database ба storage

- PostgreSQL 16
- Docker Volume
- File upload storage volume

### DevOps / Runtime

- Docker
- Docker Compose
- Git / GitHub

---

## 4. Системийн архитектур

Систем нь дараах үндсэн хэсгүүдээс бүрдэнэ.

```text
React Frontend
      ↓
API Gateway
      ↓
Auth Service
Item Service
Matching Service
Claim Service
File Service
      ↓
PostgreSQL Database Service
```

Frontend нь зөвхөн API Gateway-тэй холбогдоно. API Gateway нь ирсэн request-ийн path-аас хамааран тохирох backend service рүү дамжуулна.

---

## 5. Service-үүдийн тайлбар

### 5.1 API Gateway

**API Gateway** нь frontend болон backend service-үүдийн хоорондох төв оролтын цэг юм.

Үүрэг:

- Frontend request-ийг хүлээн авах
- Path-based routing хийх
- Service-үүдийн URL-ийг frontend-ээс нуух
- CORS тохиргоог төвлөрүүлэх

Route-ууд:

```text
/api/auth/**     → Auth Service
/api/items/**    → Item Service
/api/matches/**  → Matching Service
/api/claims/**   → Claim Service
/api/files/**    → File Service
```

---

### 5.2 Auth Service

**Auth Service** нь хэрэглэгчийн бүртгэл болон нэвтрэлтийг хариуцна.

Үүрэг:

- Register хийх
- Login хийх
- Password hash хийж хадгалах
- User response/token буцаах

Database:

- PostgreSQL database service дээр user account мэдээлэл хадгална.

---

### 5.3 Item Service

**Item Service** нь lost/found item-ийн үндсэн бүртгэлийг хариуцна.

Үүрэг:

- Алдагдсан эд зүйл бүртгэх
- Олдсон эд зүйл бүртгэх
- Бүх item жагсаах
- Item-ийг type-аар шүүх
- Item status update хийх

Item status:

```text
OPEN
MATCHED
CLAIMED
CLOSED
```

Database:

- PostgreSQL database service дээр item мэдээлэл хадгална.

---

### 5.4 Matching Service

**Matching Service** нь алдагдсан эд зүйлд тохирох олдсон эд зүйлсийг хайж санал болгоно.

Үүрэг:

- Lost item-ийн мэдээллийг авах
- Found item-үүдтэй харьцуулах
- Category, location, title, description зэрэг мэдээллээр score тооцох
- Боломжит match result буцаах

Matching Service өөрөө database хадгалахгүй. Item Service-ээс өгөгдөл авч тохирол тооцдог.

---

### 5.5 Claim Service

**Claim Service** нь буцаан авах хүсэлтийн workflow-г хариуцна.

Үүрэг:

- Claim request үүсгэх
- Claim жагсаах
- Claim status update хийх
- Claim approve/reject хийх

Claim status:

```text
PENDING
APPROVED
REJECTED
```

Claim approve болсон үед холбогдох lost болон found item-үүд `CLAIMED` төлөвтэй болно. Frontend дээр зөвхөн `OPEN` item-үүд харагддаг тул claim болсон item жагсаалтаас алга болно.

Database:

- PostgreSQL database service дээр claim мэдээлэл хадгална.

---

### 5.6 File Service

**File Service** нь зураг upload болон зураг serve хийх үүрэгтэй.

Үүрэг:

- Зураг upload хийх
- Upload хийсэн image URL буцаах
- Item-д хадгалагдсан image URL-аар зураг харуулах

File Service нь PostgreSQL database ашиглахгүй. Зургууд Docker volume дээр хадгалагдана.

---

### 5.7 PostgreSQL Database Service

PostgreSQL нь тусдаа Docker service хэлбэрээр ажиллана.

```text
postgres-db
```

Энэ нь business microservice биш, харин infrastructure service юм. Auth Service, Item Service, Claim Service нь энэ database service-тэй холбогдож өгөгдлөө хадгалдаг.

Database-ийн өгөгдөл Docker volume дээр хадгалагддаг.

```yaml
volumes:
  postgres-data:
```

Иймээс:

```bash
docker compose down
docker compose up
```

хийсэн ч өгөгдөл хадгалагдана.

Харин:

```bash
docker compose down -v
```

хийвэл volume устах тул database-ийн өгөгдөл устна.

---

## 6. Docker Compose бүтэц

Local development болон demo орчинд бүх service-ийг Docker Compose ашиглан нэг дор ажиллуулна.

Үндсэн container-ууд:

```text
num-postgres-db
num-auth-service
num-item-service
num-matching-service
num-claim-service
num-file-service
num-api-gateway
```

Frontend нь development үед local npm dev server дээр ажиллана.

```text
http://localhost:5173
```

Backend API Gateway:

```text
http://localhost:8080
```

---

## 7. Port тохиргоо

| Service | Port | Тайлбар |
|---|---:|---|
| API Gateway | 8080 | Frontend-ийн үндсэн backend entry point |
| Auth Service | 8081 | Login/Register |
| Item Service | 8082 | Lost/Found item |
| Matching Service | 8083 | Smart matching |
| Claim Service | 8084 | Claim workflow |
| File Service | 8085 | File upload |
| PostgreSQL | 5432 | Database service |
| React Frontend | 5173 | Frontend development server |

---

## 8. Project ажиллуулах заавар

### 8.1 Backend service-үүдийг асаах

Project root folder дээр:

```bash
cd ~/Desktop/num-lost-found-platform
docker compose up --build
```

Бүх container ажиллаж байгаа эсэхийг шалгах:

```bash
docker compose ps
```

---

### 8.2 Frontend асаах

Шинэ terminal нээгээд:

```bash
cd ~/Desktop/num-lost-found-platform/frontend-app
npm run dev
```

Browser дээр:

```text
http://localhost:5173
```

---

## 9. API шалгах command-ууд

API Gateway health check:

```bash
curl http://localhost:8080/actuator/health
```

Auth Service ping:

```bash
curl http://localhost:8080/api/auth/ping
```

Items жагсаах:

```bash
curl http://localhost:8080/api/items
```

Claims жагсаах:

```bash
curl http://localhost:8080/api/claims
```

---

## 10. Жишээ API request

### 10.1 Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Javzaa",
    "email": "javzato@gmail.com",
    "password": "1234"
  }'
```

### 10.2 Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "javzato@gmail.com",
    "password": "1234"
  }'
```

### 10.3 Lost item үүсгэх

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Black Wallet",
    "description": "Lost black wallet near NUM library",
    "category": "Wallet",
    "location": "NUM Library",
    "type": "LOST",
    "contactName": "Javzaa",
    "contactEmail": "javzato@gmail.com",
    "imageUrl": ""
  }'
```

### 10.4 Found item үүсгэх

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Black Wallet Found",
    "description": "Found a black wallet near NUM library entrance",
    "category": "Wallet",
    "location": "NUM Library",
    "type": "FOUND",
    "contactName": "Student Office",
    "contactEmail": "office@num.edu.mn",
    "imageUrl": ""
  }'
```

### 10.5 Match хайх

```bash
curl http://localhost:8080/api/matches/lost/1
```

### 10.6 Claim үүсгэх

```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "lostItemId": 1,
    "foundItemId": 2,
    "claimantName": "Javzaa",
    "claimantEmail": "javzato@gmail.com",
    "proofDescription": "Wallet has my student ID card and bank card inside."
  }'
```

### 10.7 Claim approve хийх

```bash
curl -X PATCH http://localhost:8080/api/claims/1/status/APPROVED
```

---

## 11. Demo workflow

Demo хийх дараалал:

1. Frontend login/register page харуулах
2. Шинэ хэрэглэгч бүртгэх
3. Dashboard руу нэвтрэх
4. Lost item зурагтай бүртгэх
5. Found item зурагтай бүртгэх
6. Smart Matching хэсэгт тохирол хайх
7. Claim request үүсгэх
8. Claim approve хийх
9. Claim approved болсон item-үүд `CLAIMED` төлөвтэй болж, OPEN list-ээс алга болж байгааг харуулах
10. Docker Compose restart хийсэн ч өгөгдөл хадгалагдаж байгааг харуулах

---

## 12. Өгөгдөл хадгалалт ба persistence

Эхний demo хувилбарт H2 in-memory database ашиглаж байсан. Харин одоогийн хувилбарт PostgreSQL database service ашиглаж байна.

PostgreSQL service нь Docker volume ашигладаг:

```text
postgres-data
```

Тиймээс container restart/down хийсэн ч user, item, claim мэдээлэл хадгалагдана.

Data хадгалагдана:

```bash
docker compose down
docker compose up
```

Data устна:

```bash
docker compose down -v
```

---

## 13. Төслийн давуу тал

- Microservice architecture ашигласан
- API Gateway ашиглан backend service-үүдийг нэг entry point-той болгосон
- Service бүр тусдаа үүрэгтэй
- PostgreSQL database service ашиглан persistent data storage хийсэн
- File upload тусдаа service болсон
- Docker Compose ашиглан бүх service-ийг нэг дор ажиллуулах боломжтой
- Frontend нь хэрэглэгчдэд ойлгомжтой dashboard хэлбэртэй
- Claim workflow болон item status transition хэрэгжсэн

---

## 14. Хязгаарлалт ба цаашдын сайжруулалт

Одоогийн хувилбар нь demo болон хичээлийн project-д зориулагдсан. Цаашид дараах байдлаар сайжруулах боломжтой.

- JWT authentication бүрэн хэрэгжүүлэх
- User role буюу admin/user permission нэмэх
- PostgreSQL schema-г service бүрээр салгах
- Image storage-ийг cloud object storage рүү шилжүүлэх
- Search/filter function нэмэх
- Notification service нэмэх
- Unit болон integration test нэмэх
- Cloud deployment-ийг production түвшинд бүрэн тохируулах

---

## 15. Дүгнэлт

NUM Lost & Found Service Platform нь алдагдсан болон олдсон эд зүйлсийн бүртгэл, тохирол, claim workflow-г microservice architecture ашиглан хэрэгжүүлсэн систем юм. Системд Auth, Item, Matching, Claim, File service болон API Gateway ашигласан. Мөн PostgreSQL database service-ийг Docker Compose дээр тусдаа infrastructure service хэлбэрээр ажиллуулж, өгөгдлийг persistent volume дээр хадгалдаг болгосон.

Энэхүү төсөл нь service-oriented architecture, API Gateway routing, Docker Compose orchestration, persistent database service, frontend-backend integration зэрэг үндсэн ойлголтуудыг нэгтгэн харуулж байна.
