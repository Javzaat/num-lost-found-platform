# NUM Lost & Found Service Platform

## 1. Төслийн танилцуулга

**NUM Lost & Found Service Platform** нь МУИС-ийн орчинд алдагдсан болон олдсон эд зүйлсийг бүртгэх, зураг хавсаргах, боломжит тохирлыг автоматаар хайх, буцаан авах хүсэлтийг удирдах зориулалттай **Service-Oriented Architecture / microservice** суурьтай систем юм.

Энэхүү төсөл нь нэг том application хэлбэрээр биш, харин тусдаа үүрэгтэй хэд хэдэн service-ээс бүрдсэн. Frontend нь backend service-үүдтэй шууд холбогдохгүй, бүх хүсэлтээ **API Gateway**-ээр дамжуулдаг. Ингэснээр системийн бүтэц илүү цэгцтэй, өргөтгөх боломжтой, service бүрийн үүрэг тодорхой болсон.

## 2. Төслийн зорилго

Энэхүү төслийн гол зорилго нь SOA хичээлийн хүрээнд бодит хэрэглээтэй, service-үүдийн хоорондын харилцааг харуулсан систем хөгжүүлэх явдал юм.

Төслийн үндсэн зорилтууд:

- Алдагдсан эд зүйл бүртгэх
- Олдсон эд зүйл бүртгэх
- Эд зүйл бүрт зураг upload хийх
- Алдагдсан болон олдсон эд зүйлсийг автоматаар харьцуулж боломжит тохирол санал болгох
- Буцаан авах хүсэлт үүсгэх
- Claim хүсэлтийг approve / reject хийх
- Login / register бүхий хэрэглэгчийн нэвтрэлтийн урсгал нэмэх
- API Gateway ашиглан service-үүдийг нэг цэгээр дамжуулж ажиллуулах
- Docker Compose ашиглан олон service-ийг нэг дор ажиллуулах

## 3. Ашигласан технологи

| Төрөл | Технологи |
|---|---|
| Backend | Java 21, Spring Boot 3.3.5 |
| API Gateway | Spring Cloud Gateway |
| Frontend | React, Vite, Axios |
| Database | H2 in-memory database |
| File Upload | Spring Multipart File Upload |
| Containerization | Docker, Docker Compose |
| Build Tool | Maven |
| Deployment бэлтгэл | Dockerfile, docker-compose.prod.yml |
| Version Control | Git, GitHub |

## 4. Системийн архитектур

```text
React Frontend
      |
      v
API Gateway
      |
      +--> Auth Service
      +--> Item Service
      +--> Matching Service
      +--> Claim Service
      +--> File Service
```

## 5. Service-үүдийн үүрэг

### 5.1 API Gateway

**API Gateway** нь frontend болон backend service-үүдийн хоорондын гол нэвтрэх цэг юм. Frontend зөвхөн API Gateway рүү request илгээнэ. Gateway нь request-ийн path-аас хамаарч зөв service рүү дамжуулна.

Route-ууд:

```text
/api/auth/**     -> Auth Service
/api/items/**    -> Item Service
/api/matches/**  -> Matching Service
/api/claims/**   -> Claim Service
/api/files/**    -> File Service
```

API Gateway ашигласнаар frontend олон service-ийн port болон URL мэдэх шаардлагагүй болсон.

### 5.2 Auth Service

**Auth Service** нь хэрэглэгчийн бүртгэл болон нэвтрэлтийг хариуцна.

Үндсэн боломжууд:

- User register
- User login
- Password hashing
- User response болон demo token буцаах
- Login хийгээгүй хэрэглэгчийг dashboard руу оруулахгүй байх flow-г дэмжих

### 5.3 Item Service

**Item Service** нь алдагдсан болон олдсон эд зүйлсийг бүртгэх, хадгалах, жагсаах, төлөв өөрчлөх үүрэгтэй.

Item object-ийн гол field-үүд:

```text
id
title
description
category
location
imageUrl
type: LOST / FOUND
status: OPEN / MATCHED / CLAIMED / CLOSED
contactName
contactEmail
createdAt
```

Claim батлагдсаны дараа тухайн lost болон found item-ийн status нь `CLAIMED` болж өөрчлөгдөнө.

### 5.4 Matching Service

**Matching Service** нь алдагдсан эд зүйлд тохирох боломжит олдсон эд зүйлсийг санал болгодог.

Matching logic нь дараах мэдээллийг харьцуулна.

- Category ижил эсэх
- Location ижил эсэх
- Title төстэй эсэх
- Description төстэй эсэх

Үр дүнд нь score болон тайлбар буцаана.

Жишээ response:

```json
[
  {
    "lostItemId": 1,
    "foundItemId": 2,
    "lostItemTitle": "Black Wallet",
    "foundItemTitle": "Black Wallet Found",
    "score": 95,
    "reason": "Matched by same category, same location, similar title, similar description. Score: 95"
  }
]
```

### 5.5 Claim Service

**Claim Service** нь хэрэглэгч буцаан авах хүсэлт үүсгэх, claim-ийн төлөв удирдах үүрэгтэй.

