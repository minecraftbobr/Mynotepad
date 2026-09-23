# Mynotepad
Mynotepad - easy notepad
A lightweight cross-platform text editor written in Java using Swing. Designed as a simple, dependency-free alternative to heavier text editors, with multilingual support and theme customization.

## Features
File operations — open, save, and "Save As" with automatic .txt extension handling

Text search — built-in find dialog with wraparound search (Ctrl+F)

Multilingual interface — full localization for English, Russian, and Spanish; switch languages at runtime without restarting

Light and dark themes — toggle between a white background with black text and a black background with white text

Configurable save notifications — optional popup confirmation on save, can be disabled in settings

Persistent settings — preferences are stored in a hidden "settings.txt" file located next to the executable

Unsaved changes protection — prompts to save, discard, or cancel when closing the application

Keyboard shortcuts — Ctrl+O to open, Ctrl+S to save, Ctrl+Shift+S to save as, Ctrl+F to find

## Requirements

Java Runtime Environment (JRE) 8 or higher

No external libraries or dependencies

**64MB RAM** For **stable work**

**1.0 GHz** Processor (Intel Pentium 4 / AMD Athlon 64 or newer)

Windows 7+

**100KB** Free Space

## How to Build and Run

Download JDK 25 (LTS) From https://adoptium.net/temurin/releases and complete download
To compile the project and build the executable `.jar` installer, follow these steps:

1. Clone or download this repository to your local machine.
2. Ensure you have the **Java Development Kit (JDK) 8** or higher installed and configured in your system's PATH.
3. Make sure the project folder structure is preserved: **`build.bat`** must be in the root directory, and all `.java` files must be located inside the **`src/main/java/`** folder.
4. Double-click the **`build.bat`** file.
5. The script will automatically compile the source files from the `src` folder, create the separate components (`NotepadApp.jar`, `unins.jar`, `sha256sumcalc.jar`), pack them inside the temporary resources, and generate the final **`install.jar`** file in the root directory.
6. Run the generated **`install.jar`** to install the application.
