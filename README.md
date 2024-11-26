# **EmojiApp**

EmojiApp is a sample Android application demonstrating the integration of APIs, modern Android development practices, and UI/UX principles. It showcases fetching and displaying data from GitHub and Google APIs, providing a robust example of modular, testable, and scalable app architecture.

---

## **Features**

- **Emoji List**  
  Fetch and display a random or list of emojis from GitHub's emoji API. The app allows users to interact with the emoji list, including adding, removing, and undoing actions, all with a responsive UI.

  ![emojis](https://github.com/user-attachments/assets/8da8c380-d4d4-4535-aefb-5ab94e60ff27)

- **Avatar Search**  
  Search for GitHub users by username. Display user avatars and cache them locally for future use, reducing redundant API calls.

  ![avatars](https://github.com/user-attachments/assets/fa9b9e0b-1e1d-4fb7-8919-caba43b16adc)

- **Google Repos Browser**  
  Explore GitHub repositories under the Google organization. Features:
    - Pagination and Caching for efficient data fetching.
    - Tap to visit repository details directly on GitHub.

  ![repos_v2](https://github.com/user-attachments/assets/e2a4cfb7-18dd-4f39-9923-45594dde9092)

---

## **Tech Stack**

### **Language & Frameworks**
- **Kotlin**: Primary programming language for Android.
- **Jetpack Compose**: Modern UI toolkit for building native UIs.
- **Hilt**: Dependency injection framework for cleaner code and easier testing.

### **Architecture**
- **MVVM (Model-View-ViewModel)**: Separates UI logic from data handling for better testability and modularity.
- **Paging 3**: Handles large datasets with support for efficient data fetching and caching.
- **Room**: Local database to store and manage app data.

### **Networking**
- **Retrofit**: REST API client build on top of OkHttp for making network calls.
- **OkHttp**: HTTP client for handling requests and responses.
- **Coil**: A Modern Image loading library optimized for current Android best development practices.

### **Utilities**
- **GitFlow**: Git branching model for streamlined development and release management.
- **Timber**: Logging utility for debugging.

---

## **APIs Used**

1. **GitHub APIs**
    - **Emoji API**: Fetch a list of emojis.
    - **User API**: Retrieve user details like avatar and username.
    - **Repos API**: Fetch a paginated list of repositories.
---

## **License**
This project is licensed under the [MIT License](LICENSE).
