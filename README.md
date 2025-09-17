# 🎬 SubStream

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Reddit-FF4500?style=for-the-badge&logo=reddit&logoColor=white" />
</div>

<div align="center">
  <h3>📱 A Modern Android Video Streaming App for Reddit Content</h3>
  <p>Built with Jetpack Compose, Clean Architecture, and Reddit API integration</p>
</div>

---

## 🌟 Overview

**SubStream** is a modern Android application that transforms Reddit's video content into a TikTok-like vertical scrolling experience using the **Reddit API** and **ExoPlayer** for seamless video playback. Built with cutting-edge Android technologies, it showcases advanced mobile development skills including Clean Architecture, Jetpack Compose, and modern Android development practices.

### 🎯 Key Features

- **🎬 Vertical Video Feed** - TikTok-style scrolling through Reddit videos using **ExoPlayer**
- **🔍 Smart Search** - Find subreddits with real-time search via **Reddit API**
- **📊 Multiple Sorting Options** - Hot, New, Top, and Rising post sorting
- **🎵 Audio Controls** - Global mute/unmute with visual feedback
- **📱 Modern UI/UX** - Material Design 3 with smooth animations
- **🔄 Infinite Scrolling** - Seamless pagination for endless content
- **⚡ Offline Support** - Robust error handling and retry mechanisms
- **🌐 Reddit API Integration** - OAuth authentication and data fetching
- **🎥 ExoPlayer Video Streaming** - Advanced video playback with custom controls

---

## 🏗️ Architecture

This project demonstrates **Clean Architecture** principles with clear separation of concerns:

```
📁 app/src/main/java/com/rookie/code/substream/
├── 🎨 presentation/          # UI Layer (Compose + ViewModels)
│   ├── screen/              # Screen composables
│   ├── composables/         # Reusable UI components
│   └── viewmodel/           # State management
├── 🧠 domain/               # Business Logic Layer
│   ├── entity/              # Domain models
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Business use cases
├── 💾 data/                 # Data Layer
│   ├── repository/          # Repository implementations
│   ├── online/              # API services
│   ├── model/               # Data models
│   └── utils/               # Utility classes
└── 🔧 di/                   # Dependency Injection
    ├── AppModule.kt
    ├── NetworkModule.kt
    └── ViewModelModule.kt
```

### 🎯 Architecture Benefits

- **🧪 Testable** - Each layer can be tested independently
- **🔄 Maintainable** - Clear separation makes code easy to modify
- **📈 Scalable** - Easy to add new features without affecting existing code
- **🛡️ Robust** - Proper error handling and state management

---

## 🛠️ Tech Stack

### Core Technologies
- **📱 Android SDK 24+** - Modern Android development
- **🐘 Kotlin 2.0** - 100% Kotlin codebase
- **🎨 Jetpack Compose** - Modern declarative UI toolkit
- **🏗️ Clean Architecture** - Scalable and maintainable code structure

### Libraries & Frameworks
- **🌐 Ktor 2.3.12** - HTTP client for Reddit API integration
- **💉 Koin 3.5.6** - Dependency injection framework
- **📦 Kotlinx Serialization** - JSON parsing and serialization
- **🔄 Kotlinx Coroutines** - Asynchronous programming
- **🎬 ExoPlayer** - Advanced video playback capabilities

### Development Tools
- **🔧 Gradle 8.7.3** - Build automation
- **📊 OkHttp Profiler** - Network debugging
- **🎨 Material Design 3** - Modern UI components

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 24+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/SubStream.git
   cd SubStream
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Wait for Gradle sync to complete

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's run button (▶️)

### 🔑 API Configuration

The app uses **Reddit's OAuth API** for authentication and data fetching, combined with **ExoPlayer** for video streaming. No additional setup required - API credentials are included for demonstration purposes.

**Key API Features:**
- **Reddit OAuth 2.0** authentication
- **Subreddit data fetching** (popular, search, posts)
- **Video URL extraction** from Reddit posts
- **ExoPlayer streaming** for multiple video formats

