# 🗳️ InnoVote Project
*InnoVote* adalah aplikasi desktop berbasis JavaFX yang memungkinkan pengguna untuk mengirimkan ide-ide inovatif secara interaktif, serta memungkinkan admin untuk mengelola dan mereview ide-ide yang masuk. Aplikasi ini dibangun dengan pendekatan Object-Oriented Programming (OOP) dan arsitektur modular.

---

## 🧠 Latar Belakang
Dalam dunia yang terus berkembang, inovasi menjadi kunci untuk kemajuan. Namun, banyak organisasi, sekolah, atau komunitas kesulitan dalam mengelola dan menampung ide dari anggotanya. Oleh karena itu, **InnoVote** hadir sebagai solusi digital untuk menampung ide dari berbagai partisipan dan mempermudah admin dalam mengelolanya.

---

## 🔍 Deskripsi Proyek
*InnoVote* dirancang sebagai aplikasi desktop berbasis Java yang memiliki dua peran utama:
    - **Partisipan** dapat mengisi identitas dan mengirimkan ide mereka.
    - **Admin** dapat melihat dan mengelola seluruh ide yang telah dikirimkan.
        Semua data disimpan secara lokal dalam file teks, tanpa menggunakan database eksternal, sehingga ringan dan mudah dijalankan.
---

## 📦 Cara Menjalankan Aplikasi
### ✅ Prasyarat
- Java Development Kit (JDK) 17+
- Apache Maven
- JavaFX SDK (sesuai versi JDK)

### ▶️ Langkah Menjalankan
1. *Clone repository*
   ```bash
    git clone https://github.com/Anugrahfitri-code/InnoVote-project-Klp-32.git
    cd InnoVoteProject
2. *Jalankan Aplikasi*
    mvn clean javafx:run

## Structur Project
InnoVoteProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/innovote/
│   │   │       ├── models/         # Kelas data: Participant, Idea
│   │   │       ├── screens/        # UI JavaFX: AdminScreen, ParticipantScreen, dsb
│   │   │       ├── services/       # Layanan logika: IdeaService, FileService
│   │   │       └── utils/          # Utilitas umum: Theme, AlertUtil
│   └── test/                       # Pengujian (optional)
├── lib/                            # Library eksternal
├── pom.xml                         # Konfigurasi Maven
└── README.md                       # Dokumentasi proyek ini