Claim status:

```text
PENDING
APPROVED
REJECTED
```

Claim approve хийх үед frontend нь Item Service рүү хүсэлт илгээж, холбогдох lost болон found item-үүдийг `CLAIMED` төлөвтэй болгодог. Ингэснээр dashboard дээр зөвхөн `OPEN` item-үүд харагдана.

### 5.6 File Service

**File Service** нь зураг upload хийх, хадгалах, image URL буцаах үүрэгтэй.

Flow:

```text
Frontend зураг сонгоно
      |
      v
/api/files/upload
      |
      v
File Service зураг хадгална
      |
      v
/api/files/{fileName} URL буцаана
      |
      v
Item Service imageUrl field дээр хадгална
```

Upload хийсэн зураг Docker volume-д хадгалагдана.

## 6. Гол боломжууд

- Login/Register page
- Login хийгээгүй хэрэглэгч dashboard харах боломжгүй
- Lost item бүртгэх
- Found item бүртгэх
- Зураг upload хийх
- Item card дээр зураг харуулах
- Location dropdown
- Category dropdown
- Smart matching
- Claim request үүсгэх
- Claim approve/reject хийх
- Approve хийсний дараа item status `CLAIMED` болох
- API Gateway-ээр бүх request route хийх
- Docker Compose ашиглан олон service нэг дор ажиллуулах

## 7. Local ажиллуулах заавар

### 7.1 Шаардлагатай зүйлс

Доорх tools суусан байх шаардлагатай.

```text
Java 21
Maven
Node.js
npm
Docker Desktop
Git
```

### 7.2 Backend service-үүдийг Docker Compose ашиглан асаах

Project root folder руу орно.

```bash
cd ~/Desktop/num-lost-found-platform
```

Docker Compose ажиллуулах:

```bash
docker compose up --build
```

Backend service-үүд дараах port дээр ажиллана.

| Service | Port |
|---|---|
| API Gateway | 8080 |
| Auth Service | 8081 |
| Item Service | 8082 |
| Matching Service | 8083 |
| Claim Service | 8084 |
| File Service | 8085 |

### 7.3 Frontend ажиллуулах

Шинэ terminal нээгээд:

```bash
cd ~/Desktop/num-lost-found-platform/frontend-app
npm install
npm run dev
```

Browser дээр:

```text
http://localhost:5173
```

нээнэ.

Хэрвээ 5173 port ашиглагдаж байвал Vite өөр port өгч болно. Жишээ нь:

```text
http://localhost:5174
```

## 8. API endpoint жагсаалт

### 8.1 Auth Service

| Method | Endpoint | Үүрэг |
|---|---|---|
| POST | `/api/auth/register` | Шинэ хэрэглэгч бүртгэх |
| POST | `/api/auth/login` | Хэрэглэгч login хийх |
| GET | `/api/auth/ping` | Auth service ажиллаж байгаа эсэх шалгах |

### 8.2 Item Service

| Method | Endpoint | Үүрэг |
|---|---|---|
| POST | `/api/items` | Lost/Found item үүсгэх |
| GET | `/api/items` | Бүх item авах |
| GET | `/api/items/{id}` | Нэг item авах |
| GET | `/api/items/lost` | Lost item-үүд авах |
| GET | `/api/items/found` | Found item-үүд авах |
| PATCH | `/api/items/{id}/status/{status}` | Item status өөрчлөх |

### 8.3 Matching Service

| Method | Endpoint | Үүрэг |
|---|---|---|
| GET | `/api/matches/lost/{lostItemId}` | Lost item-д тохирох found item хайх |

### 8.4 Claim Service

| Method | Endpoint | Үүрэг |
|---|---|---|
| POST | `/api/claims` | Claim request үүсгэх |
| GET | `/api/claims` | Бүх claim авах |
| PATCH | `/api/claims/{id}/status/{status}` | Claim status update хийх |

### 8.5 File Service

| Method | Endpoint | Үүрэг |
|---|---|---|
| POST | `/api/files/upload` | Зураг upload хийх |
| GET | `/api/files/{fileName}` | Upload хийсэн зураг авах |

## 9. Test хийх жишээ command-ууд

### 9.1 Service health шалгах

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/auth/ping
```

### 9.2 Register хийх

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Javzaa",
    "email": "javzato@gmail.com",
    "password": "1234"
  }'
```

### 9.3 Login хийх

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "javzato@gmail.com",
    "password": "1234"
  }'
```

### 9.4 Lost item үүсгэх

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Black Wallet",
    "description": "Lost black wallet near NUM library",
    "category": "Түрийвч",
    "location": "МУИС Номын сан",
    "type": "LOST",
    "contactName": "Javzaa",
    "contactEmail": "javzato@gmail.com",
    "imageUrl": ""
  }'
```

### 9.5 Found item үүсгэх

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Black Wallet Found",
    "description": "Found black wallet near NUM library entrance",
    "category": "Түрийвч",
    "location": "МУИС Номын сан",
    "type": "FOUND",
    "contactName": "Student Office",
    "contactEmail": "office@num.edu.mn",
    "imageUrl": ""
  }'
