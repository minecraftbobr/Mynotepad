Imports System
Imports System.IO
Imports System.Reflection
Imports System.Diagnostics
Imports System.Windows.Forms
Imports System.Drawing
Imports System.Threading
Imports System.Collections.Generic

<Assembly: AssemblyVersion("1.0.0.0")>
<Assembly: AssemblyFileVersion("1.0.0.0")>
<Assembly: AssemblyProduct("MyNotepad")>
<Assembly: AssemblyTitle("MyNotepad Setup")>
<Assembly: AssemblyCopyright("Copyright © 2026 minecraftbobr")>

Public Class InstallerForm
    Inherits Form

    Private Const APP_NAME As String = "MyNotepad"
    Private Const APP_VERSION As String = "1.0"

    Private Const RES_NOTEPAD As String = "NotepadApp.jar"
    Private Const RES_UNINS As String = "unins.jar"
    Private Const RES_SHA256 As String = "sha256sumcalc.jar"

    Private lblTitle As Label
    Private lnkJava As LinkLabel
    Private lblLang As Label
    Private cmbLang As ComboBox
    Private lblPath As Label
    Private txtPath As TextBox
    Private btnBrowse As Button
    Private chkSha256 As CheckBox
    Private chkLaunch As CheckBox
    Private prgProgress As ProgressBar
    Private txtLog As TextBox
    Private btnInstall As Button
    Private btnCancel As Button

    Private installDir As String
    Private currentLang As String = "en"

    Private ReadOnly L As New Dictionary(Of String, Dictionary(Of String, String)) From {
        {"en", New Dictionary(Of String, String) From {
            {"title",        "Setup — My Notepad"},
            {"heading",      "Installing My Notepad 1.0"},
            {"javaHint",     "This program requires Java 8+ To download Click Here"},
            {"javaMissing",  "Java not found. This program requires Java 8+ — Click Here"},
            {"lang",         "Language:"},
            {"path",         "Install folder:"},
            {"browse",       "Browse..."},
            {"sha",          "Install SHA-256 calculator"},
            {"launch",       "Launch My Notepad after install"},
            {"install",      "Install"},
            {"cancel",       "Cancel"},
            {"logStart",     "Starting installation into "},
            {"logNotepad",   "Installed NotepadApp.jar"},
            {"logUnins",     "Installed unins.jar"},
            {"logSha",       "Installed sha256sumcalc.jar"},
            {"logShaSkip",   "SHA-256 calculator skipped"},
            {"logBat",       "Launcher created"},
            {"logSettings",  "settings.txt created"},
            {"logDone",      "Installation completed successfully!"},
            {"doneTitle",    "Done"},
            {"doneMsg",      "Installation complete!{0}{0}Launch My Notepad?"},
            {"errTitle",     "Error"},
            {"errMsg",       "Installation error:"},
            {"errLink",      "Cannot open link: "},
            {"errResource",  "Resource not found inside installer: "},
            {"errLaunch",    "Failed to launch application: "}
        }},
        {"ru", New Dictionary(Of String, String) From {
            {"title",        "Установка — Мой Блокнот"},
            {"heading",      "Установка Мой Блокнот 1.0"},
            {"javaHint",     "This program requires Java 8+ To download Click Here"},
            {"javaMissing",  "Java не найдена. This program requires Java 8+ — Click Here"},
            {"lang",         "Язык:"},
            {"path",         "Папка установки:"},
            {"browse",       "Обзор..."},
            {"sha",          "Установить калькулятор суммы SHA-256"},
            {"launch",       "Запустить Мой Блокнот после установки"},
            {"install",      "Установить"},
            {"cancel",       "Отмена"},
            {"logStart",     "Начало установки в "},
            {"logNotepad",   "Установлен NotepadApp.jar"},
            {"logUnins",     "Установлен unins.jar"},
            {"logSha",       "Установлен sha256sumcalc.jar"},
            {"logShaSkip",   "Калькулятор SHA-256 пропущен"},
            {"logBat",       "Создан файл запуска"},
            {"logSettings",  "Создан settings.txt"},
            {"logDone",      "Установка завершена успешно!"},
            {"doneTitle",    "Готово"},
            {"doneMsg",      "Установка завершена!{0}{0}Запустить Мой Блокнот?"},
            {"errTitle",     "Ошибка"},
            {"errMsg",       "Ошибка установки:"},
            {"errLink",      "Не удалось открыть ссылку: "},
            {"errResource",  "Ресурс не найден внутри установщика: "},
            {"errLaunch",    "Не удалось запустить приложение: "}
        }},
        {"es", New Dictionary(Of String, String) From {
            {"title",        "Instalación — Mi Bloc de Notas"},
            {"heading",      "Instalando Mi Bloc de Notas 1.0"},
            {"javaHint",     "This program requires Java 8+ To download Click Here"},
            {"javaMissing",  "Java no encontrada. This program requires Java 8+ — Click Here"},
            {"lang",         "Idioma:"},
            {"path",         "Carpeta de instalación:"},
            {"browse",       "Examinar..."},
            {"sha",          "Instalar calculadora SHA-256"},
            {"launch",       "Ejecutar Mi Bloc de Notas tras instalar"},
            {"install",      "Instalar"},
            {"cancel",       "Cancelar"},
            {"logStart",     "Iniciando instalación en "},
            {"logNotepad",   "Instalado NotepadApp.jar"},
            {"logUnins",     "Instalado unins.jar"},
            {"logSha",       "Instalado sha256sumcalc.jar"},
            {"logShaSkip",   "Calculadora SHA-256 omitida"},
            {"logBat",       "Lanzador creado"},
            {"logSettings",  "settings.txt creado"},
            {"logDone",      "¡Instalación completada con éxito!"},
            {"doneTitle",    "Listo"},
            {"doneMsg",      "¡Instalación completada!{0}{0}¿Ejecutar Mi Bloc de Notas?"},
            {"errTitle",     "Error"},
            {"errMsg",       "Error de instalación:"},
            {"errLink",      "No se pudo abrir el enlace: "},
            {"errResource",  "Recurso no encontrado dentro del instalador: "},
            {"errLaunch",    "No se pudo iniciar la aplicación: "}
        }}
    }

    Private Function T(key As String) As String
        If L.ContainsKey(currentLang) AndAlso L(currentLang).ContainsKey(key) Then
            Return L(currentLang)(key)
        End If
        Return L("en")(key)
    End Function

    Public Sub New()
        Me.Text = T("title")
        Me.Size = New Size(580, 520)
        Me.StartPosition = FormStartPosition.CenterScreen
        Me.FormBorderStyle = FormBorderStyle.FixedDialog
        Me.MaximizeBox = False
        Me.MinimizeBox = False

        lblTitle = New Label()
        lblTitle.Font = New Font("Arial", 14, FontStyle.Bold)
        lblTitle.Location = New Point(20, 15)
        lblTitle.Size = New Size(400, 30)
        Me.Controls.Add(lblTitle)

        lblLang = New Label()
        lblLang.Location = New Point(440, 22)
        lblLang.Size = New Size(60, 20)
        lblLang.TextAlign = ContentAlignment.MiddleRight
        Me.Controls.Add(lblLang)

        cmbLang = New ComboBox()
        cmbLang.DropDownStyle = ComboBoxStyle.DropDownList
        cmbLang.Location = New Point(505, 19)
        cmbLang.Size = New Size(50, 22)
        cmbLang.Items.AddRange(New Object() {"EN", "RU", "ES"})
        cmbLang.SelectedIndex = 0
        AddHandler cmbLang.SelectedIndexChanged, AddressOf CmbLang_Changed
        Me.Controls.Add(cmbLang)

        lnkJava = New LinkLabel()
        lnkJava.Font = New Font("Arial", 9, FontStyle.Regular)
        lnkJava.Location = New Point(20, 48)
        lnkJava.Size = New Size(540, 20)
        AddHandler lnkJava.LinkClicked, AddressOf LnkJava_LinkClicked
        Me.Controls.Add(lnkJava)

        lblPath = New Label()
        lblPath.Location = New Point(20, 80)
        lblPath.Size = New Size(110, 20)
        Me.Controls.Add(lblPath)

        Dim defaultPath As String = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), APP_NAME)
        txtPath = New TextBox()
        txtPath.Text = defaultPath
        txtPath.Location = New Point(135, 77)
        txtPath.Size = New Size(310, 20)
        Me.Controls.Add(txtPath)

        btnBrowse = New Button()
        btnBrowse.Location = New Point(455, 75)
        btnBrowse.Size = New Size(85, 24)
        AddHandler btnBrowse.Click, AddressOf BtnBrowse_Click
        Me.Controls.Add(btnBrowse)

        chkSha256 = New CheckBox()
        chkSha256.Location = New Point(23, 115)
        chkSha256.Size = New Size(400, 20)
        Me.Controls.Add(chkSha256)

        chkLaunch = New CheckBox()
        chkLaunch.Location = New Point(23, 140)
        chkLaunch.Size = New Size(400, 20)
        Me.Controls.Add(chkLaunch)

        prgProgress = New ProgressBar()
        prgProgress.Location = New Point(23, 175)
        prgProgress.Size = New Size(517, 23)
        Me.Controls.Add(prgProgress)

        txtLog = New TextBox()
        txtLog.Multiline = True
        txtLog.ReadOnly = True
        txtLog.ScrollBars = ScrollBars.Vertical
        txtLog.Font = New Font("Consolas", 9)
        txtLog.Location = New Point(23, 210)
        txtLog.Size = New Size(517, 160)
        Me.Controls.Add(txtLog)

        btnCancel = New Button()
        btnCancel.Location = New Point(455, 390)
        btnCancel.Size = New Size(85, 25)
        AddHandler btnCancel.Click, Sub() Application.Exit()
        Me.Controls.Add(btnCancel)

        btnInstall = New Button()
        btnInstall.Location = New Point(360, 390)
        btnInstall.Size = New Size(85, 25)
        AddHandler btnInstall.Click, AddressOf BtnInstall_Click
        Me.Controls.Add(btnInstall)

        ApplyLanguage()
        CheckJava()
    End Sub

    Private Sub ApplyLanguage()
        Me.Text = T("title")
        lblTitle.Text = T("heading")
        lblLang.Text = T("lang")
        lblPath.Text = T("path")
        btnBrowse.Text = T("browse")
        chkSha256.Text = T("sha")
        chkLaunch.Text = T("launch")
        btnInstall.Text = T("install")
        btnCancel.Text = T("cancel")

        If lnkJava.Tag IsNot Nothing AndAlso lnkJava.Tag.ToString() = "missing" Then
            lnkJava.Text = T("javaMissing")
        Else
            lnkJava.Text = T("javaHint")
        End If

        Dim linkText As String = "Click Here"
        Dim idx As Integer = lnkJava.Text.IndexOf(linkText)
        If idx >= 0 Then
            lnkJava.LinkArea = New LinkArea(idx, linkText.Length)
        Else
            lnkJava.LinkArea = New LinkArea(0, 0)
        End If
    End Sub

    Private Sub CmbLang_Changed(sender As Object, e As EventArgs)
        Select Case cmbLang.SelectedIndex
            Case 0 : currentLang = "en"
            Case 1 : currentLang = "ru"
            Case 2 : currentLang = "es"
            Case Else : currentLang = "en"
        End Select
        ApplyLanguage()
    End Sub

    Private Sub CheckJava()
        Dim available As Boolean = IsJavaAvailable()
        If Not available Then
            lnkJava.Tag = "missing"
            lnkJava.LinkColor = Color.Red
            lnkJava.ActiveLinkColor = Color.DarkRed
        Else
            lnkJava.Tag = Nothing
            lnkJava.LinkColor = Color.Blue
            lnkJava.ActiveLinkColor = Color.Red
        End If
        ApplyLanguage()
    End Sub

    Private Function IsJavaAvailable() As Boolean
        Try
            Dim psi As New ProcessStartInfo("javaw", "-version")
            psi.UseShellExecute = False
            psi.RedirectStandardError = True
            psi.CreateNoWindow = True
            Using p As Process = Process.Start(psi)
                p.WaitForExit(3000)
                Return p.ExitCode = 0 OrElse p.ExitCode = 1
            End Using
        Catch
            Return False
        End Try
    End Function

    Private Sub LnkJava_LinkClicked(sender As Object, e As LinkLabelLinkClickedEventArgs)
        Try
            Dim psi As New ProcessStartInfo("https://www.java.com/en/download/manual.jsp")
            psi.UseShellExecute = True
            Process.Start(psi)
        Catch ex As Exception
            MessageBox.Show(T("errLink") & ex.Message, T("errTitle"),
                            MessageBoxButtons.OK, MessageBoxIcon.Warning)
        End Try
    End Sub

    Private Sub BtnBrowse_Click(sender As Object, e As EventArgs)
        Using fbd As New FolderBrowserDialog()
            If fbd.ShowDialog() = DialogResult.OK Then
                txtPath.Text = fbd.SelectedPath
            End If
        End Using
    End Sub

    Private Sub BtnInstall_Click(sender As Object, e As EventArgs)
        installDir = txtPath.Text.Trim()
        btnInstall.Enabled = False
        btnBrowse.Enabled = False
        cmbLang.Enabled = False
        txtPath.ReadOnly = True

        Dim t As New Thread(AddressOf RunInstall)
        t.IsBackground = True
        t.Start()
    End Sub

    Private Sub RunInstall()
        Try
            Log(T("logStart") & installDir)
            SetProgress(5)

            If Not Directory.Exists(installDir) Then
                Directory.CreateDirectory(installDir)
            End If
            SetProgress(15)

            ExtractResource(RES_NOTEPAD, Path.Combine(installDir, "NotepadApp.jar"))
            SetProgress(40)
            Log(T("logNotepad"))

            ExtractResource(RES_UNINS, Path.Combine(installDir, "unins.jar"))
            SetProgress(60)
            Log(T("logUnins"))

            Dim installSha As Boolean = False
            Me.Invoke(Sub() installSha = chkSha256.Checked)

            If installSha Then
                ExtractResource(RES_SHA256, Path.Combine(installDir, "sha256sumcalc.jar"))
                SetProgress(80)
                Log(T("logSha"))
            Else
                SetProgress(80)
                Log(T("logShaSkip"))
            End If

            CreateLauncherBat()
            SetProgress(90)
            Log(T("logBat"))

            CreateSettingsFile()
            SetProgress(95)
            Log(T("logSettings"))

            SetProgress(100)
            Log(T("logDone"))

            Dim launchChecked As Boolean = False
            Me.Invoke(Sub() launchChecked = chkLaunch.Checked)

            Dim doneMsg As String = String.Format(T("doneMsg"), Environment.NewLine)
            Dim choice As DialogResult = MessageBox.Show(doneMsg, T("doneTitle"),
                                                          MessageBoxButtons.YesNo,
                                                          MessageBoxIcon.Information)

            If launchChecked OrElse choice = DialogResult.Yes Then
                LaunchApp()
            End If

            Me.Invoke(Sub() Application.Exit())

        Catch ex As Exception
            Log(T("errMsg") & " " & ex.Message)
            MessageBox.Show(T("errMsg") & Environment.NewLine & ex.Message,
                            T("errTitle"), MessageBoxButtons.OK, MessageBoxIcon.Error)
            Me.Invoke(Sub()
                          btnInstall.Enabled = True
                          btnBrowse.Enabled = True
                          cmbLang.Enabled = True
                          txtPath.ReadOnly = False
                      End Sub)
        End Try
    End Sub

    Private Sub ExtractResource(resourceName As String, destination As String)
        Dim currentAssembly As Assembly = Assembly.GetExecutingAssembly()

        Dim fullResourceName As String = ""
        For Each name As String In currentAssembly.GetManifestResourceNames()
            If name.EndsWith(resourceName, StringComparison.OrdinalIgnoreCase) Then
                fullResourceName = name
                Exit For
            End If
        Next

        If String.IsNullOrEmpty(fullResourceName) Then
            Throw New Exception(T("errResource") & resourceName)
        End If

        Using stream As Stream = currentAssembly.GetManifestResourceStream(fullResourceName)
            Using fileStream As New FileStream(destination, FileMode.Create, FileAccess.Write)
                stream.CopyTo(fileStream)
            End Using
        End Using
    End Sub

    Private Sub CreateLauncherBat()
        Dim batPath As String = Path.Combine(installDir, "Start.bat")
        Dim content As String = "@echo off" & Environment.NewLine &
            "start """" javaw -jar """ & installDir & "\NotepadApp.jar""" & Environment.NewLine
        File.WriteAllText(batPath, content, System.Text.Encoding.GetEncoding(866))
    End Sub

    Private Sub CreateSettingsFile()
        Dim settingsPath As String = Path.Combine(installDir, "settings.txt")

        Dim mskNow As DateTime = DateTime.UtcNow.AddHours(3)
        Dim timeStamp As String =
            mskNow.ToString("ddd MMM dd HH:mm:ss",
                            New System.Globalization.CultureInfo("en-US")) &
            " MSK " & mskNow.Year.ToString()

        Dim sb As New System.Text.StringBuilder()
        sb.AppendLine("#MyNotepad settings")
        sb.AppendLine("#" & timeStamp)
        sb.AppendLine("language=" & currentLang)

        Dim utf8NoBom As New System.Text.UTF8Encoding(False)
        File.WriteAllText(settingsPath, sb.ToString(), utf8NoBom)

        Dim fi As New FileInfo(settingsPath)
        fi.Attributes = fi.Attributes Or FileAttributes.Hidden
    End Sub

    Private Sub LaunchApp()
        Try
            Dim psi As New ProcessStartInfo()
            psi.FileName = "javaw"
            psi.Arguments = "-jar """ & Path.Combine(installDir, "NotepadApp.jar") & """"
            psi.UseShellExecute = True
            Process.Start(psi)
        Catch ex As Exception
            Log(T("errLaunch") & ex.Message)
            Thread.Sleep(2000)
        End Try
    End Sub

    Private Sub Log(msg As String)
        If Me.InvokeRequired Then
            Me.Invoke(Sub() Log(msg))
        Else
            txtLog.AppendText(msg & Environment.NewLine)
        End If
    End Sub

    Private Sub SetProgress(value As Integer)
        If Me.InvokeRequired Then
            Me.Invoke(Sub() SetProgress(value))
        Else
            prgProgress.Value = value
        End If
    End Sub

    <STAThread>
    Public Shared Sub Main()
        Application.EnableVisualStyles()
        Application.SetCompatibleTextRenderingDefault(False)
        Application.Run(New InstallerForm())
    End Sub
End Class