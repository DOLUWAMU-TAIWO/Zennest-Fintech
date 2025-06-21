# Payment Service (Spring Boot, Paystack, GraphQL, Docker)

This service handles **payment initialization**, **verification**, and **Paystack webhook processing** for donations or transactions.
It uses **Spring Boot 3**, **PostgreSQL**, **Docker Compose**, **Prometheus metrics**, and **GitHub Actions** for CI/CD.

---

# 🔧 Features
- **Secure API Key Authentication** using a custom `ApiKeyFilter`
- **Initialize payments** for users (members) via Paystack
- **Verify payments** manually or via **automatic webhook**
- **Full GraphQL API** for retrieving and managing payments
- **Detailed metrics** (payment counts, verification timings, webhook processing)
- **Production-grade** Dockerfile with multistage build, healthchecks
- **PostgreSQL database** with persistent volume
- **CI/CD** pipeline that builds and pushes multi-arch Docker images

---

# 💡 Quick Architecture Overview
| Component | Technology |
|:---|:---|
| Backend Framework | Spring Boot 3 (Java 21) |
| Database | PostgreSQL 16 |
| Payment Gateway | Paystack (Webhook + REST API) |
| GraphQL Support | Spring GraphQL |
| Security | Bearer Token API Key authentication |
| Metrics | Micrometer + Prometheus |
| Deployment | Docker Compose |
| CI/CD | GitHub Actions + DockerHub |

---

# 🌐 Endpoints

## REST Endpoints
| Method | Path | Description |
|:---|:---|:---|
| POST | `/api/payments/member/donate` | Initialize a new member donation |
| GET | `/api/payments/verify/{reference}` | Verify a payment manually |
| POST | `/api/payments/webhook` | Receive Paystack webhooks securely |
| GET | `/api/payments/health` | Healthcheck endpoint |

## GraphQL Endpoints
| Type | Path |
|:---|:---|
| Query + Mutation | `/graphql` (Spring GraphQL + GraphiQL enabled)

Example Queries:
- `getPaymentById(id)`
- `getPaymentByReference(reference)`
- `createMemberDonation(request)`
- `verifyDonationPayment(reference)`

---

# 🛡️ Security
- All non-health endpoints are protected by **Bearer Authorization** headers.
- The `Authorization` header must start with `Bearer <API_KEY>`.
- Invalid or missing keys immediately return **401 Unauthorized**.
- Webhook signature is validated using HMAC SHA-512 with Paystack secret.

---

# 🎓 How It Works

1. **User donates** -> Frontend calls `POST /api/payments/member/donate`
2. **Payment record created** -> `PENDING` status
3. **Paystack initializes** -> returns `authorization_url` and `reference`
4. **User pays** via Paystack UI
5. Paystack **calls your webhook** -> `POST /api/payments/webhook`
6. Webhook **verifies** and **updates** payment to `SUCCESS`
7. Alternatively, manual **verification** via `/api/payments/verify/{reference}`

---

# 🚀 Running Locally

```bash
git clone <repo-url>
cd payment-service
cp .env.example .env  # Fill in DB credentials, Paystack keys

# Build with Docker Compose
docker-compose up --build
```

---

# 📂 Docker Compose

```yaml
services:
  postgres:
    image: postgres:16.0
    container_name: postgres_payment
    env_file:
      - .env
    volumes:
      - payment_data:/var/lib/postgresql/data

  payment_service:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: payment-service
    env_file:
      - .env
    ports:
      - "6500:6500"
      - "6600:6600"
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:6600/actuator/health"]
      interval: 30s
      retries: 3
    depends_on:
      - postgres
    volumes:
      - ./logs:/app/logs

volumes:
  payment_data:
```

---

# 📊 Metrics
- `payments.initialization.count`
- `payments.verification.count`
- `payments.webhook.count`
- `payments.success.count`
- `payments.failed.count`
- `payments.pending.total`

Prometheus-compatible metrics exposed at `/actuator/prometheus`.

---

# 🌐 Environment Variables (`.env`)
| Key | Example |
|:---|:---|
| POSTGRES_USERNAME | payment_user |
| POSTGRES_PASSWORD | secretpassword |
| POSTGRES_DB | payment_db |
| SERVICE_PASSWORD | API_KEY_FOR_SERVICE |
| PAYSTACK_SECRET_KEY | sk_test_xxx |
| API_KEY | userservice_api_key |

---

# 👩‍💻 Developer Notes
- `ApiKeyFilter` protects all endpoints except healthchecks.
- Webhook validates Paystack's HMAC-SHA512 signature.
- GraphQL schema supports filtering payments by status, ID, reference.
- Dockerfile uses multi-stage Maven -> JAR -> lightweight runtime image.
- `/verify-payment` on frontend redirects to `/verify-payment.html?trxref=xxx`

---

# 💰 Future Improvements
- Add retries for webhook processing on transient failures.
- Separate user-service GraphQL endpoint to a dynamic config.
- Add dead-letter queue (DLQ) for failed webhook payloads.
- Enable distributed tracing (OpenTelemetry).

---

# 🎉 Congratulations
> This project is **Production Ready** and follows **modern Spring Boot practices**!

---

# 🔗 License
Open-sourced for learning and scaling real-world payment microservices.

---

# 🔁 Contributions
If you make improvements (e.g., webhook retries, Kafka integration, etc.), feel free to create a pull request.

---

# ✨ Maintained by [Dolu Payment Services @ QoreLabs]
Built with passion, precision, and production in mind.

# Zennest-Fintech
# Zennest-Fintech
