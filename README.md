# InnoVote – Sistem Event & Voting Komunitas Inovator

## 📅 Event
Kompetisi Ide Inovasi Mahasiswa 2025

## 🧠 Deskripsi Aplikasi
**InnoVote** adalah aplikasi berbasis JavaFX yang dirancang untuk mendukung event kompetisi inovasi mahasiswa. Aplikasi ini memungkinkan:

- Pendaftaran peserta acara kompetisi inovasi
- Manajemen komunitas peserta berdasarkan topik inovasi
- Voting ide terbaik oleh anggota komunitas
- Pengelolaan ide dan komentar dari juri

---

## 🖥️ Scene Aplikasi

### 🔐 Scene 1 – Login / Pendaftaran User
- User dapat login sebagai **Peserta** atau **Juri**
- Jika belum memiliki akun, pengguna dapat mendaftar melalui halaman pendaftaran

### 🏠 Scene 2 – Halaman Utama
- **Peserta**
  - Mendaftarkan ide inovasi
  - Melihat ide-ide dari peserta lain

- **Juri**
  - Melihat semua ide peserta
  - Memberikan vote dan komentar pada ide yang dipilih

---

## 🌟 Fitur Tambahan
- Daftar komunitas inovator berdasarkan topik (AI, Lingkungan, Kesehatan, dll.)
- Statistik jumlah vote per ide
- Deadline waktu voting

---

## 💡 Penerapan OOP

### ✅ Encapsulation
- Atribut dalam class `User`, `Idea`, `Vote`, `Community` bersifat private, dengan akses melalui setter dan getter

### 🧬 Inheritance
- Superclass: `User`
- Subclass: `Participant`, `Judge`

### 🔍 Abstraction
- Kelas abstrak `EventUser` dengan method abstrak seperti `showDashboard()`, `submitIdea()`, yang diimplementasi berbeda oleh subclass

### 🔁 Polymorphism
- Method `voteIdea()` dioverride:
  - `Judge` dapat melakukan voting
  - `Participant` tidak dapat melakukan voting

---

## 👥 Anggota Tim

| NIM         | Nama Lengkap                | Kontribusi                                                                 |
|-------------|-----------------------------|----------------------------------------------------------------------------|
| H071241080  | Hanifah Atthahira Basir     | `models/User`, `Judge`, `Participant`<br>`screens/auth/LoginScreen`, `RegisterScreen`<br>`services/AuthService`<br>`session/Session`<br>`utils/Validator`, `AlertHelper`<br>`exceptions/AuthException` |
| H071241026  | Natasya                     | `models/Idea`, `EventUser`<br>`screens/participant/IdeaSubmissionScreen`, `ParticipantDashboardScreen`<br>`screens/common/IdeaDetailScreen`<br>`services/IdeaService`<br>`exceptions/IdeaException` |
| H071241058  | Anugrah Fitri Novanda       | `models/Vote`<br>`screens/judge/JudgeDashboardScreen`, `VotingScreen`<br>`services/VotingService`<br>`exceptions/VotingException`<br>`utils/SceneManager`, `DummyDataBase`.  `Theme` |

---

## 📁 Struktur Proyek
├── models/
│ ├── User.java
│ ├── Judge.java
│ ├── Participant.java
│ ├── Idea.java
│ ├── EventUser.java
│ └── Vote.java
│
├── screens/
│ ├── auth/
│ │ ├── LoginScreen.java
│ │ └── RegisterScreen.java
│ ├── participant/
│ │ ├── IdeaSubmissionScreen.java
│ │ └── ParticipantDashboardScreen.java
│ ├── judge/
│ │ ├── JudgeDashboardScreen.java
│ │ └── VotingScreen.java
│ └── common/
│ └── IdeaDetailScreen.java
│
├── services/
│ ├── AuthService.java
│ ├── IdeaService.java
│ └── VotingService.java
│
├── session/
│ └── Session.java
│
├── utils/
│ ├── Validator.java
│ ├── AlertHelper.java
│ ├── SceneManager.java
│ └── DummyDataBase.java
└ ── Theme.java
│
└── exceptions/
├── AuthException.java
├── IdeaException.java
└── VotingException.java