```

### 9.6 Matching шалгах

```bash
curl http://localhost:8080/api/matches/lost/1
```

### 9.7 Claim үүсгэх

```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "lostItemId": 1,
    "foundItemId": 2,
    "claimantName": "Javzaa",
    "claimantEmail": "javzato@gmail.com",
    "proofDescription": "Энэ түрийвчин дотор миний оюутны үнэмлэх болон банкны карт байгаа."
  }'
```

### 9.8 Claim approve хийх

```bash
curl -X PATCH http://localhost:8080/api/claims/1/status/APPROVED
```

## 10. Docker Compose бүтэц

Local `docker-compose.yml` нь backend service-үүдийг нэг Docker network дээр ажиллуулна.

Үндсэн санаа:

```text
api-gateway -> auth-service
api-gateway -> item-service
api-gateway -> matching-service
api-gateway -> claim-service
api-gateway -> file-service
matching-service -> item-service
```

File upload-д зориулсан volume:

```text
file-uploads:/app/uploads
```

Production deployment-д зориулан нэмэлтээр `docker-compose.prod.yml` бэлдсэн. Энэ file нь frontend-ийг Nginx ашиглан port 80 дээр serve хийх зориулалттай.

## 11. Demo хийх дараалал

Багшид үзүүлэх demo flow:

1. Login/Register page харуулах
2. Шинэ хэрэглэгч register хийх
3. Dashboard руу орж байгааг харуулах
4. Lost item зурагтай бүртгэх
5. Found item зурагтай бүртгэх
6. Smart Matching хэсэгт lost item сонгож тохирол хайх
7. Claim request үүсгэх
8. Claim approve хийх
9. Claim шийдвэрлэгдсэн гэж харагдах
10. Approved болсон item-үүд OPEN list-ээс алга болж байгааг харуулах

## 12. SOA зарчмын тайлбар

Энэхүү төсөл нь SOA-ийн дараах санаануудыг хэрэгжүүлсэн.

### 12.1 Service separation

Системийн том functionality-г тусдаа service-үүдэд хуваасан.

```text
Auth
Item
Matching
Claim
File
Gateway
```

### 12.2 Loose coupling

Frontend нь service бүрийн дотоод implementation-г мэдэхгүй. Frontend зөвхөн API Gateway-тэй харилцана.

### 12.3 Reusability

File Service, Auth Service, Matching Service зэрэг нь цаашид өөр системд дахин ашиглагдах боломжтой.

### 12.4 Independent deployment

Service бүр Dockerfile-той. Иймээс тус бүрийг тусад нь build/deploy хийх боломжтой.

### 12.5 API-based communication

Service-үүд REST API ашиглан хоорондоо харилцаж байна.

## 13. Cloud deployment бэлтгэл

Cloud deployment-д зориулж дараах зүйлсийг бэлдсэн.

- Backend service бүрийн Dockerfile
- Frontend production Dockerfile
- `docker-compose.prod.yml`
- API Gateway CORS environment тохиргоо
- Frontend `VITE_API_BASE_URL` environment тохиргоо
- DigitalOcean Droplet дээр Docker build туршилт

Final demo-г local Docker Compose хувилбараар үзүүлж болно. Cloud deployment нь production compose file ашиглан үргэлжлүүлэх боломжтой.

## 14. Төслийн давуу тал

- Бодит хэрэгцээтэй campus service санаа
- Service бүрийн үүрэг тодорхой
- API Gateway ашигласан
- Login/Register authentication flow нэмсэн
- File upload service нэмсэн
- Matching algorithm-той
- Claim workflow-тэй
- Docker Compose ашигласан
- Frontend Монгол хэлтэй, demo хийхэд ойлгомжтой
- Cloud deployment-д бэлэн бүтэцтэй

## 15. Ирээдүйн сайжруулалт

Цаашид дараах байдлаар сайжруулах боломжтой.

- H2 database-г PostgreSQL болгох
- JWT authentication бүрэн хэрэгжүүлэх
- Role-based access control нэмэх
- Admin dashboard хийх
- Notification service нэмэх
- Email notification илгээх
- DigitalOcean Spaces эсвэл S3 storage ашиглах
- Matching algorithm-г илүү ухаалаг болгох
- Item search/filter нэмэх
- Claim history page нэмэх
- HTTPS болон domain name тохируулах

## 16. Дүгнэлт

NUM Lost & Found Service Platform нь SOA/microservice architecture-ийн үндсэн ойлголтуудыг бодит систем дээр хэрэгжүүлсэн project юм. Систем нь authentication, item management, smart matching, claim workflow, file upload зэрэг тусдаа service-үүдээс бүрдэж, API Gateway-ээр дамжуулан frontend-тэй холбогддог.

Энэхүү project нь зөвхөн CRUD систем биш, харин service separation, API routing, file handling, workflow management, containerization зэрэг олон ойлголтыг нэгтгэсэн практик ажил болсон.
