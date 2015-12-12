Set objShell = WScript.CreateObject("WScript.Shell")

deskt = objShell.SpecialFolders("Desktop")
appDataLocation=objShell.ExpandEnvironmentStrings("%APPDATA%")

Set lnk = objShell.CreateShortcut(deskt & "\MC Modinstaller.LNK")
lnk.TargetPath = appDataLocation & "\Modinstaller\MCModinstaller.exe"
lnk.Arguments = ""
lnk.Description = "Minecraft Modinstaller starten"
lnk.HotKey = "ALT+CTRL+M"
lnk.IconLocation = appDataLocation & "\Modinstaller\MCModinstaller.exe"
lnk.WindowStyle = "1"
lnk.WorkingDirectory = appDataLocation & "\Modinstaller"
lnk.Save

Set lnk2 = objShell.CreateShortcut(appDataLocation & "\Microsoft\Windows\Start Menu\Programs\MC Modinstaller.LNK")
lnk2.TargetPath = appDataLocation & "\Modinstaller\MCModinstaller.exe"
lnk2.Arguments = ""
lnk2.Description = "Minecraft Modinstaller starten"
lnk2.HotKey = "ALT+CTRL+M"
lnk2.IconLocation = appDataLocation & "\Modinstaller\MCModinstaller.exe"
lnk2.WindowStyle = "1"
lnk2.WorkingDirectory = appDataLocation & "\Modinstaller"
lnk2.Save

Set lnk = Nothing
Set lnk2 = Nothing