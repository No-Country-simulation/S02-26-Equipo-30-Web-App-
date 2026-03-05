# HorseTrust API

HorseTrust is a production-ready backend API for a peer-to-peer horse marketplace.  
The platform allows users to list horses for sale, explore available listings, communicate with sellers, manage favorites, and securely complete transactions.

The API is built with **Java, Spring Boot, and PostgreSQL**, and follows a modular architecture designed for scalability, security, and maintainability.

---

# Overview

HorseTrust connects horse buyers and sellers in a secure digital marketplace.  
Users can publish horse listings, explore horses with advanced filtering, communicate through private chat, and complete purchases through an integrated payment system.

The platform provides strong moderation, seller verification signals, and trust scoring indicators to improve marketplace transparency.

---

# Tech Stack

**Backend**
- Java 21
- Spring Boot 3
- Spring Security (JWT Authentication)
- Spring Data JPA
- Hibernate

**Database**
- PostgreSQL

**Infrastructure**
- Docker
- Cloudinary (media storage)

**Payments**
- Stripe API

**Documentation**
- OpenAPI / Swagger

**Other**
- MapStruct (DTO mapping)
- Lombok
- JPA Specifications (dynamic filtering)

---

# Architecture

The application follows a layered architecture:
Controller
Service
Repository
Entity
DTO
Mapper

Each domain module is isolated and follows the same structure.

Main modules:
auth
users
horses
listings
media
favorites
chat
payments
metrics
admin

---

# Core Features

## Authentication & Security

The API uses **JWT-based authentication** with Spring Security.

Features include:

- User registration
- Secure login
- Stateless authentication
- Role-based access control
- Method-level authorization
- Admin permissions

Roles supported:

- USER
- ADMIN

---

# Horse Listings

Users can publish horses for sale through listings.

Each listing connects:
User (seller)
Horse
Listing metadata

A listing contains:

- Seller information
- Horse data
- Price
- Listing status
- Creation timestamp

Listing statuses include:
DRAFT
PUBLIC
ACTIVE
UNDER_REVIEW
PAUSED
SOLD
CLOSED
EXPIRED
WITHDRAWN
DELETED

Public users can browse **ACTIVE and PUBLIC listings**.

---

# Horse Data

Each horse contains detailed information including:

- Name
- Breed
- Birth date
- Sex
- Height, weight, and physical metrics
- Location (country, region, city)
- Discipline / main use
- Lineage
- Racing history
- Trust score indicators
- Veterinary exam statistics
- Seller reliability indicators

This information allows buyers to evaluate horses with greater transparency.

---

# Listing Search & Filtering

Listings support advanced filtering using **JPA Specifications**.

Filters include:

- Keyword search
- Breed
- Discipline / main use
- Location
- Minimum price
- Maximum price

Results are paginated and sortable.

Example request:
GET /api/v1/listings?breed=Friesian&discipline=DRESSAGE&minPrice=10000&maxPrice=50000&page=0&size=20&sort=price,asc

---

# Favorites

Users can save horses to their favorites list.

Endpoints allow users to:

- Add a horse to favorites
- Remove from favorites
- Retrieve their favorite horses

Favorites are stored as a relationship between **User and Horse**.

---

# Chat System

The platform includes a private chat system.

Users can:

- Start conversations with sellers
- Send messages
- Retrieve message history

Chat communication occurs only between authenticated users.

---

# Media Uploads

Horse images and documents can be uploaded through the API.

Features include:

- Secure upload endpoint
- Ownership validation
- Cloudinary storage integration

Media can be associated with horses and listings.

---

# Payments (Stripe Integration)

HorseTrust integrates **Stripe** to process secure payments.

Supported features include:

- Creating payment intents
- Processing listing purchases
- Handling successful payments
- Recording transactions

Stripe ensures:

- Secure payment processing
- PCI compliance
- Reliable transaction handling

Payments are linked to listings and users.

---

# Admin Features

Administrative endpoints allow platform moderation.

Admin capabilities include:

- Viewing platform metrics
- Managing users
- Moderating listings
- Investigating flagged sellers
- Monitoring marketplace activity

Admin routes are protected with role-based security.

---

# API Documentation

The API is fully documented using **OpenAPI / Swagger**.

Swagger UI is available at:
/swagger-ui.html

OpenAPI specification:
/v3/api-docs

The documentation allows developers to:

- Explore endpoints
- Understand request/response schemas
- Test endpoints interactively

---

# Project Structure
src/main/java/com/horseretail

config
auth
users
horses
listings
favorites
chat
media
payments
metrics
admin
repository
service
dto
mapper
model

Each module contains:
Controller
Service
Repository
DTO
Mapper
Entity

---

# Running the Project

## Requirements

- Java 21
- Maven
- PostgreSQL
- Docker (optional)

---

## Clone the repository

git clone https://github.com/yourusername/horseretail-api.git](https://github.com/No-Country-simulation/S02-26-Equipo-30-Web-App-.git

---

## Configure environment variables

Example configuration:
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/horseretail
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

JWT_SECRET=your-secret-key

STRIPE_SECRET_KEY=your-stripe-secret-key
STRIPE_WEBHOOK_SECRET=your-stripe-webhook-secret

CLOUDINARY_CLOUD_NAME=your-cloud
CLOUDINARY_API_KEY=your-key
CLOUDINARY_API_SECRET=your-secret

---

## Run the application
mvn spring-boot:run

Application will start on:
http://localhost:8090

---

# Example API Flow

Typical marketplace flow:

1. User registers
2. User logs in
3. Seller creates horse profile
4. Seller publishes listing
5. Buyers explore listings
6. Buyer contacts seller via chat
7. Buyer purchases horse using Stripe
8. Payment is recorded

---

# Security

Security measures implemented:

- JWT authentication
- Stateless sessions
- Role-based authorization
- Endpoint protection
- Ownership validation
- Secure password hashing (BCrypt)
- CORS configuration
- Input validation

---

# Design Goals

The API was designed with the following goals:

- Clean architecture
- Production readiness
- Scalable modules
- Secure authentication
- Clear separation of concerns
- Extensible domain model
- Developer-friendly API documentation

---

# Author

Backend developed as a complete marketplace API project using modern **Spring Boot architecture and best practices**.

---
