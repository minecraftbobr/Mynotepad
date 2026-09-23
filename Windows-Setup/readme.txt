This directory contains the pure VB.NET source code for "Setup.exe" (installer wrapper). 
It compiles standalone and does not require Visual Studio.

Prerequisites before compiling:
1. Compile the Java files first using "build.bat" in the root directory.
2. Copy the generated JAR files ("NotepadApp.jar", "unins.jar", "sha256sumcalc.jar") into this "windows-setup" folder so the compiler can embed them as resources.

Compilation command (Run from this directory in CMD or PowerShell):

C:\Windows\Microsoft.NET\Framework64\v4.0.30319\vbc.exe /target:winexe /out:Setup.exe /nowin32manifest /nowarn:42024 /resource:NotepadApp.jar /resource:unins.jar /resource:sha256sumcalc.jar MyNotePadinstaller.vb

Output:
A standalone "Setup.exe" will be generated in this folder.