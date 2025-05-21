# 🛡️ SC2006 Lab 5 – Crime Reporting App

This is the final version of our software engineering lab project. It is a full-stack mobile app that enables users to report crimes, locate nearby police stations, and access crime trend data in Singapore.

---

## 📽️ Demo Video

Click the thumbnail below to watch a walkthrough of the **CrimeWatch Police Contact App**, showcasing its features, interface, and user experience.

[![Watch the demo](https://img.youtube.com/vi/C4v-a-K5KUw/0.jpg)](https://youtu.be/C4v-a-K5KUw)

---

## 🛠️ Built With

- **React Native (Expo)** – For building the cross-platform mobile app
- **FastAPI (Python)** – Backend framework to handle API logic
- **MySQL** – Relational database to store user and report data
- **Google Maps API** – For location mapping and directions
- **Twilio** – For sending emergency SMS alerts

---

## 📁 Folder Structure

```
lab5/
├── project/
│   ├── frontend/                  # React Native app (Expo)
│   ├── backend/                   # FastAPI backend with API routes
│   ├── database/                  # MySQL schema & migration scripts
├── docs/                          # Final documentation files
│   ├── SRS.pdf
│   ├── functional_requirements.pdf
│   ├── class_diagram.pdf
│   ├── sequence_diagram.pdf.pdf
│   ├── use_case_diagram.pdf
│   ├── use_case_description.pdf
│   ├── data_dictionary.pdf
│   ├── test_cases.pdf
│   ├── peer_evaluation_form.pdf
│   ├── system_architecture.pdf
└── README.md                      # You are here
```
---

## 🚀 Getting Started

### ✅ Prerequisites

- Node.js & npm
- Python 3.10+
- MySQL Server
- Expo Go (for mobile testing)

---

### ⚙️ Backend Setup (FastAPI)

#### 🔹 Option 1: With Virtual Environment
```bash
python -m venv venv
source venv/bin/activate      # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

#### 🔹 Option 2: Without Virtual Environment
```bash
pip install -r requirements.txt
```

### ▶️ Starting Backend
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

---

### 📱 Frontend Setup (React Native with Expo)

```bash
cd project/frontend/
npm install
npx expo start
```

- Scan the QR code with **Expo Go** to run the app on your phone.
- Make sure your **phone and computer are on the same Wi-Fi network**.
- Update the backend URL in `constants.ts` to your local IP:

```ts
// project/frontend/constants.ts
export const BASE_URL = "http://<your-local-ip>:8000";
```

> 💡 Use `ipconfig` (Windows) or `ifconfig` / `ip a` (Mac/Linux) to find your IP.

---

### 🛢️ MySQL Database Setup

1. Open your MySQL client (e.g. MySQL Workbench or terminal).
2. Create a new database:
```sql
CREATE DATABASE crime_reporting;
```
3. Import the schema from the project:
```bash
mysql -u root -p crime_reporting < project/database/schema.sql
```
4. Make sure your database credentials are correctly set in:
```env
project/backend/.env
```

Example `.env`:
```
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=crime_reporting
```

---

### 📂 .gitignore

Create a `.gitignore` file in both `project/backend/` and `project/frontend/` and add:

```
# Node modules
node_modules/

# macOS system files
.DS_Store

# Python
__pycache__/
*.py[cod]

# Environment files
.env
.env.local
```

---

## 📄 Final Documentation (in `/docs`)

| Document | Description |
|----------|-------------|
| `SRS.pdf` | Software Requirements Specification |
| `functional_requirements.pdf` | Functional requirements overview |
| `use_case_diagram.pdf` | Visual diagram of use cases |
| `use_case_description.pdf` | Detailed description of each use case |
| `sequence_diagram.pdf` | Sequence diagram |
| `class_diagram.pdf` | Class relationships and system structure |
| `system_architecture.pdf` | High-level architecture and component design |
| `data_dictionary.pdf` | Definitions and formats of key data fields |
| `test_cases.pdf` | Manual test cases for feature validation |
| `peer_evaluation_form.pdf` | Team member peer evaluation submission |

---

## 🧠 Software Engineering Concepts Applied

- ✅ Clean architecture with separation of frontend/backend concerns
- ✅ Requirements traceability from SRS → use cases → sequence → tests
- ✅ MVC-inspired backend structure with modular files
- ✅ Secure handling of credentials and environment configs
- ✅ Version control using GitHub
- ✅ Final app tested and demo-ready via Expo

---

## 👥 Team Members

- Zayd Shabaz Ali
- Tan Chuen Keat
- Khor Haojun
- Dommaraju Pranati
- Agarwal Dhruvikaa

---

## 📎 Note on Previous Labs

This is the final version submitted for **Lab 5**. Earlier development iterations (Lab 1–3) are preserved in the project root folders for traceability and review.

---

## 📜 License

This project is part of NTU SC2006 and is submitted for academic evaluation only.