---

## 📱 Screenshots

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="https://via.placeholder.com/300x600/3DDC84/FFFFFF?text=Home+Screen" alt="Home Screen" width="200"/>
        <br><b>Home Screen</b><br>Search & Browse Subreddits
      </td>
      <td align="center">
        <img src="https://via.placeholder.com/300x600/FF4500/FFFFFF?text=Video+Feed" alt="Video Feed" width="200"/>
        <br><b>Video Feed</b><br>TikTok-style Scrolling
      </td>
      <td align="center">
        <img src="https://via.placeholder.com/300x600/4285F4/FFFFFF?text=Video+Player" alt="Video Player" width="200"/>
        <br><b>Video Player</b><br>Full-screen Controls
      </td>
    </tr>
  </table>
</div>

---

## 🎯 Key Implementation Highlights

### 🏗️ Clean Architecture Implementation
- **Domain Layer**: Pure business logic with entities and use cases
- **Data Layer**: Repository pattern with API integration
- **Presentation Layer**: Jetpack Compose with MVVM pattern

### 🎨 Modern UI/UX
- **Material Design 3** components and theming
- **Smooth animations** and transitions
- **Responsive design** for different screen sizes
- **Dark/Light theme** support

### 🔄 State Management
- **StateFlow** for reactive state management
- **ViewModel** for UI state handling
- **Compose state** for local UI state

### 🌐 Network Layer
- **Reddit API integration** with OAuth authentication
- **Ktor HTTP client** for API communication
- **Token management** for secure authentication
- **Error handling** and retry mechanisms

### 🎬 Video Features
- **ExoPlayer integration** for Reddit video playback
- **Custom video controls** with play/pause
- **Global mute/unmute** functionality
- **Vertical scrolling** video feed
- **Multiple video format** support (MP4, WebM, HLS, DASH)
- **Reddit video URL extraction** and streaming

---

## 📊 Code Quality

### 🧪 Testing
- **Unit tests** for business logic
- **UI tests** for Compose components
- **Integration tests** for API calls

### 📝 Code Standards
- **100% Kotlin** codebase
- **Clean code** principles
- **Comprehensive documentation**
- **Consistent naming** conventions

### 🔍 Code Analysis
- **Lint checks** for code quality
- **ProGuard** for release builds
- **Memory leak** prevention

---

## 🚀 Performance Optimizations

- **Lazy loading** for video content
- **Image caching** for thumbnails
- **Memory management** for video players
- **Efficient state updates** with Compose
- **Background processing** with Coroutines

---

## 🔮 Future Enhancements

- [ ] **User authentication** with Reddit login
- [ ] **Favorites system** for saved videos
- [ ] **Download functionality** for offline viewing
- [ ] **Push notifications** for new content
- [ ] **Social features** like comments and likes
- [ ] **Dark mode** theme toggle
- [ ] **Accessibility** improvements

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Guidelines
1. Follow Clean Architecture principles
2. Write comprehensive tests
3. Update documentation
4. Follow Kotlin coding standards

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 About the Developer

This project showcases advanced Android development skills including:

- **Modern Android Development** with Jetpack Compose
- **Clean Architecture** implementation
- **Reddit API Integration** with OAuth authentication
- **ExoPlayer Video Streaming** for Reddit video content
- **State Management** with StateFlow and ViewModels
- **Dependency Injection** with Koin
- **Network Layer** with Ktor
- **UI/UX Design** with Material Design 3
- **Video URL Extraction** from Reddit posts
- **Custom Video Controls** and playback management

---

<div align="center">
  <h3>🌟 Star this repository if you found it helpful!</h3>
  <p>Built with ❤️ using Jetpack Compose and Clean Architecture</p>
</div>

---

<div align="center">
  <img src="https://img.shields.io/badge/Made%20with-Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-FF6B6B?style=for-the-badge" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge" />
</div>
