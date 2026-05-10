# Fix package declarations in semproject
$semprojectPath = "C:\Users\MOBILE ISLAND\AndroidStudioProjects\Slotify\app\src\main\java\com\example\slotify\semproject"
Get-ChildItem -Path $semprojectPath -Recurse -Filter *.kt | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.semproject', 'package com.example.slotify.semproject'
    $content = $content -replace 'import com\.example\.semproject', 'import com.example.slotify.semproject'
    Set-Content -Path $_.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($_.Name)"
}

# Fix package declarations in madness_project
$madnessPath = "C:\Users\MOBILE ISLAND\AndroidStudioProjects\Slotify\app\src\main\java\com\example\slotify\madness_project"
Get-ChildItem -Path $madnessPath -Recurse -Filter *.kt | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.madness_project', 'package com.example.slotify.madness_project'
    $content = $content -replace 'import com\.example\.madness_project', 'import com.example.slotify.madness_project'
    Set-Content -Path $_.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($_.Name)"
}

# Fix package declarations in lab8
$lab8Path = "C:\Users\MOBILE ISLAND\AndroidStudioProjects\Slotify\app\src\main\java\com\example\slotify\lab8"
Get-ChildItem -Path $lab8Path -Recurse -Filter *.kt | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.lab8', 'package com.example.slotify.lab8'
    $content = $content -replace 'import com\.example\.lab8', 'import com.example.slotify.lab8'
    Set-Content -Path $_.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($_.Name)"
}

Write-Host "`nPackage declarations fixed successfully!"
