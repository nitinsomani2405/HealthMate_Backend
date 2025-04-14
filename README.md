# HealthMate Backend Documentation

## Database Tables

### 1. Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(12) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);
```

### 2. Sessions Table
```sql
CREATE TABLE sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    access_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    access_token_expiry TIMESTAMP NOT NULL,
    refresh_token_expiry TIMESTAMP NOT NULL
);
```

### 3. Patient Table
```sql
CREATE TABLE patient (
    patient_id UUID PRIMARY KEY REFERENCES users(id),
    date_of_birth VARCHAR(255),
    profile_completed BOOLEAN NOT NULL
);
```

### 4. Doctor Table
```sql
CREATE TABLE doctor (
    doctor_id UUID PRIMARY KEY REFERENCES users(id),
    date_of_birth VARCHAR(255),
    speciality VARCHAR(255),
    clinic_name VARCHAR(255),
    profile_completed BOOLEAN NOT NULL
);
```

### 5. Appointments Table
```sql
CREATE TABLE appointments (
    appointment_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patient(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctor(doctor_id),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);
```

### 6. Prescriptions Table
```sql
CREATE TABLE prescriptions (
    prescription_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patient(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctor(doctor_id),
    prescribed_at TIMESTAMP NOT NULL,
    medicines JSONB,
    notes TEXT
);
```

### 7. Lab Reports Table
```sql
CREATE TABLE lab_reports (
    report_id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL REFERENCES doctor(doctor_id),
    patient_id UUID NOT NULL REFERENCES patient(patient_id),
    file_url VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);
```

## API Endpoints

### Authentication APIs

1. **Signup**
- Endpoint: `POST /api/signup`
- Request Body:
```json
{
    "email": "string",
    "password": "string",
    "fullName": "string",
    "phoneNumber": "string",
    "gender": "string",
    "role": "string"
}
```
- Response:
```json
{
    "data": {
        "user": {
            "id": "UUID",
            "email": "string",
            "createdAt": "timestamp"
        }
    }
}
```

2. **Login**
- Endpoint: `POST /api/login`
- Request Body:
```json
{
    "email": "string",
    "password": "string"
}
```
- Response:
```json
{
    "data": {
        "user": {
            "id": "UUID",
            "email": "string",
            "createdAt": "timestamp"
        },
        "session": {
            "accessToken": "string",
            "refreshToken": "string",
            "accessTokenExpiry": "timestamp",
            "refreshTokenExpiry": "timestamp"
        }
    },
    "role": "string"
}
```

3. **Refresh Token**
- Endpoint: `POST /api/refresh-token`
- Request Body:
```json
{
    "refreshToken": "string"
}
```
- Response:
```json
{
    "data": {
        "session": {
            "accessToken": "string",
            "refreshToken": "string",
            "accessTokenExpiry": "timestamp",
            "refreshTokenExpiry": "timestamp"
        }
    }
}
```

### Profile APIs

1. **Get Profile**
- Endpoint: `GET /api/get-profile/{userId}`
- Response (Doctor):
```json
{
    "id": "UUID",
    "email": "string",
    "fullName": "string",
    "phoneNumber": "string",
    "gender": "string",
    "role": "string",
    "dateOfBirth": "string",
    "profileCompleted": boolean,
    "speciality": "string",
    "clinicName": "string"
}
```
- Response (Patient):
```json
{
    "id": "UUID",
    "email": "string",
    "fullName": "string",
    "phoneNumber": "string",
    "gender": "string",
    "role": "string",
    "dateOfBirth": "string",
    "profileCompleted": boolean
}
```

2. **Post Profile**
- Endpoint: `POST /api/post-profile`
- Request Body:
```json
{
    "fullName": "string",
    "email": "string",
    "phone": "string",
    "gender": "string",
    "dateOfBirth": "string",
    "speciality": "string",
    "clinicName": "string"
}
```

### Appointment APIs

1. **Book Appointment**
- Endpoint: `POST /api/book-appointment`
- Request Body:
```json
{
    "doctorId": "UUID",
    "appointmentDate": "date",
    "appointmentTime": "time",
    "reason": "string"
}
```
- Response:
```json
{
    "message": "string",
    "appointmentId": "UUID",
    "status": "string"
}
```

2. **Cancel Appointment**
- Endpoint: `PUT /api/cancel-appointment/{appointmentId}`

3. **Confirm Appointment**
- Endpoint: `PUT /api/confirm-appointment/{appointmentId}`

4. **Get Patient Appointments**
- Endpoint: `GET /api/patient/appointments`
- Response:
```json
{
    "appointments": [
        {
            "doctorId": "UUID",
            "doctorName": "string",
            "appointments": [
                {
                    "appointmentId": "UUID",
                    "appointmentDate": "date",
                    "appointmentTime": "time",
                    "reason": "string",
                    "status": "string"
                }
            ]
        }
    ]
}
```

5. **Get Doctor Appointments**
- Endpoint: `GET /api/doctor/appointments`
- Response:
```json
{
    "appointments": [
        {
            "patientId": "UUID",
            "patientName": "string",
            "appointments": [
                {
                    "appointmentId": "UUID",
                    "appointmentDate": "date",
                    "appointmentTime": "time",
                    "reason": "string",
                    "status": "string"
                }
            ]
        }
    ]
}
```

### Prescription APIs

1. **Add Prescription**
- Endpoint: `POST /api/prescriptions`
- Request Body:
```json
{
    "patientId": "UUID",
    "medicines": [
        {
            "name": "string",
            "dosage": "string",
            "frequency": "string",
            "duration": "string",
            "instructions": "string"
        }
    ],
    "notes": "string"
}
```

2. **Get Patient Prescriptions**
- Endpoint: `GET /api/patient/prescriptions`
- Response:
```json
{
    "prescriptions": [
        {
            "doctorId": "UUID",
            "doctorName": "string",
            "prescriptions": [
                {
                    "prescriptionId": "UUID",
                    "prescribedAt": "timestamp",
                    "medicines": [
                        {
                            "name": "string",
                            "dosage": "string",
                            "frequency": "string",
                            "duration": "string",
                            "instructions": "string"
                        }
                    ],
                    "notes": "string"
                }
            ]
        }
    ]
}
```

3. **Get Doctor Prescriptions**
- Endpoint: `GET /api/doctor/prescriptions`
- Response:
```json
{
    "prescriptions": [
        {
            "patientId": "UUID",
            "patientName": "string",
            "prescriptions": [
                {
                    "prescriptionId": "UUID",
                    "prescribedAt": "timestamp",
                    "medicines": [
                        {
                            "name": "string",
                            "dosage": "string",
                            "frequency": "string",
                            "duration": "string",
                            "instructions": "string"
                        }
                    ],
                    "notes": "string"
                }
            ]
        }
    ]
}
```

### Lab Report APIs

1. **Upload Lab Report**
- Endpoint: `POST /api/lab-reports/upload`
- Request Parameters:
  - patient_id: UUID
  - file: MultipartFile
- Response:
```json
{
    "message": "string",
    "reportId": "UUID",
    "fileUrl": "string"
}
```

2. **Get Patient Lab Reports**
- Endpoint: `GET /api/lab-reports/patient`
- Response:
```json
{
    "reports": [
        {
            "reportId": "UUID",
            "doctorName": "string",
            "fileUrl": "string",
            "uploadedAt": "timestamp"
        }
    ]
}
```

3. **Get Doctor Lab Reports**
- Endpoint: `GET /api/lab-reports/doctor`
- Response:
```json
{
    "reports": [
        {
            "reportId": "UUID",
            "patientName": "string",
            "fileUrl": "string",
            "uploadedAt": "timestamp"
        }
    ]
}
```

4. **Delete Lab Report**
- Endpoint: `DELETE /api/lab-reports/{report_id}`
- Response:
```json
{
    "message": "string"
}
```

### Miscellaneous APIs

1. **Get All Doctors**
- Endpoint: `GET /api/doctors`
- Response:
```json
{
    "doctors": [
        {
            "doctorId": "UUID",
            "name": "string",
            "speciality": "string",
            "clinicName": "string"
        }
    ]
}
```

2. **Get Connected Patients**
- Endpoint: `GET /api/doctor/patients`
- Response:
```json
{
    "patients": [
        {
            "patientId": "UUID",
            "name": "string",
            "email": "string",
            "phoneNumber": "string"
        }
    ]
}
```

## Security

- All endpoints except `/api/login`, `/api/signup`, `/api/refresh-token`, `/api/status`, `/`, and `/api/get-profile/**` require authentication
- Authentication is done using JWT tokens
- Tokens are passed in the Authorization header as `Bearer <token>`
- Access tokens have an expiry time
- Refresh tokens can be used to get new access tokens

## Error Handling

The API returns appropriate HTTP status codes and error messages in the following format:
```json
{
    "status": number,
    "error": "string",
    "message": "string"
}
```

Common error codes:
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error 