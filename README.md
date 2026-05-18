# 🏪 API Estoque - Estocadão

API REST para gerenciamento de estoque desenvolvida com Kotlin + Ktor + Supabase.

---

# 🚀 Como rodar o projeto

## 1. Pré-requisitos

- Java 17+
- IntelliJ IDEA (recomendado)

---

## 2. Configurar Supabase

No arquivo:

```txt
src/main/kotlin/com/estoque/estocadao/database/SupabaseClient.kt
```

Adicione suas credenciais:

```kotlin
const val SUPABASE_URL = "https://seu-projeto.supabase.co"
const val SUPABASE_KEY = "sua-chave-anon-key"
```

---

## 3. Executar

### Pelo IntelliJ

Abra `Application.kt` → clique na seta verde ao lado do `main()`.

### Pelo terminal

```bash
./gradlew run
```

O servidor vai rodar em:

```txt
http://localhost:8081
```

---

## 4. Verificar se está funcionando

```bash
curl http://localhost:8081/health
```

**Resposta esperada:**

```txt
OK
```

---

# 📡 Endpoints da API

## Produtos (`/products`)

| Método | Endpoint | O que faz |
|---------|----------|------------|
| GET | `/products` | Listar todos produtos |
| GET | `/products/{id}` | Buscar produto por ID |
| POST | `/products` | Criar novo produto |
| PUT | `/products/{id}` | Atualizar produto |
| DELETE | `/products/{id}` | Remover produto |

---

## Estoque (`/stock`)

| Método | Endpoint | O que faz |
|---------|----------|------------|
| GET | `/stock` | Listar todos itens |
| GET | `/stock/{id}` | Buscar item por ID |
| POST | `/stock` | Adicionar item ao estoque |
| PUT | `/stock/{id}` | Atualizar item |
| DELETE | `/stock/{id}` | Remover item |

---

## Especial

| Método | Endpoint | O que faz |
|---------|----------|------------|
| GET | `/stock/summary` | Resumo: quantidade total por produto |

---

# 🧪 Testando com cURL

## Produtos

### Listar produtos

```bash
curl http://localhost:8081/products
```

### Criar produto

```bash
curl -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Caneta Azul","description":"Caneta esferográfica","sku":"CAN001","category":"Papelaria"}'
```

### Buscar produto (substitua o ID)

```bash
curl http://localhost:8081/products/ID-DO-PRODUTO
```

### Atualizar produto

```bash
curl -X PUT http://localhost:8081/products/ID-DO-PRODUTO \
  -H "Content-Type: application/json" \
  -d '{"name":"Caneta Azul Premium","price":3.50}'
```

### Deletar produto

```bash
curl -X DELETE http://localhost:8081/products/ID-DO-PRODUTO
```

---

## Estoque

### Listar estoque

```bash
curl http://localhost:8081/stock
```

### Adicionar item ao estoque

> Use um ID de produto válido.

```bash
curl -X POST http://localhost:8081/stock \
  -H "Content-Type: application/json" \
  -d '{"product_id":"ID-DO-PRODUTO","quantity":100,"unit_price":2.50,"location":"A1"}'
```

### Atualizar item

```bash
curl -X PUT http://localhost:8081/stock/ID-DO-ITEM \
  -H "Content-Type: application/json" \
  -d '{"quantity":200,"unit_price":2.25}'
```

### Deletar item

```bash
curl -X DELETE http://localhost:8081/stock/ID-DO-ITEM
```

### Resumo do estoque

```bash
curl http://localhost:8081/stock/summary
```

### Resposta esperada do `/stock/summary`

```json
[
  {
    "product_id": "550e8400-e29b-41d4-a716-446655440000",
    "product_name": "Caneta Azul",
    "total_quantity": 100
  },
  {
    "product_id": "550e8400-e29b-41d4-a716-446655440001",
    "product_name": "Caderno A4",
    "total_quantity": 50
  }
]
```

---

# 📦 Teste Rápido Completo

### 1. Criar produto

```bash
curl -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Produto Teste","description":"Teste","sku":"TEST001","category":"Teste"}'
```

### 2. Pegar o ID do produto criado e salvar

### 3. Adicionar ao estoque

```bash
curl -X POST http://localhost:8081/stock \
  -H "Content-Type: application/json" \
  -d '{"product_id":"ID-QUE-VOCE-GUARDOU","quantity":50,"unit_price":10.00,"location":"TEST"}'
```

### 4. Ver resumo

```bash
curl http://localhost:8081/stock/summary
```

---

# ✅ Códigos de Resposta

| Status | Significado |
|---------|--------------|
| 200 | Sucesso (GET/PUT) |
| 201 | Criado com sucesso (POST) |
| 204 | Deletado com sucesso (DELETE) |
| 400 | Dados inválidos |
| 404 | Recurso não encontrado |

---

# ⚠️ Erro comum: Produto não encontrado no estoque

Se ao criar um item de estoque aparecer:

```txt
Produto não encontrado
```

Faça o seguinte:

1. Primeiro crie um produto com `POST /products`
2. Copie o `id` retornado
3. Use esse ID no campo `product_id` do `POST /stock`

---

# 🛠️ Tecnologias

- Kotlin 1.9.0
- Ktor 2.3.7
- Supabase (PostgreSQL)
- `kotlinx.serialization`

---

# 📁 Estrutura do Projeto

```txt
src/main/kotlin/com/estoque/estocadao/
├── Application.kt          # Main do servidor
├── models/                 # Dados (Product, StockItem)
├── routes/                 # Endpoints HTTP
├── services/               # Lógica de negócio
└── database/               # Conexão Supabase
```