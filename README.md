# Hotel Reservation System (Spring Boot)

This is a **Hotel Reservation Management System** that allows:

- Customers to book hotel rooms
- Customers to make payments using Paystack
- Admins to manage rooms
- Admins and receptionists to manage bookings
- Receptionists to check guests in and out
- Email notifications for booking updates
- Role-based access control (ADMIN / RECEPTIONIST / CUSTOMER)
---

## Technologies Used

- Java (Spring Boot)
- Spring MVC
- Spring Security
- Thymeleaf
- MySQL
- Bootstrap 5
- Paystack API
- Java Mail Sender
- Dotenv (.env)
---

# Features

## Customer

- Register account
- Login securely
- View available rooms
- Book rooms
- Pay online using Paystack
- View booking history
- Cancel bookings

---

## Admin

- Create rooms
- Edit rooms
- View all bookings
- Confirm bookings
- Reject bookings
- Create users
- Edit users
- Delete users

---

## Receptionist

- View bookings
- Confirm bookings
- Reject bookings
- Check guests in
- Check guests out

---

## Payment System

- Integrated with Paystack
- Generates payment link after booking
- Verifies payment automatically
- Updates booking payment status

---

## Email Notifications

Customers receive email updates when:
- Booking is confirmed
- Booking is rejected
- Booking status changes

---

# Setup Instructions

## Clone from GitHub

Open terminal or Git Bash

- Run:
- Open a terminal or Git Bash
- Navigate to the folder where you want to clone the project
- Run the clone command (replace <repo-url> with your GitHub repository URL):
- git clone <repo-url>
- Navigate into the cloned folder:
- cd <project-folder-name>
- Open the project in your IDE  IntelliJ

- Configure MySQL Database
### Open MySQL Workbench and run:

```
create database hotel_reservation;
```

### Configure the database connection in application.yml
```bash
spring:
  application:
    name: hotel_reservation

  datasource:
    url: jdbc:mysql://localhost:3306/hotel_reservation?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
    username: your_mysql_username
    password: your_mysql_password

  jpa:
    hibernate:
      ddl-auto: update

  mvc:
    hiddenmethod:
      filter:
        enabled: true

  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password

    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: 9090

paystack:
  secret-key: ${PAYSTACK_SECRET_KEY}
  base-url: https://api.paystack.co
  callback-url: http://localhost:9090/payments/verify
  
```

## Create .env File

- Create a .env file in the root folder of the project.

Example:
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_mail_password

PAYSTACK_SECRET_KEY=your_paystack_secret_key

## **Do NOT push your .env file to GitHub.!**

- Edit configuration from the dropdown at the top of project. Close to Run/Debug/Stop 
- Add environmental variables 
- Name and Values

- Run the Application
- 
- Run the application from:

- HotelReservationApplication.java

Application starts on:

``
http://localhost:9090
``

- Default Admin Account

When the application starts, a default admin account is automatically created.

---
Role : ADMIN
---
Email : houseoface81@gmail.com
---
Password : 12345
---
## LOGIN
![image alt](https://github.com/isaac-alexander/-Hotel-Reservation/blob/efb0ba5a079b99a805598b4a35544539511b2588/login.png)

## DASHBOARD 
![image alt](https://github.com/isaac-alexander/-Hotel-Reservation/blob/b676ebc13033ed347ae9e3d18751dcc76c9666b6/admin_dashboard.png)

## BOOKING
![image alt](https://github.com/isaac-alexander/-Hotel-Reservation/blob/aa9857ce3580d1776bc79621b0af844ac75b8944/booking_confirmed.png)

## HISTORY
![image alt](https://github.com/isaac-alexander/-Hotel-Reservation/blob/aa9857ce3580d1776bc79621b0af844ac75b8944/admin_booking_history.png)
